/*
 * BruceHurrican
 * Copyright (c) 2016.
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 *    This document is Bruce's individual learning the android demo, wherein the use of the code from the Internet, only to use as a learning exchanges.
 *    And where any person can download and use, but not for commercial purposes.
 *    Author does not assume the resulting corresponding disputes.
 *    If you have good suggestions for the code, you can contact BurrceHurrican@foxmail.com
 *    本文件为Bruce's个人学习android的作品, 其中所用到的代码来源于互联网，仅作为学习交流使用。
 *    任和何人可以下载并使用, 但是不能用于商业用途。
 *    作者不承担由此带来的相应纠纷。
 *    如果对本代码有好的建议，可以联系BurrceHurrican@foxmail.com
 */

package com.bruce.demo.net;

import com.bruceutils.utils.logdetails.LogDetails;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by BruceHurrican on 17/1/5.
 */

public class RetrofitDemo {
    public void doRetrofit() {
        String url = "http://10.180.184.14:8080";
        Retrofit retrofit = new Retrofit.Builder().baseUrl(url).addConverterFactory
                (GsonConverterFactory.create()).client(new OkHttpClient.Builder().addInterceptor
                (new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder().addHeader("a1", "A").addHeader
                        ("device", "mac").build();
                return chain.proceed(request);
            }
        }).build()).build();
        Call<ApkModel> call = retrofit.create(IGetData.class).getData();
        call.enqueue(new Callback<ApkModel>() {
            @Override
            public void onResponse(Call<ApkModel> call, Response<ApkModel> response) {
                LogDetails.d("call is cancelled ? " + call.isCanceled());
                LogDetails.d(response);
            }

            @Override
            public void onFailure(Call<ApkModel> call, Throwable t) {
                LogDetails.d("call is cancelled ? " + call.isCanceled());
                LogDetails.d("网络请求失败");
            }
        });
    }

    public void doRx() {
        String url = "http://10.180.184.14:8080";
        Retrofit retrofit = new Retrofit.Builder().baseUrl(url).addConverterFactory
                (GsonConverterFactory.create()).addCallAdapterFactory(RxJavaCallAdapterFactory
                .create()).client(new OkHttpClient.Builder().addInterceptor(new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request request = chain.request().newBuilder().addHeader("a1", "A").addHeader
                        ("device", "mac").build();
                return chain.proceed(request);
            }
        }).build()).build();
        APIService apiService = retrofit.create(APIService.class);
        apiService.getData().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread
                ()).subscribe(new Subscriber<ApkModel>() {
            @Override
            public void onCompleted() {
                LogDetails.d("请求成功");
            }

            @Override
            public void onError(Throwable throwable) {
                LogDetails.d("失败");

            }

            @Override
            public void onNext(ApkModel apkModel) {
                LogDetails.d("获取数据");
                LogDetails.d(apkModel);

            }
        });
    }
}
