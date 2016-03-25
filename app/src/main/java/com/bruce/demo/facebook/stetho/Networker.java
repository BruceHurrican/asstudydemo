/*
 * Copyright (c) 2014-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.bruce.demo.facebook.stetho;

import com.facebook.stetho.urlconnection.ByteArrayRequestEntity;
import com.facebook.stetho.urlconnection.SimpleRequestEntity;
import com.facebook.stetho.urlconnection.StethoURLConnectionManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.zip.GZIPInputStream;

import javax.annotation.Nullable;

/**
 * Very simple centralized network middleware for illustration purposes.
 */
public class Networker {
    private static final int READ_TIMEOUT_MS = 10000;
    private static final int CONNECT_TIMEOUT_MS = 15000;
    private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    private static final String GZIP_ENCODING = "gzip";
    private static Networker sInstance;
    private final Executor sExecutor = Executors.newFixedThreadPool(4);

    private Networker() {
    }

    public static synchronized Networker get() {
        if (sInstance == null) {
            sInstance = new Networker();
        }
        return sInstance;
    }

    private static void requestDecompression(HttpURLConnection conn) {
        conn.setRequestProperty(HEADER_ACCEPT_ENCODING, GZIP_ENCODING);
    }

    @Nullable
    private static InputStream applyDecompressionIfApplicable(HttpURLConnection conn, @Nullable InputStream in) throws IOException {
        if (in != null && GZIP_ENCODING.equals(conn.getContentEncoding())) {
            return new GZIPInputStream(in);
        }
        return in;
    }

    private static void copy(InputStream in, OutputStream out, byte[] buf) throws IOException {
        if (in == null) {
            return;
        }
        int n;
        while ((n = in.read(buf)) != -1) {
            out.write(buf, 0, n);
        }
    }

    public void submit(HttpRequest request, Callback callback) {
        sExecutor.execute(new HttpRequestTask(request, callback));
    }

    public enum HttpMethod {
        GET, POST
    }

    public interface Callback {
        void onResponse(HttpResponse result);

        void onFailure(IOException e);
    }

    public static class HttpRequest {
        public final String friendlyName;
        public final HttpMethod method;
        public final String url;
        public final byte[] body;

        HttpRequest(Builder b) {
            if (b.method == HttpMethod.POST) {
                if (b.body == null) {
                    throw new IllegalArgumentException("POST must have a body");
                }
            } else if (b.method == HttpMethod.GET) {
                if (b.body != null) {
                    throw new IllegalArgumentException("GET cannot have a body");
                }
            }
            this.friendlyName = b.friendlyName;
            this.method = b.method;
            this.url = b.url;
            this.body = b.body;
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public static class Builder {
            private String friendlyName;
            private HttpMethod method;
            private String url;
            private byte[] body = null;

            Builder() {
            }

            public Builder friendlyName(String friendlyName) {
                this.friendlyName = friendlyName;
                return this;
            }

            public Builder method(HttpMethod method) {
                this.method = method;
                return this;
            }

            public Builder url(String url) {
                this.url = url;
                return this;
            }

            public Builder body(byte[] body) {
                this.body = body;
                return this;
            }

            public HttpRequest build() {
                return new HttpRequest(this);
            }
        }
    }

    public static class HttpResponse {
        public final int statusCode;
        public final byte[] body;

        HttpResponse(int statusCode, byte[] body) {
            this.statusCode = statusCode;
            this.body = body;
        }
    }

    private class HttpRequestTask implements Runnable {
        private final HttpRequest request;
        private final Callback callback;
        private final StethoURLConnectionManager stethoManager;

        public HttpRequestTask(HttpRequest request, Callback callback) {
            this.request = request;
            this.callback = callback;
            stethoManager = new StethoURLConnectionManager(request.friendlyName);
        }

        @Override
        public void run() {
            try {
                HttpResponse response = doFetch();
                callback.onResponse(response);
            } catch (IOException e) {
                callback.onFailure(e);
            }
        }

        private HttpResponse doFetch() throws IOException {
            HttpURLConnection conn = configureAndConnectRequest();
            try {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                InputStream rawStream = conn.getInputStream();
                try {
                    // Let Stetho see the raw, possibly compressed stream.
                    rawStream = stethoManager.interpretResponseStream(rawStream);
                    InputStream decompressedStream = applyDecompressionIfApplicable(conn, rawStream);
                    if (decompressedStream != null) {
                        copy(decompressedStream, out, new byte[1024]);
                    }
                } finally {
                    if (rawStream != null) {
                        rawStream.close();
                    }
                }
                return new HttpResponse(conn.getResponseCode(), out.toByteArray());
            } finally {
                conn.disconnect();
            }
        }

        private HttpURLConnection configureAndConnectRequest() throws IOException {
            URL url = new URL(request.url);

            // Note that this does not actually create a new connection so it is appropriate to
            // defer preConnect until after the HttpURLConnection instance is configured.  Do not
            // invoke connect, conn.getInputStream, conn.getOutputStream, etc before calling
            // preConnect!
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            try {
                conn.setReadTimeout(READ_TIMEOUT_MS);
                conn.setConnectTimeout(CONNECT_TIMEOUT_MS);
                conn.setRequestMethod(request.method.toString());

                // Adding this disables transparent gzip compression so that we can intercept
                // the raw stream and display the correct response body size.
                requestDecompression(conn);

                SimpleRequestEntity requestEntity = null;
                if (request.body != null) {
                    requestEntity = new ByteArrayRequestEntity(request.body);
                }

                stethoManager.preConnect(conn, requestEntity);
                try {
                    if (request.method == HttpMethod.POST) {
                        if (requestEntity == null) {
                            throw new IllegalStateException("POST requires an entity");
                        }
                        conn.setDoOutput(true);

                        requestEntity.writeTo(conn.getOutputStream());
                    }

                    // Ensure that we are connected after this point.  Note that getOutputStream above will
                    // also connect and exchange HTTP messages.
                    conn.connect();

                    stethoManager.postConnect();

                    return conn;
                } catch (IOException inner) {
                    // This must only be called after preConnect.  Failures before that cannot be
                    // represented since the request has not yet begun according to Stetho.
                    stethoManager.httpExchangeFailed(inner);
                    throw inner;
                }
            } catch (IOException outer) {
                conn.disconnect();
                throw outer;
            }
        }
    }
}
