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
        webView.setWebViewClient(new WebViewClient());
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
                    webView.setVisibility(View.VISIBLE);
                    webView.loadUrl("https://github.com/brucehurrican");
                } else {
                    webView.setVisibility(View.GONE);
                }
            }
        });
        return relativeLayout;
    }
}
