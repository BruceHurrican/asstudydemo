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

package com.study.bruce.demo.studydata.fragments.pulltorefreshmore;

import com.study.bruce.demo.studydata.fragments.pulltorefreshmore.widget.PtrFrameLayout;
import com.study.bruce.demo.studydata.fragments.pulltorefreshmore.widget.PtrIndicator;

/**
 * Created by BruceHurrican on 2015/12/20.
 */
public interface PtrUIHandler {
    /**
     * When the content view has reached top and has been completed, view will be reset.
     *
     * @param frame
     */
    void onUIReset(PtrFrameLayout frame);

    /**
     * prepare for loading
     *
     * @param frame
     */
    void onUIRefreshPrepare(PtrFrameLayout frame);

    /**
     * perform refreshing UI
     *
     * @param frame
     */
    void onUIRefreshBegin(PtrFrameLayout frame);

    /**
     * perform UI after refresh
     *
     * @param frame
     */
    void onUIRefreshComplete(PtrFrameLayout frame);

    void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator);
}
