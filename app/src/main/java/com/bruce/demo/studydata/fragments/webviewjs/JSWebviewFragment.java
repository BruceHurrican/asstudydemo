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

package com.bruce.demo.studydata.fragments.webviewjs;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.bruce.demo.base.BaseFragment;
import com.bruceutils.utils.LogUtils;

/**
 * webview JS 交互
 * Created by BruceHurrican on 2016/1/13.
 */
public class JSWebviewFragment extends BaseFragment {

    private WebView webView;

    @Override
    public String getTAG() {
        return "JSWebviewFragment";
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /*
         用全局 applicationContext 避免内存泄漏,如此则如果要显示 JS 中的 alert 需要重写 onJsAlert() 否则报错
         错误信息
         JsDialogHelper: Cannot create a dialog, the WebView context is not an Activity.
         java.lang.ClassCastException: com.bruce.demo.DemoApplication cannot be cast to android.app.Activity
         */
        WebView webView = new WebView(getActivity().getApplicationContext());
        webView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("提示:").setMessage(message);
                builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LogUtils.d("js对话框按钮 ok 被点击");
                        result.confirm();
                    }
                });
                builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        LogUtils.d("js对话框按钮 cancel 被点击");
                        result.cancel();
                    }
                });
                builder.setCancelable(false);
                builder.create();
                builder.show();
                return true;
            }
        }); // 允许弹出 alert

        webView.addJavascriptInterface(this, "jswebview");
        webView.loadUrl("file:///android_asset/test.html");
        return webView;
    }

    @Override
    public void onPause() {
        if (null != webView) {
            webView.onPause();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        /*
         最优解决方案为在调用 WebView 的 Activity onDestroy() 时调用 webView.destroy();
         并将该 Activity 所在进程销毁,调用 android.os.Process.killProcess(android.os.Process.myPid()); 实现
         */
        if (null != webView) {
            webView.destroy();
        }
        super.onDestroy();
    }

    @JavascriptInterface
    public void androidMethod(String txt) {
        showToastShort(txt);
        LogUtils.d(txt);
    }
}
