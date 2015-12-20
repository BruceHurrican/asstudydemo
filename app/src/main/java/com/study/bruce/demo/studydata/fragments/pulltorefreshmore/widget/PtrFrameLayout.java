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

package com.study.bruce.demo.studydata.fragments.pulltorefreshmore.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.TextView;

import com.study.bruce.demo.R;
import com.study.bruce.demo.studydata.fragments.pulltorefreshmore.PtrHandler;
import com.study.bruce.demo.studydata.fragments.pulltorefreshmore.PtrUIHandler;
import com.study.bruce.demo.studydata.fragments.pulltorefreshmore.PtrUIHandlerHolder;
import com.study.bruce.demo.studydata.fragments.pulltorefreshmore.PtrUIHandlerHook;
import com.study.bruce.demo.utils.LogUtils;

/**
 * Created by BruceHurrican on 2015/12/20.
 */
public class PtrFrameLayout extends ViewGroup {
    // status enum
    public static final byte PTR_STATUS_INIT = 1;
    private byte mStatus = PTR_STATUS_INIT;
    public static final byte PTR_STATUS_PREPARE = 2;
    public static final byte PTR_STATUS_LOADING = 3;
    public static final byte PTR_STATUS_COMPLETE = 3;
    private static final boolean DEBUG_LAYOUT = true;
    public static boolean DEBUG = false;
    private static int ID = 1;
    protected final String LOG_TAG = "ptr-frame-" + (++ID);
    // auto refresh status
    private static byte FLAG_AUTO_REFRESH_AT_ONCE = 0x01;
    private static byte FLAG_AUTO_REFRESH_BUT_LATER = 0x01 << 1;
    private static byte FLAG_ENABLE_NEXT_PTR_ATR_ONCE = 0x01 << 2;
    private static byte FLAG_PIN_CONTENT = 0x01 << 3;
    private static byte MASK_AUTO_REFRESH = 0x03;
    protected View mContent;
    // optional config for define header and content in xml file
    private int mHeaderId = 0;
    private int mContainerId = 0;
    // config
    private int mDurationToClose = 200;
    private int mDurationToCloseHeader = 1000;
    private boolean mKeepHeaderWhenRefresh = true;
    private boolean mPullToRefresh = false;
    private View mHeaderView;
    private PtrUIHandlerHolder mPtrUIHandlerHolder = PtrUIHandlerHolder.create();
    private PtrHandler mPtrHandler;
    // working parameters
    private ScrollChecker mScrollChecker;
    private int mPagingTouchSlop;
    private int mHeaderHeight;
    private boolean mDisableWhenHorizontalMove = false;
    private int mFlag = 0x00;
    // disable when detect moving horizontally
    private boolean mPreventForHorizontal = false;
    private MotionEvent mLastMoveEvent;
    private PtrUIHandlerHook mRefreshCompleteHook;
    private int mLoadingMinTime = 500;
    private long mLoadingStartTime = 0;
    private PtrIndicator mPtrIndicator;
    private boolean mHasSendCancelEvent = false;
    private Runnable mPerformRefreshCompleteDelay = new Runnable() {
        @Override
        public void run() {
            mPerformRefreshComplete();
        }
    };

    public PtrFrameLayout(Context context) {
        this(context, null);
    }

