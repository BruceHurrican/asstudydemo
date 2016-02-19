/*
 * Copyright (c) 2016.
 *    This document is Bruce's individual learning the android demo, wherein the use of the code from the Internet, only to use as a learning exchanges.
 *   And where any person can download and use, but not for commercial purposes.
 *   Author does not assume the resulting corresponding disputes.
 *   If you have good suggestions for the code, you can contact BurrceHurrican@foxmail.com
 *   本文件为Bruce's个人学习android的demo, 其中所用到的代码来源于互联网，仅作为学习交流使用。
 *   任和何人可以下载并使用, 但是不能用于商业用途。
 *   作者不承担由此带来的相应纠纷。
 *   如果对本代码有好的建议，可以联系BurrceHurrican@foxmail.com
 */

package com.bruce.demo.utils.volley;

import com.android.volley.VolleyError;

/**
 * 自定义监听器
 * Created by BruceHurrican on 2015/9/13.
 */
public interface VolleyRequestListener {
    void onSuccessListener(String result);

    void onErrorListener(VolleyError error);
}
