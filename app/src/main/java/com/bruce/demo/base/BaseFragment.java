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

package com.bruce.demo.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.*;
import android.support.v4.app.Fragment;
import android.widget.Toast;
import com.bruce.demo.DemoApplication;
import com.bruce.demo.R;
import com.bruce.demo.utils.Constants;
import com.bruce.demo.utils.LogUtils;

import java.lang.ref.WeakReference;

/**
 * 基类Fragment
 * Created by BruceHurrican on 2015/9/13.
 */
public abstract class BaseFragment extends Fragment {
    private final String TAG = getTAG();
    private Context context;
    /**
     * 加载进度等待对话框
     */
    private ProgressDialog pd_waiting;
    private UIHandler mUIHandler;
    private WorkerHandler mWorkerHandler;
    private HandlerThread mHandlerThread;

    public abstract String getTAG();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity().getApplicationContext();
//        TAG = getTAG();
    }

    public void showToastShort(final String text) {
        initUIHandler();
        getUIHandler().post(new Runnable() {
            @Override
            public void run() {
                if (null != context) {
                    Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
                } else {
                    LogUtils.e("打印日志出错");
                }
            }
        });
    }

    public void showToastLong(final String text) {
        initUIHandler();
        getUIHandler().post(new Runnable() {
            @Override
            public void run() {
                if (null != context) {
                    Toast.makeText(context, text, Toast.LENGTH_LONG).show();
                } else {
                    LogUtils.e("print log error");
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        recycleUIHandler();
        recycleWorkerHandler();
        // 监控 fragment 是否存在 memory leak
        if (Constants.IS_OPEN_LEAKCANARY) {
            DemoApplication.getRefWatcher(getActivity()).watch(this);
        }
    }

    /**
     * @param msg 提示信息
     * @return
     */
    public ProgressDialog initProgressDialog(String msg) {
        pd_waiting = new ProgressDialog(getActivity());
        pd_waiting.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        pd_waiting.setTitle("提示");
        pd_waiting.setMessage(msg);
        pd_waiting.setIcon(R.mipmap.icon_workdemo);
        pd_waiting.setIndeterminate(false);
        pd_waiting.setCancelable(false);
        pd_waiting.setCanceledOnTouchOutside(false);
        return pd_waiting;
    }

    public void showProgressDialog() {
        if (null != pd_waiting) {
            pd_waiting.show();
        } else {
            LogUtils.e("显示进度框失败--pd_waiting->" + pd_waiting);
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
            LogUtils.e("显示进度框失败--pd_waiting->" + pd_waiting);
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
            LogUtils.e("UIHandler 为空");
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
            LogUtils.e("initWorkerHandler is called ,don't called again!");
        }
    }

    public void initWorkerHandler() {
        initWorkerHandler("workThread");
    }

    /**
     * UIHandler 未初始化，统一调用此方法
     */
    public void uiHandlerNotInit() {
//        showToastShort("UIHandler 未初始化");
        LogUtils.e("UIHandler 未初始化");
    }

    /**
     * 获得mWorkerHandler,子线程的WorkerHandler
     *
     * @return
     */
    public WorkerHandler getWorkerHandler() {
        if (null == mWorkerHandler) {
            LogUtils.e("获取WorkerHandler实例为空");
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
//        showToastShort("WorkerHandler 未初始化");
        LogUtils.e("WorkerHandler 未初始化");
    }

    public void recycleWorkerHandler() {
        if (null != mHandlerThread && null != mWorkerHandler) {
            mHandlerThread.quit();
            mWorkerHandler.removeCallbacksAndMessages(null);
        }
    }

    public static class UIHandler extends Handler {
        WeakReference<BaseFragment> weakReference;

        /**
         * 防止 Handler 泄露，需要定义成内部静态类，Handler 也是造成内在泄露的一个重要的源头，主要 Handler 属于 TLS(Thread Local Storage)变量，生命周期和 Activity 是不一致的，
         * Handler 引用 Activity 会存在内在泄露
         *
         * @param fragment
         */
        public UIHandler(BaseFragment fragment) {
            super(Looper.getMainLooper());
            this.weakReference = new WeakReference<BaseFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final BaseFragment fragment = weakReference.get();
            if (null != fragment) {
                fragment.handleUIMessage(msg);
            }
        }
    }

    /**
     * 子线程Handler，用作耗时处理，替换AsyncTask做后台请求
     */
    public static class WorkerHandler extends Handler {
        WeakReference<BaseFragment> weakReference;

        public WorkerHandler(Looper looper, BaseFragment fragment) {
            super(looper);
            this.weakReference = new WeakReference<BaseFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            final BaseFragment fragment = weakReference.get();
            if (null != fragment) {
                fragment.handleWorkerMessage(msg);
            }
        }
    }
}
