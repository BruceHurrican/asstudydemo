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

package com.bruce.demo.studydata.fragments.sliding;

import android.content.Context;
import android.graphics.Point;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;

/**
 * 滑动操作练习
 * Created by BruceHurrican on 2016/2/7.
 */
public class SlidingView extends LinearLayout {
    private ViewDragHelper viewDragHelper;
    private View dragView, autoBackView, edgeTrackerView;
    private Point autoBackOriginPos = new Point();

    public SlidingView(Context context) {
        super(context);
        initViews();
    }

    public SlidingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public SlidingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews();
    }

    private void initViews() {
        viewDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                // 禁止 edgeTrackerView 直接移动
                return child == dragView || child == autoBackView;
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {
                return left;
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                return top;
            }

            // 手指释放时回调
            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                // autoBackView 手指释放时可以自动回去
                if (releasedChild == autoBackView) {
                    viewDragHelper.settleCapturedViewAt(autoBackOriginPos.x, autoBackOriginPos.y);
                    invalidate();
                }
            }

            // 在边界拖动时回调
            @Override
            public void onEdgeDragStarted(int edgeFlags, int pointerId) {
                viewDragHelper.captureChildView(edgeTrackerView, pointerId);
            }
        });
//        viewDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT); // 只在用户点击屏幕左侧边缘时响应触摸操作
        viewDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_ALL); // 屏幕上下左右边缘时响应触摸操作
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return viewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        viewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        autoBackOriginPos.x = autoBackView.getLeft();
        autoBackOriginPos.y = autoBackView.getTop();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        dragView = getChildAt(0);
        autoBackView = getChildAt(1);
        edgeTrackerView = getChildAt(2);
    }

    @Override
    public void computeScroll() {
        if (viewDragHelper.continueSettling(true)) {
            invalidate();
        }
    }
}
