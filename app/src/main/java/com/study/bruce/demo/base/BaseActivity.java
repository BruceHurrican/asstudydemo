/*
 * Copyright (c) 2015.
 *   This document is just for Bruce's personal study.
 *   Some resources come from the Internet. Everyone can download and use it for study, but can
 *   not be used for commercial purpose. The author does not bear the
 *   corresponding disputes arising therefrom.
 *   Please delete within 24 hours after download.
 *   If you have good suggestions for this code, you can contact BurrceHurrican@foxmail.com.
 *   本文件为Bruce's个人学习android的demo, 其中所用到的代码来源于互联网，仅作为学习交流使用。
 *   任和何人可以下载并使用, 但是不能用于商业用途。
 *   作者不承担由此带来的相应纠纷。
 *   如果对本代码有好的建议，可以联系BurrceHurrican@foxmail.com
 */

package com.study.bruce.demo.base;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import com.study.bruce.demo.R;
import com.study.bruce.demo.log.Logs;

import java.lang.ref.WeakReference;

/**
 * 基类Activity
 * Created by BruceHurrican on 2015/7/5.
 */
public abstract class BaseActivity extends Activity {
    private Context context;
    private final String TAG = getTAG();
    private String logsTag;
    /**
     * 加载进度等待对话框
     */
    private ProgressDialog pd_waiting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = BaseActivity.this;
//        TAG = getTAG();
        logsTag = getLocalClassName() + "-->";
    }

    public abstract String getTAG();

    public final void logV(String text) {
        Logs.v(logsTag, text);
    }

    public final void logD(String text) {
        Logs.d(logsTag, text);
    }

    public final void logI(String text) {
        Logs.i(logsTag, text);
    }

    public final void logW(String text) {
        Logs.w(logsTag, text);
    }

    public final void logE(String text) {
        Logs.e(logsTag, text);
    }

    public void showToastShort(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (null != context) {
                    Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
                } else {
                    Logs.e(TAG, "打印日志出错");
                }
            }
        });
    }

    public void showToastLong(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (null != context) {
                    Toast.makeText(context, text, Toast.LENGTH_LONG).show();
                } else {
                    Logs.e(TAG, "print log error");
                }
            }
        });
    }

    /**
     * @param msg 提示信息
     * @return
     */
    public ProgressDialog initProgressDialog(String msg) {
        pd_waiting = new ProgressDialog(this);
        pd_waiting.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd_waiting.setTitle("提示");
        pd_waiting.setMessage(msg);
        pd_waiting.setIcon(R.drawable.icon_workdemo);
        pd_waiting.setIndeterminate(false);
        pd_waiting.setCancelable(false);
        pd_waiting.setCanceledOnTouchOutside(false);
        return pd_waiting;
    }

    public void showProgressDialog() {
        if (null != pd_waiting) {
            pd_waiting.show();
        } else {
            logE("显示进度框失败--pd_waiting->" + pd_waiting);
        }
    }

    public void cancelProgressDialog() {
        if (null != pd_waiting) {
            getUIHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    pd_waiting.cancel();
                }
            }, 2000);
        } else {
            logE("显示进度框失败--pd_waiting->" + pd_waiting);
        }
    }

    /**
     * 子类在使用sendUIMessage前需要调用initUIHandler方法来初始化mUIHandler对象
     */
    public void initUIHandler() {
        if (null == mUIHandler) {
            mUIHandler = new UIHandler(this);
        }
    }

    /**
     * 获得UIHandler，主线程的handler
     *
     * @return
     */
    public UIHandler getUIHandler() {
        if (null == mUIHandler) {
            logE("UIHandler 为空");
        }
        return mUIHandler;
    }

    /**
     * 处理UIHandler收到的消息，一般子类需要重写该方法
     *
     * @param msg
     */
    public void handleUIMessage(Message msg) {
        // super 一般不做处理，如果有共用的可以考虑在此处理
    }

    // 目前只提供两个sendUIMessage的方法，如果需要使用其他handler发送消息的方法getUIHandler后处理
    public void sendUIMessage(Message msg) {
        if (null != mUIHandler) {
            mUIHandler.sendMessage(msg);
        } else {
            uiHandlerNotInit();
        }
    }

    public void sendUIMessageEmpty(int what) {
        sendUIMessageEmptyDelayed(what, 0);
    }

    public void sendUIMessageEmptyDelayed(int what, long delayMillis) {
        if (null != mUIHandler) {
            mUIHandler.sendEmptyMessageDelayed(what, delayMillis);
        } else {
            uiHandlerNotInit();
        }
    }

    /**
     * 需要在父类onDestroy中调用，如果有特殊地方需要调用清除消息，可以调用
     */
    public void recycleUIHandler() {
        if (null != mUIHandler) {
            mUIHandler.removeCallbacksAndMessages(null);
        }
    }

    /**
     * 子类在使用handleWorkerMessage前需要调用initWorkerHandler方法来初始化mWorkerHandler和mHandlerThread对象
     *
     * @param name
     */
    public void initWorkerHandler(String name) {
        if (mHandlerThread == null && mWorkerHandler == null) {
            mHandlerThread = new HandlerThread(name);
            mHandlerThread.start();
            mWorkerHandler = new WorkerHandler(mHandlerThread.getLooper(), this);
        } else {
            logE("initWorkerHandler is called ,don't called again!");
        }
    }

    public void initWorkerHandler() {
        initWorkerHandler("workThread");
    }

    /**
     * UIHandler 未初始化，统一调用此方法
     */
    public void uiHandlerNotInit() {
        showToastShort("UIHandler 未初始化");
        logE("UIHandler 未初始化");
    }

    /**
     * 获得mWorkerHandler,子线程的WorkerHandler
     *
     * @return
     */
    public WorkerHandler getWorkerHandler() {
        if (null == mWorkerHandler) {
            logE("获取WorkerHandler实例为空");
        }
        return mWorkerHandler;
    }

    /**
     * 处理WorkerHandler收到的消息，一般子类需要重写该方法
     *
     * @param msg
     */
    public void handleWorkerMessage(Message msg) {
        // super 一般不做处理，如果有共用的可以考虑在此处理
    }

    // 目前只提供两个sendWorkerMessage的方法，如果需要使用其他handler发送消息的方法getmUIHandler后处理
    public void sendWorkderMessage(Message msg) {
        if (null != mWorkerHandler) {
            mWorkerHandler.sendMessage(msg);
        } else {
            workerHandlerNotInit();
        }
    }

    public void sendWorkerMessageEmpty(int what) {
        if (null != mWorkerHandler) {
            mWorkerHandler.sendEmptyMessage(what);
        } else {
            workerHandlerNotInit();
        }
    }

    /**
     * WorkerHandler 未初始化，统一调用此方法
     */
    private void workerHandlerNotInit() {
        showToastShort("WorkerHandler 未初始化");
        logE("WorkerHandler 未初始化");
    }

    public void recycleWorkerHandler() {
        if (null != mHandlerThread && null != mWorkerHandler) {
            mHandlerThread.quit();
            mWorkerHandler.removeCallbacksAndMessages(null);
        }
    }

    private UIHandler mUIHandler;
    private WorkerHandler mWorkerHandler;
    private HandlerThread mHandlerThread;

    public static class UIHandler extends Handler {
        WeakReference<BaseActivity> weakReference;

        /**
         * 防止 Handler 泄露，需要定义成内部静态类，Handler 也是造成内在泄露的一个重要的源头，主要 Handler 属于 TLS(Thread Local Storage)变量，生命周期和 Activity 是不一致的，
         * Handler 引用 Activity 会存在内在泄露
         *
         * @param activity
         */
        public UIHandler(BaseActivity activity) {
            super(Looper.getMainLooper());
            this.weakReference = new WeakReference<BaseActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final BaseActivity activity = weakReference.get();
            if (null != activity) {
                activity.handleUIMessage(msg);
            }
        }
    }

    /**
     * 了线程Handler，用作耗时处理，替换AsyncTask做后台请求
     */
    public static class WorkerHandler extends Handler {
        WeakReference<BaseActivity> weakReference;

        public WorkerHandler(Looper looper, BaseActivity activity) {
            super(looper);
            this.weakReference = new WeakReference<BaseActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final BaseActivity activity = weakReference.get();
            if (null != activity) {
                activity.handleWorkerMessage(msg);
            }
        }
    }
}
