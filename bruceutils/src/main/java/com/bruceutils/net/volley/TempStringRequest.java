/*
 * BruceHurrican
 * Copyright (c) 2016.
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *
 *    This document is Bruce's individual learning the android demo, wherein the use of the code from the Internet, only to use as a learning exchanges.
 *   And where any person can download and use, but not for commercial purposes.
 *   Author does not assume the resulting corresponding disputes.
 *   If you have good suggestions for the code, you can contact BurrceHurrican@foxmail.com
 *   本文件为Bruce's个人学习android的demo, 其中所用到的代码来源于互联网，仅作为学习交流使用。
 *   任和何人可以下载并使用, 但是不能用于商业用途。
 *   作者不承担由此带来的相应纠纷。
 *   如果对本代码有好的建议，可以联系BurrceHurrican@foxmail.com
 */

package com.bruceutils.net.volley;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.Map;

/**
 * 用于解决 内存泄漏 需要配合其他类使用
 * Created by BruceHurrican on 2016/2/18.
 */
public class TempStringRequest extends StringRequest {
    private VolleyRequestListener volleyRequestListener;
    private Map<String, String> params;

    public TempStringRequest(int method, String url, Map<String, String> params, final VolleyRequestListener listener) {
        super(method, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                listener.onSuccessListener(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onErrorListener(error);
            }
        });
        this.volleyRequestListener = listener;
        this.params = params;
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }
}