    public PtrFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PtrFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mPtrIndicator = new PtrIndicator();
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.PtrFrameLayout, 0, 0);
        if (null != array) {
            mHeaderId = array.getResourceId(R.styleable.PtrFrameLayout_ptr_header, mHeaderId);
            mContainerId = array.getResourceId(R.styleable.PtrFrameLayout_ptr_content, mContainerId);
            mPtrIndicator.setResistance(array.getFloat(R.styleable.PtrFrameLayout_ptr_resistance, mPtrIndicator.getResistance()));
            mDurationToClose = array.getInt(R.styleable.PtrFrameLayout_ptr_duration_to_close, mDurationToClose);
            mDurationToCloseHeader = array.getInt(R.styleable.PtrFrameLayout_ptr_duration_to_close_header, mDurationToCloseHeader);

            float ratio = mPtrIndicator.getRatioOfHeaderHeightToRefresh();
            ratio = array.getFloat(R.styleable.PtrFrameLayout_ptr_ratio_of_header_height_to_refresh, ratio);
            mPtrIndicator.setRatioOfHeaderHeightToRefresh(ratio);
            mKeepHeaderWhenRefresh = array.getBoolean(R.styleable.PtrFrameLayout_ptr_keep_header_when_refresh, mKeepHeaderWhenRefresh);
            mPullToRefresh = array.getBoolean(R.styleable.PtrFrameLayout_ptr_pull_to_fresh, mPullToRefresh);
            array.recycle();
        }
        mScrollChecker = new ScrollChecker();
        final ViewConfiguration conf = ViewConfiguration.get(getContext());
        mPagingTouchSlop = conf.getScaledTouchSlop() * 2;
    }

    @Override
    protected void onFinishInflate() {
        final int childCount = getChildCount();
        if (childCount > 2) {
            throw new IllegalStateException("PtrFrameLayout only can host 2 elements");
        } else if (childCount == 2) {
            if (mHeaderId != 0 && mHeaderView == null) {
                mHeaderView = findViewById(mHeaderId);
            }
            if (mContainerId != 0 && mContent == null) {
                mContent = findViewById(mContainerId);
            }

            // not specify header or content
            if (null == mContent || null == mHeaderView) {
                View child1 = getChildAt(0);
                View child2 = getChildAt(1);
                if (child1 instanceof PtrUIHandler) {
                    mHeaderView = child1;
                    mContent = child2;
                } else if (child2 instanceof PtrUIHandler) {
                    mHeaderView = child2;
                    mContent = child1;
                } else {
                    // both are not specified
                    if (null == mContent && null == mHeaderView) {
                        mHeaderView = child1;
                        mContent = child2;
                    } else {
                        // only one is specified
                        if (null == mHeaderView) {
                            mHeaderView = mContent == child1 ? child2 : child1;
                        } else {
                            mContent = mHeaderView == child1 ? child2 : child1;
                        }
                    }

                }
            } else if (childCount == 1) {
                mContent = getChildAt(0);
            } else {
                TextView errorView = new TextView(getContext());
                errorView.setClickable(true);
                errorView.setTextColor(0xffff6600);
                errorView.setGravity(Gravity.CENTER);
                errorView.setTextSize(20);
                errorView.setText("The content view in PtrFrameLayout is empty. Do you forget to specify its id in xml layout file?");
                mContent = errorView;
                addView(mContent);
            }
            if (null != mHeaderView) {
                mHeaderView.bringToFront();
            }
            super.onFinishInflate();
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (null != mScrollChecker) {
            mScrollChecker.destroy();
        }
        if (mPerformRefreshCompleteDelay != null) {
            removeCallbacks(mPerformRefreshCompleteDelay);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        LogUtils.d(String.format("onMeasure frame: width:%s,height:%s,padding:%s %s %s %s", getMeasuredHeight(), getMeasuredWidth(), getPaddingLeft(), getPaddingRight(), getPaddingTop(), getPaddingBottom()));
        if (null != mHeaderView) {
            measureChildWithMargins(mHeaderView, widthMeasureSpec, 0, heightMeasureSpec, 0);
            MarginLayoutParams layoutParams = (MarginLayoutParams) mHeaderView.getLayoutParams();
            mHeaderHeight = mHeaderView.getMeasuredHeight() + layoutParams.topMargin + layoutParams.bottomMargin;
            mPtrIndicator.setHeaderHeight(mHeaderHeight);
        }
        if (null != mContent) {
            measureContentView(mContent, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams layoutParams = (MarginLayoutParams) mContent.getLayoutParams();
            LogUtils.d(String.format("onMeasure content, width: %s, height: %s, margin: %s %s %s %s", getMeasuredWidth(), getMeasuredHeight(), layoutParams.leftMargin, layoutParams.topMargin, layoutParams.rightMargin, layoutParams.bottomMargin));
            LogUtils.d(String.format("onMeasure, currentPos: %s, lastPos: %s, top: %s", mPtrIndicator.getmCurrentPosY(), mPtrIndicator.getLastPosY(), mContent.getTop()));
        }
    }

    private void measureContentView(View child, int parentWidthMeasureSpec, int parentHeightMeasureSpec) {
        final MarginLayoutParams layoutParams = (MarginLayoutParams) child.getLayoutParams();
        final int childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec, getPaddingLeft() + getPaddingRight() + layoutParams.leftMargin + layoutParams.rightMargin, layoutParams.width);
        final int childHeightMeasureSpec = getChildMeasureSpec(parentHeightMeasureSpec, getPaddingTop() + getPaddingBottom() + layoutParams.topMargin, layoutParams.height);
        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        layoutChildren();
    }

    private void layoutChildren() {
        int offsetX = mPtrIndicator.getmCurrentPosY();
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        if (null != mHeaderView) {
            MarginLayoutParams layoutParams = (MarginLayoutParams) mHeaderView.getLayoutParams();
            final int left = paddingLeft + layoutParams.leftMargin;
            final int top = paddingTop + layoutParams.topMargin + offsetX - mHeaderHeight;
            final int right = left + mHeaderView.getMeasuredWidth();
            final int bottom = top + mHeaderView.getMeasuredHeight();
            mHeaderView.layout(left, top, right, bottom);
            LogUtils.d(String.format("onLayout header: %s %s %s %s", left, top, right, bottom));
        }
        if (null != mContent) {
            if (isPinContent()) {
                offsetX = 0;
            }
            MarginLayoutParams layoutParams = (MarginLayoutParams) mContent.getLayoutParams();
            final int left = paddingLeft + layoutParams.leftMargin;
            final int top = paddingTop + layoutParams.topMargin + offsetX;
            final int right = left + mContent.getMeasuredWidth();
            final int bottom = top + mContent.getMeasuredHeight();
            LogUtils.d(String.format("onLayout content: %s %s %s %s", left, top, right, bottom));
            mContent.layout(left, top, right, bottom);
        }
    }

    public boolean dispatchTouchEventSupper(MotionEvent e) {
        return super.dispatchTouchEvent(e);
    }


}
