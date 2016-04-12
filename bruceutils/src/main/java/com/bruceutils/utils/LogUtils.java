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

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Log 工具，类似 android.util.Log. tag 自动产生，格式：
 * customTagPrefix:className.methodName(Line:lineNumber),
 * customTagPrefix 为空时只输出：className.methodName(Line:lineNumber).
 * Created by BruceHurrican on 2015/12/9.
 */
public final class LogUtils {
    private static boolean IS_OPEN_LOG = true;
    /**
     * sd 卡根目录
     */
    public static final String FILE_PATH_ROOT = Environment.getExternalStorageDirectory().getPath() + "/demo/";// SD卡中的根目录
    /**
     * 日志路径
     */
    public static final String PATH_LOG_INFO = FILE_PATH_ROOT + "log/";
    /**
     * 崩溃日志路径
     */
    public static final String PATH_CRASH_LOG = FILE_PATH_ROOT + "crash/";
    private static final String customTagPrefix = "bruce";
    public static AndroidCustomLog androidCustomLog;

    public static void openLog(boolean isOpen) {
        IS_OPEN_LOG = isOpen;
    }

    private LogUtils() {
    }

    public static void v(String content) {
        if (!IS_OPEN_LOG) {
            return;
        }
        StackTraceElement callerStackTraceElement = getCallerStackTraceElement();
        String tag = generateTag(callerStackTraceElement);
        if (null != androidCustomLog) {
            androidCustomLog.v(tag, content);
        } else {
            Log.v(tag, content);
        }
    }

    public static void v(String content, Throwable throwable) {
        if (!IS_OPEN_LOG) {
            return;
        }
        StackTraceElement callerStackTraceElement = getCallerStackTraceElement();
        String tag = generateTag(callerStackTraceElement);
        if (null != androidCustomLog) {
            androidCustomLog.v(tag, content, throwable);
        } else {
            Log.v(tag, content);
        }
    }

    public static void d(String content) {
        if (!IS_OPEN_LOG) {
            return;
        }
        StackTraceElement callerStackTraceElement = getCallerStackTraceElement();
        String tag = generateTag(callerStackTraceElement);
        if (null != androidCustomLog) {
            androidCustomLog.d(tag, content);
        } else {
            Log.d(tag, content);
        }
    }

    public static void d(String content, Throwable throwable) {
        if (!IS_OPEN_LOG) {
            return;
        }
        StackTraceElement callerStackTraceElement = getCallerStackTraceElement();
        String tag = generateTag(callerStackTraceElement);
        if (null != androidCustomLog) {
            androidCustomLog.d(tag, content, throwable);
        } else {
            Log.d(tag, content);
        }
    }

    public static void i(String content) {
        if (!IS_OPEN_LOG) {
            return;
        }
        StackTraceElement callerStackTraceElement = getCallerStackTraceElement();
        String tag = generateTag(callerStackTraceElement);
        if (null != androidCustomLog) {
            androidCustomLog.i(tag, content);
        } else {
            Log.i(tag, content);
        }
    }

    public static void i(String content, Throwable throwable) {
        if (!IS_OPEN_LOG) {
            return;
        }
        StackTraceElement callerStackTraceElement = getCallerStackTraceElement();
        String tag = generateTag(callerStackTraceElement);
        if (null != androidCustomLog) {
            androidCustomLog.i(tag, content, throwable);
        } else {
            Log.i(tag, content);
        }
    }

    public static void w(String content) {
        if (!IS_OPEN_LOG) {
            return;
        }
        StackTraceElement callerStackTraceElement = getCallerStackTraceElement();
        String tag = generateTag(callerStackTraceElement);
        if (null != androidCustomLog) {
            androidCustomLog.w(tag, content);
        } else {
            Log.w(tag, content);
        }
    }

    public static void w(Throwable throwable) {
        if (!IS_OPEN_LOG) {
            return;
        }
        StackTraceElement callerStackTraceElement = getCallerStackTraceElement();
        String tag = generateTag(callerStackTraceElement);
        if (null != androidCustomLog) {
            androidCustomLog.w(tag, throwable);
        } else {
            Log.w(tag, throwable.toString());
        }
    }

    public static void w(String content, Throwable throwable) {
        if (!IS_OPEN_LOG) {
            return;
        }
        StackTraceElement callerStackTraceElement = getCallerStackTraceElement();
        String tag = generateTag(callerStackTraceElement);
        if (null != androidCustomLog) {
            androidCustomLog.w(tag, content, throwable);
        } else {
            Log.w(tag, content);
        }
    }

