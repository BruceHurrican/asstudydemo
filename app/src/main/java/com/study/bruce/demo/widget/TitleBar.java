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

package com.study.bruce.demo.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.study.bruce.demo.R;

/**
 * 自定义标题栏
 * Created by BruceHurrican on 2015/12/1.
 */
public class TitleBar extends RelativeLayout implements View.OnClickListener {
    private TextView tv_left, tv_right, tv_center;
    private ImageView iv_left, iv_right;
    private Context context;
    private OnTitleBarClickListener titleBarClickListener;

    public TitleBar(Context context) {
        super(context);
        this.context = context;
        initViews();
    }

    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initViews();
    }

    private void initViews() {
        // 获取 LayoutInflater 实例的两种方法
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.view_widget_titlebar, this);
        tv_left = (TextView) findViewById(R.id.tv_left);
        tv_right = (TextView) findViewById(R.id.tv_right);
        tv_center = (TextView) findViewById(R.id.tv_center);
        iv_left = (ImageView) findViewById(R.id.iv_left);
        iv_right = (ImageView) findViewById(R.id.iv_right);
        bindListener();
    }

    private void bindListener() {
        tv_left.setOnClickListener(this);
        tv_right.setOnClickListener(this);
        iv_left.setOnClickListener(this);
        iv_right.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_left:
            case R.id.iv_left:
                titleBarClickListener.onLeftBtnClick();
                break;
            case R.id.tv_right:
            case R.id.iv_right:
                titleBarClickListener.onRightBtnClick();
                break;
        }
    }

    /**
     * 设置标题
     *
     * @param title 标题内容
     */
    public void setTilte(CharSequence title) {
        tv_center.setText(title);
    }

    /**
     * 设置左右按钮文本内容
     *
     * @param leftText  左按钮文本
     * @param rightText 右按钮文本
     */
    public void setBtnText(CharSequence leftText, CharSequence rightText) {
        tv_left.setText(leftText);
        tv_right.setText(rightText);
    }

    /**
     * 设置按钮图片样式
     *
     * @param leftImgResID  左按钮图片资源ID
     * @param rightImgResID 右按钮图片资源ID
     */
    public void setBtnImg(int leftImgResID, int rightImgResID) {
        iv_left.setImageResource(leftImgResID);
        iv_right.setImageResource(rightImgResID);
    }

    public void setOnTitleBarClickListener(OnTitleBarClickListener listener) {
        titleBarClickListener = listener;
    }

    /**
     * 按钮接听接口
     */
    public interface OnTitleBarClickListener {
        void onLeftBtnClick();

        void onRightBtnClick();
    }
}
