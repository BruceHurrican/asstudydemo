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
 * A single linked list to wrap PtrUIHandler
 * Created by BruceHurrican on 2015/12/20.
 */
public class PtrUIHandlerHolder implements PtrUIHandler {
    private PtrUIHandler mHandler;
    private PtrUIHandlerHolder mNext;

    private boolean contains(PtrUIHandler handler) {
        return null != mHandler && mHandler == handler;
    }

    private PtrUIHandlerHolder() {

    }

    public boolean hasHandler() {
        return null != mHandler;
    }

    private PtrUIHandler getHandler() {
        return mHandler;
    }

    public static void addHandler(PtrUIHandlerHolder head, PtrUIHandler handler) {
        if (null == handler) {
            return;
        }
        if (null == head) {
            return;
        }
        if (null == head.mHandler) {
            head.mHandler = handler;
            return;
        }
        PtrUIHandlerHolder current = head;
        for (; ; current = current.mNext) {
            // duplicated
            if (current.contains(handler)) {
                return;
            }
            if (current.mNext == null) {
                break;
            }
        }

        PtrUIHandlerHolder newHolder = new PtrUIHandlerHolder();
        newHolder.mHandler = handler;
        current.mNext = newHolder;
    }

    public static PtrUIHandlerHolder create() {
        return new PtrUIHandlerHolder();
    }

    public static PtrUIHandlerHolder removeHandler(PtrUIHandlerHolder head, PtrUIHandler handler) {
        if (null == head || null == handler || null == head.mHandler) {
            return head;
        }
        PtrUIHandlerHolder current = head;
        PtrUIHandlerHolder pre = null;
        do {
            // delete current: link pre to next, unlink next from current;
            // pre will no change, current move to next element;
            if (current.contains(handler)) {
                // current is head
                if (null == pre) {
                    head = current.mNext;
                    current.mNext = null;
                    current = head;
                } else {
                    pre.mNext = current.mNext;
                    current.mNext = null;
                    current = pre.mNext;
                }
            } else {
                pre = current;
                current = current.mNext;
            }
        } while (null != current);
        if (null == head) {
            head = new PtrUIHandlerHolder();
        }
        return head;
    }


    @Override
    public void onUIReset(PtrFrameLayout frame) {
        PtrUIHandlerHolder current = this;
        do {
            final PtrUIHandler handler = current.getHandler();
            if (null != handler) {
                handler.onUIReset(frame);
            }
        } while ((current = current.mNext) != null);
    }

    @Override
    public void onUIRefreshPrepare(PtrFrameLayout frame) {
        if (!hasHandler()) {
            return;
        }
        PtrUIHandlerHolder current = this;
        do {
            final PtrUIHandler handler = current.getHandler();
            if (null != handler) {
                handler.onUIRefreshPrepare(frame);
            }
        } while ((current = current.mNext) != null);
    }

    @Override
    public void onUIRefreshBegin(PtrFrameLayout frame) {
        PtrUIHandlerHolder current = this;
        do {
            final PtrUIHandler handler = current.getHandler();
            if (null != handler) {
                handler.onUIRefreshBegin(frame);
            }
        } while ((current = current.mNext) != null);
    }

    @Override
    public void onUIRefreshComplete(PtrFrameLayout frame) {
        PtrUIHandlerHolder current = this;
        do {
            final PtrUIHandler handler = current.getHandler();
            if (null != handler) {
                handler.onUIRefreshComplete(frame);
            }
        } while ((current = current.mNext) != null);
    }

    @Override
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {
        PtrUIHandlerHolder current = this;
        do {
            final PtrUIHandler handler = current.getHandler();
            if (null != handler) {
                handler.onUIPositionChange(frame, isUnderTouch, status, ptrIndicator);
            }
        } while ((current = current.mNext) != null);
    }
}
