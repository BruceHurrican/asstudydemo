/*
 *   BruceHurrican
 *   Copyright (c) 2016.
 *   Licensed under the Apache License, Version 2.0 (the "License");
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

package com.bruce.demo.studydata.activities.floatwindow;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Build;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bruce.demo.R;
import com.bruceutils.utils.LogUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by BruceHurrican on 16/8/17.
 */
public class MiToast {
    private static final String TAG = "ExToast";

    public static final int LENGTH_ALWAYS = 0;
    public static final int LENGTH_SHORT = 2;
    public static final int LENGTH_LONG = 4;

    private Toast toast;
    private Context mContext;
    private int mDuration = LENGTH_SHORT;
    private int animations = -1;
    private boolean isShow = false;

    private Object mTN;
    private Method show;
    private Method hide;
    private WindowManager mWM;
    private WindowManager.LayoutParams params;
    private View mSmallView, mBigView;

    private float mTouchStartX;
    private float mTouchStartY;
    private float x;
    private float y;

    private SmallViewTouchListener smallViewTouchListener = new SmallViewTouchListener();

    private Handler handler = new Handler();

    public MiToast(final Context context){
        this.mContext = context;
        if (toast == null) {
            toast = new Toast(mContext);
        }
        LayoutInflater inflate = (LayoutInflater)
                mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mSmallView = inflate.inflate(R.layout.fw_view_small, null);
        mSmallView.setOnTouchListener(smallViewTouchListener);


        mBigView = inflate.inflate(R.layout.fw_view_big, null);
        View view = mBigView.findViewById(R.id.rl_container);
//        viewWidth = view.getLayoutParams().width;
//        viewHeight = view.getLayoutParams().height;
        View view2 = mBigView.findViewById(R.id.ll_container);
        Button btn_close = (Button) mBigView.findViewById(R.id.btn_close);
        Button btn_back = (Button) mBigView.findViewById(R.id.btn_back);
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击关闭悬浮窗的时候，移除所有悬浮窗，并停止Service
                FWmanager.removeBigWindow(context);
                FWmanager.removeSmallWindow(context);
//                Intent intent = new Intent(context, FWService.class);
//                context.stopService(intent);
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击返回的时候，移除大悬浮窗，创建小悬浮窗
                FWmanager.removeBigWindow(context);
                FWmanager.createSmallWindow(context);
//                LogDetails.i(SharedPreferencesUtil.getString(context,"kk1","212"));
//                Toast.makeText(context, SharedPreferencesUtil.getString(context, "kk1", "212"), Toast.LENGTH_SHORT).show();
            }
        });
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                FWmanager.removeBigWindow(context);
                FWmanager.createSmallWindow(context);
                return true;
            }
        });
        view2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        homeWatcherReceiver = new HomeWatcherReceiver();
        context.registerReceiver(homeWatcherReceiver, new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));

    }

    private Runnable hideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    /**
     * Show the view for the specified duration.
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void show(){
        if (isShow) {
            return;
        }
        TextView tv = (TextView) mSmallView.findViewById(R.id.percent);
        tv.setText("悬浮窗");
        toast.setView(mSmallView);
        initTN();
        try {
            show.invoke(mTN);
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        isShow = true;
        //判断duration，如果大于#LENGTH_ALWAYS 则设置消失时间
//        if (mDuration > LENGTH_ALWAYS) {
//            mSmallView.postDelayed(hideRunnable, mDuration * 1000);
//        }
    }

    /**
     * Close the view if it's showing, or don't show it if it isn't showing yet.
     * You do not normally have to call this.  Normally view will disappear on its own
     * after the appropriate duration.
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void hide(){
        if(!isShow) return;
        try {
            hide.invoke(mTN);
        } catch (InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        isShow = false;
    }

    public void setView(View view) {
        toast.setView(view);
    }

    public View getView() {
        return toast.getView();
    }

    /**
     * Set how long to show the view for.
     * @see #LENGTH_SHORT
     * @see #LENGTH_LONG
     * @see #LENGTH_ALWAYS
     */
    public void setDuration(int duration) {
        mDuration = duration;
    }

    public int getDuration() {
        return mDuration;
    }

    public void setMargin(float horizontalMargin, float verticalMargin) {
        toast.setMargin(horizontalMargin,verticalMargin);
    }

    public float getHorizontalMargin() {
        return toast.getHorizontalMargin();
    }

    public float getVerticalMargin() {
        return toast.getVerticalMargin();
    }

    public void setGravity(int gravity, int xOffset, int yOffset) {
        toast.setGravity(gravity,xOffset,yOffset);
    }

    public int getGravity() {
        return toast.getGravity();
    }

    public int getXOffset() {
        return toast.getXOffset();
    }

    public int getYOffset() {
        return toast.getYOffset();
    }

    public static MiToast makeText(Context context, CharSequence text, int duration) {
        Toast toast = Toast.makeText(context,text,Toast.LENGTH_SHORT);
        MiToast exToast = new MiToast(context);
        exToast.toast = toast;
        exToast.mDuration = duration;

        return exToast;
    }

    public static MiToast makeText(Context context, int resId, int duration)
            throws Resources.NotFoundException {
        return makeText(context, context.getResources().getText(resId), duration);
    }

    public void setText(int resId) {
        setText(mContext.getText(resId));
    }

    public void setText(CharSequence s) {
        toast.setText(s);
    }

    public int getAnimations() {
        return animations;
    }

    public void setAnimations(int animations) {
        this.animations = animations;
    }

    private void initTN() {
        try {
            Field tnField = toast.getClass().getDeclaredField("mTN");
            tnField.setAccessible(true);
            mTN = tnField.get(toast);
            show = mTN.getClass().getMethod("show");
            hide = mTN.getClass().getMethod("hide");

            Field tnParamsField = mTN.getClass().getDeclaredField("mParams");
            tnParamsField.setAccessible(true);
            params = (WindowManager.LayoutParams) tnParamsField.get(mTN);
            params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;

            /**设置动画*/
            if (animations != -1) {
                params.windowAnimations = animations;
            }

            /**调用tn.show()之前一定要先设置mNextView*/
            Field tnNextViewField = mTN.getClass().getDeclaredField("mNextView");
            tnNextViewField.setAccessible(true);
            tnNextViewField.set(mTN, toast.getView());

            mWM = (WindowManager)mContext.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        } catch (Exception e) {
            e.printStackTrace();
        }
        setGravity(Gravity.LEFT | Gravity.TOP,0 ,0);
    }

    /**
     * 记录当前手指位置在屏幕上的横坐标值
     */
    private float xInScreen;

    private int statusBarHeight;

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

    private class SmallViewTouchListener implements View.OnTouchListener{

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
                    if (xDownInScreen == xInScreen && yDownInScreen == yInScreen) {
                        showBigView();
                    }
                    break;
                default:
                    break;
            }
            return true;
        }
    }

