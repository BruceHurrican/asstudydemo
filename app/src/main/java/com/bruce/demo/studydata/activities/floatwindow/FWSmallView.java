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

package com.bruce.demo.studydata.activities.floatwindow;

import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bruce.demo.R;
import com.bruceutils.utils.LogUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by BruceHurrican on 2016/5/24.
 */
public class FWSmallView extends LinearLayout {
    /**
     * 记录小悬浮窗的宽度
     */
    public static int viewWidth;
    /**
     * 记录小悬浮窗的高度
     */
    public static int viewHeight;
    /**
     * 记录系统状态栏的高度
     */
    private static int statusBarHeight;
    private final View view;
    /**
     * 用于更新小悬浮窗的位置
     */
    private WindowManager windowManager;

    /**
     * 小悬浮窗的参数
     */
    private WindowManager.LayoutParams mParams;

    /**
     * 记录当前手指位置在屏幕上的横坐标值
     */
    private float xInScreen;

    /**
     * 记录当前手指位置在屏幕上的纵坐标值
     */
    private float yInScreen;

    /**
     * 记录手指按下时在屏幕上的横坐标的值
     */
    private float xDownInScreen;

    /**
     * 记录手指按下时在屏幕上的纵坐标的值
     */
    private float yDownInScreen;

    /**
     * 记录手指按下时在小悬浮窗的View上的横坐标的值
     */
    private float xInView;

    /**
     * 记录手指按下时在小悬浮窗的View上的纵坐标的值
     */
    private float yInView;
    private boolean isShow = false;
    private Toast toast;
    private Object mTN; // Toast 内部类 TN
    private Method show; // Toast show() 方法
    private Method hide; // Toast hide() 方法

    public FWSmallView(Context context) {
        super(context);
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        LayoutInflater.from(context).inflate(R.layout.fw_view_small, this);
        view = findViewById(R.id.ll_container);
        viewWidth = view.getLayoutParams().width;
        viewHeight = view.getLayoutParams().height;
        if (null == toast) {
            toast = new Toast(context.getApplicationContext());
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // 手指按下时记录必要数据,纵坐标的值都需要减去状态栏高度
                xInView = event.getX();
                yInView = event.getY();
                xDownInScreen = event.getRawX();
                yDownInScreen = event.getRawY() - getStatusBarHeight();
                xInScreen = event.getRawX();
                yInScreen = event.getRawY() - getStatusBarHeight();
                break;
            case MotionEvent.ACTION_MOVE:
                xInScreen = event.getRawX();
                yInScreen = event.getRawY() - getStatusBarHeight();
                // 手指移动的时候更新小悬浮窗的位置
                updateViewPosition();
                break;
            case MotionEvent.ACTION_UP:
                // 如果手指离开屏幕时，xDownInScreen和xInScreen相等，且yDownInScreen和yInScreen相等，则视为触发了单击事件。
                if ((xDownInScreen == xInScreen) && (yDownInScreen == yInScreen)) {
                    openBigWindow();
                } else if (Math.abs(xDownInScreen - xInScreen) <= 10 && Math.abs(yDownInScreen - yInScreen) <= 10) {
                    openBigWindow();
                }
                LogUtils.i("isShow->"+FWmanager.isWindowShowing());
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 将小悬浮窗的参数传入，用于更新小悬浮窗的位置。
     *
     * @param params 小悬浮窗的参数
     */
    public void setParams(WindowManager.LayoutParams params) {
        mParams = params;
    }

    /**
     * 更新小悬浮窗在屏幕中的位置。
     */
    private void updateViewPosition() {
        mParams.x = (int) (xInScreen - xInView);
        mParams.y = (int) (yInScreen - yInView);
        windowManager.updateViewLayout(this, mParams);
    }

    /**
     * 打开大悬浮窗，同时关闭小悬浮窗。
     */
    private void openBigWindow() {
        FWmanager.createBigWindow(getContext());
        FWmanager.removeSmallWindow(getContext());
    }

    /**
     * 用于获取状态栏的高度。
     *
     * @return 返回状态栏高度的像素值。
     */
    private int getStatusBarHeight() {
        if (statusBarHeight == 0) {
            try {
                Class<?> c = Class.forName("com.android.internal.R$dimen");
                Object o = c.newInstance();
                Field field = c.getField("status_bar_height");
                int x = (Integer) field.get(o);
                statusBarHeight = getResources().getDimensionPixelSize(x);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusBarHeight;
    }

    public void show(){
        if (isShow) {
            return;
        }
//        TextView tv = (TextView) findViewById(R.id.percent);
//        tv.setText("悬浮窗");
        toast.setView(this);
        /** 此处解决部分三星手机的触摸事件 {@link #onTouchEvent}没有响应的bug */
        view.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // 手指按下时记录必要数据,纵坐标的值都需要减去状态栏高度
                        xInView = event.getX();
                        yInView = event.getY();
                        xDownInScreen = event.getRawX();
                        yDownInScreen = event.getRawY() - getStatusBarHeight();
                        xInScreen = event.getRawX();
                        yInScreen = event.getRawY() - getStatusBarHeight();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        xInScreen = event.getRawX();
                        yInScreen = event.getRawY() - getStatusBarHeight();
                        // 手指移动的时候更新小悬浮窗的位置
                        updateViewPosition();
                        break;
                    case MotionEvent.ACTION_UP:
                        // 如果手指离开屏幕时，xDownInScreen和xInScreen相等，且yDownInScreen和yInScreen相等，则视为触发了单击事件。
                        if ((xDownInScreen == xInScreen) && (yDownInScreen == yInScreen)) {
                            openBigWindow();
                        } else if (Math.abs(xDownInScreen - xInScreen) <= 10 && Math.abs(yDownInScreen - yInScreen)
                                <= 10) {
                            openBigWindow();
                        }
                        LogUtils.i("isShow->" + FWmanager.isWindowShowing());
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        initTN();
        try {
            show.invoke(mTN);
        } catch (Exception e) {
            e.printStackTrace();
        }
        isShow = true;
    }

    public void hide(){
        if(!isShow) {
            return;
        }
        post(new Runnable() {
            @Override
            public void run() {
                try {
                    hide.invoke(mTN);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                isShow = false;
            }
        });
    }

    private void initTN() {
        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        int screenHeight = windowManager.getDefaultDisplay().getHeight();
        try {
            Field tnField = toast.getClass().getDeclaredField("mTN");
            tnField.setAccessible(true);
            mTN = tnField.get(toast);
            show = mTN.getClass().getMethod("show");
            hide = mTN.getClass().getMethod("hide");

            Field tnParamsField = mTN.getClass().getDeclaredField("mParams");
            tnParamsField.setAccessible(true);
            if (null == mParams) {
                mParams = (WindowManager.LayoutParams) tnParamsField.get(mTN);
                mParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                /**设置动画*/
                mParams.windowAnimations = android.R.anim.fade_in;
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT){
                    mParams.type = WindowManager.LayoutParams.TYPE_PHONE;
                }
                mParams.x = screenWidth;
                mParams.y = screenHeight / 2;
            }

            /**调用tn.show()之前一定要先设置mNextView*/
            Field tnNextViewField = mTN.getClass().getDeclaredField("mNextView");
            tnNextViewField.setAccessible(true);
            tnNextViewField.set(mTN, toast.getView());

//            windowManager = (WindowManager)getContext().getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        toast.setGravity(Gravity.LEFT | Gravity.TOP,mParams.x ,mParams.y);
    }
}
