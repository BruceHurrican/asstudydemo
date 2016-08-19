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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bruce.demo.R;
import com.bruceutils.utils.LogUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by BruceHurrican on 2016/5/24.
 */
public class FWBigView extends LinearLayout {
    /**
     * 记录大悬浮窗的宽度
     */
    public static int viewWidth;

    /**
     * 记录大悬浮窗的高度
     */
    public static int viewHeight;
    /**
     * 监听HOME按键
     */
    private HomeWatcherReceiver homeWatcherReceiver;
    private Context mContext;

    public FWBigView(final Context context) {
        super(context);
        this.mContext = context;
        LayoutInflater.from(context).inflate(R.layout.fw_view_big, this);
        View view = findViewById(R.id.rl_container);
        viewWidth = view.getLayoutParams().width;
        viewHeight = view.getLayoutParams().height;
        View view2 = findViewById(R.id.ll_container);
        Button btn_close = (Button) findViewById(R.id.btn_close);
        Button btn_back = (Button) findViewById(R.id.btn_back);
        btn_close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击关闭悬浮窗的时候，移除所有悬浮窗，并停止Service
                FWmanager.removeBigWindow(context);
                FWmanager.removeSmallWindow(context);
                Intent intent = new Intent(getContext(), FWService.class);
                context.stopService(intent);
//                SharedPreferencesUtil.saveString(context,"kk1","aa1");
//                Toast.makeText(context, "kk1", Toast.LENGTH_SHORT).show();
            }
        });
        btn_back.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击返回的时候，移除大悬浮窗，创建小悬浮窗
                FWmanager.removeBigWindow(context);
                FWmanager.createSmallWindow(context);
//                LogDetails.i(SharedPreferencesUtil.getString(context,"kk1","212"));
//                Toast.makeText(context, SharedPreferencesUtil.getString(context, "kk1", "212"), Toast.LENGTH_SHORT).show();
            }
        });
        view.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                FWmanager.removeBigWindow(context);
                FWmanager.createSmallWindow(context);
                return true;
            }
        });
        view2.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        homeWatcherReceiver = new HomeWatcherReceiver();

        if (null == toast) {
            toast = new Toast(context.getApplicationContext());
        }
    }

    public void registerHomeRec(){
        mContext.registerReceiver(homeWatcherReceiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));
    }

    private class HomeWatcherReceiver extends BroadcastReceiver {
        private static final String LOG_TAG = "HomeReceiver";
        private static final String SYSTEM_DIALOG_REASON_KEY = "reason";
        private static final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
        private static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";
        private static final String SYSTEM_DIALOG_REASON_LOCK = "lock";
        private static final String SYSTEM_DIALOG_REASON_ASSIST = "assist";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LogUtils.i(LOG_TAG + "onReceive: action: " + action);
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                // android.intent.action.CLOSE_SYSTEM_DIALOGS
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                LogUtils.i(LOG_TAG + "reason: " + reason);

                if (SYSTEM_DIALOG_REASON_HOME_KEY.equals(reason)) {
                    // 短按Home键
                    LogUtils.i(LOG_TAG + "homekey");

                } else if (SYSTEM_DIALOG_REASON_RECENT_APPS.equals(reason)) {
                    // 长按Home键 或者 activity切换键
                    LogUtils.i(LOG_TAG + "long press home key or activity switch");

                } else if (SYSTEM_DIALOG_REASON_LOCK.equals(reason)) {
                    // 锁屏
                    LogUtils.i(LOG_TAG + "lock");
                } else if (SYSTEM_DIALOG_REASON_ASSIST.equals(reason)) {
                    // samsung 长按Home键
                    LogUtils.i(LOG_TAG + "assist");
                }

            }
            FWmanager.removeBigWindow(context);
            FWmanager.createSmallWindow(context);
            context.unregisterReceiver(homeWatcherReceiver);
        }

    }

    private boolean isShow = false;
    public void show(){
        if (isShow) {
            return;
        }
        toast.setView(this);
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

    private Toast toast;
    private Object mTN; // Toast 内部类 TN
    private Method show; // Toast show() 方法
    private Method hide; // Toast hide() 方法
    private WindowManager.LayoutParams mParams;
    private WindowManager windowManager;
    private void initTN() {
        windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
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
//                mParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
//                        | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                /**设置动画*/
                mParams.windowAnimations = android.R.anim.fade_in;
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT){
                    mParams.type = WindowManager.LayoutParams.TYPE_PHONE;
                }
                mParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
                mParams.format = PixelFormat.RGBA_8888;
                mParams.gravity = Gravity.LEFT | Gravity.TOP;
                mParams.x = screenWidth / 2 - viewWidth / 2;
                mParams.y = screenHeight / 2 - viewHeight / 2;
                mParams.width = viewWidth;
                mParams.height = viewHeight;
            }
            /**调用tn.show()之前一定要先设置mNextView*/
            Field tnNextViewField = mTN.getClass().getDeclaredField("mNextView");
            tnNextViewField.setAccessible(true);
            tnNextViewField.set(mTN, toast.getView());
//            windowManager = (WindowManager)getContext().getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        toast.setGravity(mParams.gravity, mParams.x ,mParams.y);
    }
}
