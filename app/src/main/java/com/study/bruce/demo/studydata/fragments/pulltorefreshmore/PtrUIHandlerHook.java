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

/**
 * Run a hook runnable, the runnable will run only once.
 * After the runnable is done, call resume to resume.
 * Once run, call take over will directory call the resume action.
 * Created by BruceHurrican on 2015/12/20.
 */
public abstract class PtrUIHandlerHook implements Runnable {
    private Runnable mResumeAction;
    private static final byte STATUS_PREPARE = 0;
    private static final byte STATUS_IN_HOOK = 1;
    private static final byte STATUS_RESUMED = 2;
    private byte mStatus = STATUS_PREPARE;

    public void takeOver() {
        takeOver(null);
    }

    public void takeOver(Runnable resumeAction) {
        if (null != resumeAction) {
            mResumeAction = resumeAction;
        }
        switch (mStatus) {
            case STATUS_PREPARE:
                mStatus = STATUS_IN_HOOK;
                run();
                break;
            case STATUS_IN_HOOK:
                break;
            case STATUS_RESUMED:
                resume();
                break;
        }
    }

    public void reset() {
        mStatus = STATUS_PREPARE;
    }

    public void resume() {
        if (null != mResumeAction) {
            mResumeAction.run();
        }
        mStatus = STATUS_RESUMED;
    }

    /**
     * Hook should always have a resume action, which is hooked by this hook.
     *
     * @param runnable
     */
    public void setResumeAction(Runnable runnable) {
        mResumeAction = runnable;
    }

}
