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

package com.study.bruce.demo.log;

import android.util.Log;

import com.study.bruce.demo.utils.Constants;

/**
 * 日志基类
 * Created by BruceHurrican on 2015/7/5.
 */
public final class Logs {
    private static Logs instance;

    private Logs() {
    }

    public static Logs getInstance() {
        if (null != instance) {
            instance = new Logs();
        }
        return instance;
    }

    public static void v(String tag, String text) {
        if (Constants.ISDEBUG) {
            Log.v(tag, text);
        }
    }

    public static void d(String tag, String text) {
        if (Constants.ISDEBUG) {
            Log.d(tag, text);
        }
    }

    public static void i(String tag, String text) {
        if (Constants.ISDEBUG) {
            Log.i(tag, text);
        }
    }

    public static void w(String tag, String text) {
        if (Constants.ISDEBUG) {
            Log.w(tag, text);
        }
    }

    public static void e(String tag, String text) {
        if (Constants.ISDEBUG) {
            Log.e(tag, text);
        }
    }
}
