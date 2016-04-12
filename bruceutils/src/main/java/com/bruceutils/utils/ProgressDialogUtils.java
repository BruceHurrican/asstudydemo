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

import android.app.ProgressDialog;
import android.content.Context;

/**
 * progress bar 工具类
 * Created by BruceHurrican on 2015/12/25.
 */
public final class ProgressDialogUtils {
    private static ProgressDialog progressBar;

    /**
     * @param context
     * @param msg     提示信息
     * @return
     */
    public static ProgressDialog initProgressBar(Context context, String msg,int iconID) {
        progressBar = new ProgressDialog(context);
        progressBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressBar.setTitle("提示");
        progressBar.setMessage(msg);
        progressBar.setIcon(iconID);
        progressBar.setIndeterminate(false);
//        progressBar.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.anim_lock));
        progressBar.setCancelable(true);
        progressBar.setCanceledOnTouchOutside(false);
        return progressBar;
    }

    public static void showProgressDialog() {
        if (null != progressBar) {
            progressBar.show();
        } else {
            LogUtils.e("progressBar 是空");
        }
    }

    public static boolean isProgressDialogShowing() {
        if (null != progressBar) {
            return progressBar.isShowing();
        } else {
            return false;
        }
    }

    public static void cancelProgressDialog() {
        if (null != progressBar) {
            progressBar.dismiss();
        } else {
            LogUtils.e("progressBar 是空");
        }
    }
}