//    @Override
//    public boolean onTouch(View v, MotionEvent event) {
//        //获取相对屏幕的坐标，即以屏幕左上角为原点
////        x = event.getRawX();
////        y = event.getRawY();
////        LogDetails.i("currX"+x+"====currY"+y);
//        switch (event.getAction()) {
////            case MotionEvent.ACTION_DOWN:    //捕获手指触摸按下动作
////                //获取相对View的坐标，即以此View左上角为原点
////                mTouchStartX =  event.getX();
////                mTouchStartY =  event.getY();
////                LogDetails.i("startX"+mTouchStartX+"====startY"+mTouchStartY);
////                break;
////            case MotionEvent.ACTION_MOVE:   //捕获手指触摸移动动作
////                updateViewPosition();
////                break;
////            case MotionEvent.ACTION_UP:    //捕获手指触摸离开动作
////                updateViewPosition();
////                break;
//            case MotionEvent.ACTION_DOWN:
//                // 手指按下时记录必要数据,纵坐标的值都需要减去状态栏高度
//                xInView = event.getX();
//                yInView = event.getY();
//                xDownInScreen = event.getRawX();
//                yDownInScreen = event.getRawY() - getStatusBarHeight();
//                xInScreen = event.getRawX();
//                yInScreen = event.getRawY() - getStatusBarHeight();
//                break;
//            case MotionEvent.ACTION_MOVE:
//                xInScreen = event.getRawX();
//                yInScreen = event.getRawY() - getStatusBarHeight();
//                // 手指移动的时候更新小悬浮窗的位置
//                updateViewPosition();
//                break;
//            case MotionEvent.ACTION_UP:
//                // 如果手指离开屏幕时，xDownInScreen和xInScreen相等，且yDownInScreen和yInScreen相等，则视为触发了单击事件。
//                if (xDownInScreen == xInScreen && yDownInScreen == yInScreen) {
//                    showBigView();
//                }
//                break;
//            default:
//                break;
//        }
//        return true;
//    }



    /**
     * 更新小悬浮窗在屏幕中的位置。
     */
    private void updateViewPosition() {
        params.x = (int) (xInScreen - xInView);
        params.y = (int) (yInScreen - yInView);
        mWM.updateViewLayout(mSmallView, params);
    }

    /**
     * 打开大悬浮窗，同时关闭小悬浮窗。
     */
    private void showBigView() {
        FWmanager.createBigWindow(mContext);
//        FWmanager.removeSmallWindow(mContext);
        mSmallView.post(hideRunnable);
    }

    private void showSmallView(){

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
                statusBarHeight = mContext.getResources().getDimensionPixelSize(x);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusBarHeight;
    }

    /**
     * 监听HOME按键
     */
    private HomeWatcherReceiver homeWatcherReceiver;
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
}
