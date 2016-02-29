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

package com.bruce.demo.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bruce.demo.R;

/**
 * 自定义 TitleBar
 * Created by BruceHurrican on 2016/2/11.
 */
public class TitleBar2 extends RelativeLayout {
    TitleBarClickListener titleBarClickListener;
    private int titleTextColor, leftTextColor, rightTextColor;
    private Drawable leftBackground, rightBackground, titleBarBackground;
    private String titleText, leftText, rightText;
    private float titleTextSize, leftTextSize, rightTextSize;
    private Context context;

    public TitleBar2(Context context) {
        this(context, null);
    }

    public TitleBar2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleBar2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        // 将 attrs.xml 中定义的所有属性值储存到 TypedArray 中
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TitleBar2);
        titleTextColor = ta.getColor(R.styleable.TitleBar2_titleTextColor2, 0);
        leftTextColor = ta.getColor(R.styleable.TitleBar2_leftTextColor, 0);
        rightTextColor = ta.getColor(R.styleable.TitleBar2_rightTextColor, 0);
        leftBackground = ta.getDrawable(R.styleable.TitleBar2_leftBackground);
        rightBackground = ta.getDrawable(R.styleable.TitleBar2_rightBackground);
        titleBarBackground = ta.getDrawable(R.styleable.TitleBar2_titleBarBackground);
        titleText = ta.getString(R.styleable.TitleBar2_titleText);
        leftText = ta.getString(R.styleable.TitleBar2_leftText);
        rightText = ta.getString(R.styleable.TitleBar2_rightText);
        titleTextSize = ta.getDimension(R.styleable.TitleBar2_titleTextSize, 10);
        leftTextSize = ta.getDimension(R.styleable.TitleBar2_leftTextSize, 10);
        rightTextSize = ta.getDimension(R.styleable.TitleBar2_rightTextSize, 10);
        ta.recycle();
        initView();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void initView() {
        Button btn_left = new Button(context);
        Button btn_right = new Button(context);
        TextView tv_title = new TextView(context);

        RelativeLayout.LayoutParams titleParams = new LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        titleParams.addRule(CENTER_IN_PARENT, TRUE);
        RelativeLayout.LayoutParams leftParams = new LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        leftParams.addRule(ALIGN_PARENT_LEFT, TRUE);
        RelativeLayout.LayoutParams rightParams = new LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        rightParams.addRule(ALIGN_PARENT_RIGHT, TRUE);

        tv_title.setText(titleText);
        tv_title.setTextSize(titleTextSize);
        tv_title.setTextColor(titleTextColor);
        tv_title.setGravity(Gravity.CENTER);

        btn_left.setText(leftText);
        btn_left.setTextSize(leftTextSize);
        btn_left.setTextColor(leftTextColor);
        btn_left.setBackground(leftBackground);

        btn_right.setText(rightText);
        btn_right.setTextSize(rightTextSize);
        btn_right.setTextColor(rightTextColor);
        btn_right.setBackground(rightBackground);

        setBackground(titleBarBackground);

        addView(tv_title, titleParams);
        addView(btn_left, leftParams);
        addView(btn_right, rightParams);


        btn_left.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                titleBarClickListener.onLeftClick(v);
            }
        });

        btn_right.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                titleBarClickListener.onRightClick(v);
            }
        });
    }

    public void setOnTitleBarClickListener(TitleBarClickListener listener) {
        titleBarClickListener = listener;
    }

    public interface TitleBarClickListener {
        void onLeftClick(View view);

        void onRightClick(View view);
    }
}
