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

package com.bruceutils.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;
import android.widget.Toast;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 崩溃日志处理类
 * Created by BruceHurrican on 2015/12/10.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "CrashHandler";
    private static CrashHandler instance = new CrashHandler();
    private Thread.UncaughtExceptionHandler mDefaultHandler;// 系统默认的UncaughtExceptionHandler处理类
    private Context context;
    private List<Activity> listContainer;
    private Map<String, String> info = new HashMap<String, String>(5);
    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

    private CrashHandler() {
    }

    public static CrashHandler getInstance() {
        return instance;
    }

    public void init(Context context) {
        this.context = context;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    public void initActivityContainer(List<Activity> activities) {
        listContainer = activities;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && null != mDefaultHandler) {
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(3000); // 让程序继续运行3秒，保证异常信息已经写入到文件中
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (null != listContainer) {
                for (Activity activity : listContainer) {
                    LogUtils.e("销毁的activity：" + activity.getLocalClassName());
                    activity.finish();
                }
            }
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    public boolean handleException(Throwable throwable) {
        if (null == throwable) {
            return false;
        } else {
            new Thread() {
                @Override
                public void run() {
                    Looper.prepare();
                    Toast.makeText(context, "程序出现异常，即将退出", Toast.LENGTH_SHORT).show();
                    Looper.loop();
                }
            }.start();
            // 收集设备参数信息
            collectDeviceInfo(context);
            StringBuffer sb = new StringBuffer();
            for (Map.Entry<String, String> entry : info.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                sb.append(key + "=" + value + "\r\n");
            }
            Writer writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            throwable.printStackTrace(printWriter);
            Throwable tmp = throwable.getCause();
            // 循环把所有异常信息写入writer中
            while (null != tmp) {
                tmp.printStackTrace(printWriter);
                tmp = tmp.getCause();
            }
            printWriter.close();
            sb.append(writer.toString());
            LogUtils.log2file(LogUtils.PATH_CRASH_LOG, TAG, sb.toString());
            return true;
        }
    }

    public void collectDeviceInfo(Context context) {
        PackageManager packageManager = context.getPackageManager();
        try {
            // 得到应用信息即主Activity
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (null != packageInfo) {
                String versionName = packageInfo.versionName == null ? "null" : packageInfo.versionName;
                String versionCode = packageInfo.versionCode + "";
                info.put("versionName", versionName);
                info.put("versionCode", versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        Field[] fields = Build.class.getDeclaredFields(); // 反射机制
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                info.put(field.getName(), field.get("").toString());
                LogUtils.d(field.getName() + ":" + field.get(""));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}