    public static void e(String content) {
        if (!IS_OPEN_LOG) {
            return;
        }
        StackTraceElement callerStackTraceElement = getCallerStackTraceElement();
        String tag = generateTag(callerStackTraceElement);
        if (null != androidCustomLog) {
            androidCustomLog.e(tag, content);
        } else {
            Log.e(tag, content);
        }
    }

    public static void e(String content, Throwable throwable) {
        if (!IS_OPEN_LOG) {
            return;
        }
        StackTraceElement callerStackTraceElement = getCallerStackTraceElement();
        String tag = generateTag(callerStackTraceElement);
        if (null != androidCustomLog) {
            androidCustomLog.e(tag, content, throwable);
        } else {
            Log.e(tag, content);
        }
    }

    public static void wtf(String content) {
        if (!IS_OPEN_LOG) {
            return;
        }
        StackTraceElement callerStackTraceElement = getCallerStackTraceElement();
        String tag = generateTag(callerStackTraceElement);
        if (null != androidCustomLog) {
            androidCustomLog.wtf(tag, content);
        } else {
            Log.w(tag, content);
        }
    }

    public static void wtf(Throwable throwable) {
        if (!IS_OPEN_LOG) {
            return;
        }
        StackTraceElement callerStackTraceElement = getCallerStackTraceElement();
        String tag = generateTag(callerStackTraceElement);
        if (null != androidCustomLog) {
            androidCustomLog.wtf(tag, throwable);
        } else {
            Log.w(tag, throwable.toString());
        }
    }

    public static void wtf(String content, Throwable throwable) {
        if (!IS_OPEN_LOG) {
            return;
        }
        StackTraceElement callerStackTraceElement = getCallerStackTraceElement();
        String tag = generateTag(callerStackTraceElement);
        if (null != androidCustomLog) {
            androidCustomLog.wtf(tag, content, throwable);
        } else {
            Log.w(tag, content);
        }
    }


    private static String generateTag(StackTraceElement callStackTraceElement) {
        String tag = "%s.%s(Line:%d)";
        String stackTraceElementClassName = callStackTraceElement.getClassName(); // 获取到类名
        stackTraceElementClassName = stackTraceElementClassName.substring(stackTraceElementClassName.lastIndexOf(".") + 1);
        tag = String.format(tag, stackTraceElementClassName, callStackTraceElement.getMethodName(), callStackTraceElement.getLineNumber());
        tag = TextUtils.isEmpty(customTagPrefix) ? tag : (customTagPrefix + ":" + tag);
        return tag;
    }

    private static StackTraceElement getCallerStackTraceElement() {
        return Thread.currentThread().getStackTrace()[4];
    }

    /**
     * 将日志写入本地文件
     *
     * @param path 文件路径
     * @param tag  日志内容标签
     * @param msg
     */
    public static void log2file(String path, String tag, String msg) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("", Locale.SIMPLIFIED_CHINESE);
            dateFormat.applyPattern("yyyyMMddHHmm");
            path += dateFormat.format(date) + ".log";
            dateFormat.applyPattern("[yyyy-MM-dd HH:mm:ss]");
            String time = dateFormat.format(date);
            File file = new File(path);
            if (!file.exists()) {
                createDipPath(path);
            }
            BufferedWriter out = null;
            try {
                out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
//                out.write(time+" "+tag+" "+msg+"\r\n");
                out.write(time + " " + tag + " " + msg);
                out.newLine();
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (null != out) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 根据文件路径，递归创建文件
     *
     * @param file
     */
    public static void createDipPath(String file) {
        String parentFile = file.substring(0, file.lastIndexOf("/"));
        File file1 = new File(file);
        File parent = new File(parentFile);
        if (!file1.exists()) {
            parent.mkdirs();
            try {
                file1.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public interface AndroidCustomLog {
        void v(String tag, String content);

        void v(String tag, String content, Throwable throwable);

        void d(String tag, String content);

        void d(String tag, String content, Throwable throwable);

        void i(String tag, String content);

        void i(String tag, String content, Throwable throwable);

        void w(String tag, String content);

        void w(String tag, Throwable throwable);

        void w(String tag, String content, Throwable throwable);

        void e(String tag, String content);

        void e(String tag, String content, Throwable throwable);

        void wtf(String tag, String content);

        void wtf(String tag, Throwable throwable);

        void wtf(String tag, String content, Throwable throwable);
    }
}
