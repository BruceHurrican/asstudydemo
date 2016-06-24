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
 *   This document is Bruce's individual learning the android demo, wherein the use of the code from the Internet, only to use as a learning exchanges.
 *   And where any person can download and use, but not for commercial purposes.
 *   Author does not assume the resulting corresponding disputes.
 *   If you have good suggestions for the code, you can contact BurrceHurrican@foxmail.com
 *   本文件为Bruce's个人学习android的demo, 其中所用到的代码来源于互联网，仅作为学习交流使用。
 *   任和何人可以下载并使用, 但是不能用于商业用途。
 *   作者不承担由此带来的相应纠纷。
 *   如果对本代码有好的建议，可以联系BurrceHurrican@foxmail.com
 */

package com.bruce.demo.studydata.fragments.widgetdemo;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.bruce.demo.base.BaseFragment;
import com.bruceutils.utils.LogUtils;

/**
 * 自定义键盘原理实现
 * Created by hrk on 16/5/27.
 */
public class DYIKeyBoardFragment extends BaseFragment {
    @Override
    public String getTAG() {
        return DYIKeyBoardFragment.class.getSimpleName();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        RelativeLayout relativeLayout = new RelativeLayout(getActivity());
        final WebView webView = new WebView(getActivity());
        final EditText editText = new EditText(getActivity());
        final Button button = new Button(getActivity());

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        relativeLayout.setLayoutParams(params);

        editText.setId(editText.generateViewId());
        RelativeLayout.LayoutParams etParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        etParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        editText.setLayoutParams(etParams);
        editText.setGravity(Gravity.CENTER);
        editText.setInputType(InputType.TYPE_NULL);
        relativeLayout.addView(editText);

        button.setId(button.generateViewId());
        RelativeLayout.LayoutParams btnParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        btnParams.addRule(RelativeLayout.BELOW, editText.getId());
        button.setLayoutParams(btnParams);
        button.setText("夺取 editText 的焦点");
        relativeLayout.addView(button);

        RelativeLayout.LayoutParams wvParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        wvParams.addRule(RelativeLayout.BELOW, button.getId());
        webView.setLayoutParams(wvParams);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        relativeLayout.addView(webView);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                button.setFocusableInTouchMode(true);
                button.setFocusable(true);
            }
        });
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                LogUtils.i("editText 焦点发生改变,当前是否获取焦点？" + hasFocus);
                if (hasFocus) {
                    // 此处可以定义弹出自定义键盘,原生或者web版本的都可以
//                    webView.setVisibility(View.VISIBLE);
                    webView.loadUrl("https://github.com/brucehurrican");
                } else {
//                    webView.setVisibility(View.GONE);
                    webView.loadUrl("http://www.jianshu.com/");
                }
            }
        });
        return relativeLayout;
    }
}
