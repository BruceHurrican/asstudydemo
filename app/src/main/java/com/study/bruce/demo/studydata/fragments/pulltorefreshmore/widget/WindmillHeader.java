/*
 * Copyright (c) 2015.
 *   This document is Bruce's individual learning the android demo, wherein the use of the code from the Internet, only to use as a learning exchanges.
 *   And where any person can download and use, but not for commercial purposes.
 *   Author does not assume the resulting corresponding disputes.
 *   If you have good suggestions for the code, you can contact BurrceHurrican@foxmail.com
 *   本文件为Bruce's个人学习android的demo, 其中所用到的代码来源于互联网，仅作为学习交流使用。
 *   任和何人可以下载并使用, 但是不能用于商业用途。
 *   作者不承担由此带来的相应纠纷。
 *   如果对本代码有好的建议，可以联系BurrceHurrican@foxmail.com
 */

package com.study.bruce.demo.studydata.fragments.pulltorefreshmore.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.study.bruce.demo.R;

/**
 * Created by BruceHurrican on 2015/12/20.
 */
public class WindmillHeader extends FrameLayout {
    private LayoutInflater inflater;
    // 下拉刷新头部视图
    private ViewGroup headView;
    // 下拉刷新文字
    private TextView tvHeadTitle;
    // 下拉刷新图标
    private ImageView ivWindmill;
    private WindmillDrawable drawable;

    public WindmillHeader(Context context) {
        this(context, null);
    }

    public WindmillHeader(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WindmillHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflater = LayoutInflater.from(context);
        // 头部
        headView = (ViewGroup) inflater.inflate(R.layout.ptfm_windmill_header, this, true);
        ivWindmill = (ImageView) headView.findViewById(R.id.iv_windmill);
        tvHeadTitle = (TextView) headView.findViewById(R.id.tv_head_title);
        drawable = new WindmillDrawable(context, ivWindmill);
        ivWindmill.setImageDrawable(drawable);
    }

    onUIR
}
