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
import android.widget.Scroller;
import android.widget.TextView;

import com.study.bruce.demo.studydata.fragments.pulltorefreshmore.PtrHandler;
import com.study.bruce.demo.studydata.fragments.pulltorefreshmore.PtrUIHandler;
import com.study.bruce.demo.studydata.fragments.pulltorefreshmore.PtrUIHandlerHolder;
import com.study.bruce.demo.studydata.fragments.pulltorefreshmore.PtrUIHandlerHook;
import com.study.bruce.demo.utils.Constants;
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
            performRefreshComplete();
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
            LogUtils.d(String.format("onMeasure, currentPos: %s, lastPos: %s, top: %s", mPtrIndicator.getCurrentPosY(), mPtrIndicator.getLastPosY(), mContent.getTop()));
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
        int offsetX = mPtrIndicator.getCurrentPosY();
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

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!isEnabled() || null == mContent || null == mHeaderView) {
            return dispatchTouchEventSupper(ev);
        }
        int action = ev.getAction();
        switch (action) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mPtrIndicator.onRelease();
                if (mPtrIndicator.hasLeftStartPosition()) {
                    LogUtils.d("call onRelease when user release");
                    onRelease(false);
                    if (mPtrIndicator.hasMovedAfterPressedDown()) {
                        sendCancelEvent();
                        return true;
                    }
                    return dispatchTouchEventSupper(ev);
                } else {
                    return dispatchTouchEventSupper(ev);
                }
            case MotionEvent.ACTION_DOWN:
                mHasSendCancelEvent = false;
                mPtrIndicator.onPressDown(ev.getX(), ev.getY());
                mScrollChecker.abortIfWorking();
                mPreventForHorizontal = false;
                /* The cancel event will be sent once the position is moved.
                    So let the event pass to children.
                 */
                dispatchTouchEventSupper(ev);
                return true;
            case MotionEvent.ACTION_MOVE:
                mLastMoveEvent = ev;
                mPtrIndicator.onMove(ev.getX(), ev.getY());
                float offsetX = mPtrIndicator.getOffsetX();
                float offsetY = mPtrIndicator.getOffsetY();
                if (mDisableWhenHorizontalMove && !mPreventForHorizontal && (Math.abs(offsetX) > mPagingTouchSlop && Math.abs(offsetX) > Math.abs(offsetY))) {
                    if (mPtrIndicator.isInStartPosition()) {
                        mPreventForHorizontal = true;
                    }
                }
                if (mPreventForHorizontal) {
                    return dispatchTouchEventSupper(ev);
                }
                boolean moveDown = offsetY > 0;
                boolean moveUp = !moveDown;
                boolean canMoveUp = mPtrIndicator.hasLeftStartPosition();
                if (Constants.ISDEBUG) {
                    boolean canMoveDown = mPtrHandler != null && mPtrHandler.checkCanDoRefresh(this, mContent, mHeaderView);
                    LogUtils.d(String.format("ACTION_MOVE: offsetY: %s, currentPos: %s, moveUp: %s, canMoveUp: %s, moveDown: %s, canMoveDown: %s", offsetY, mPtrIndicator.getCurrentPosY(), moveUp, canMoveUp, moveDown, canMoveDown));
                }
                // disable move when header not reach top
                if (moveDown && null != mPtrHandler != mPtrHandler.checkCanDoRefresh(this, mContent, mHeaderView)) {
                    return dispatchTouchEventSupper(ev);
                }
                if ((moveUp && canMoveUp) || moveDown) {
                    movePos(offsetY);
                    return true;
                }
        }
        return dispatchTouchEventSupper(ev);
    }

    /**
     * if deltaY > 0, move the content down
     *
     * @param deltaY
     */
    private void movePos(float deltaY) {
        // has reached the top
        if ((deltaY < 0 && mPtrIndicator.isInStartPosition())) {
            LogUtils.d("has reached the top");
            return;
        }
        int to = mPtrIndicator.getCurrentPosY() + (int) deltaY;
        // over top
        if (mPtrIndicator.willOverTop(to)) {
            LogUtils.d("over top");
            to = PtrIndicator.POS_START;
        }
        mPtrIndicator.setCurrentPos(to);
        int change = to - mPtrIndicator.getLastPosY();
        updatePos(change);
    }

    private void updatePos(int change) {
        if (0 == change) {
            return;
        }
        boolean isUnderTouch = mPtrIndicator.isUnderTouch();
        // once moved, cancel event will be sent to child
        if (isUnderTouch && !mHasSendCancelEvent && mPtrIndicator.hasMovedAfterPressedDown()) {
            mHasSendCancelEvent = true;
            sendCancelEvent();
        }
        // leave initiated position or just refresh complete
        if ((mPtrIndicator.hasJustLeftStartPosition() && mStatus == PTR_STATUS_INIT) || (mPtrIndicator.goDownCrossFinishPosition() && mStatus == PTR_STATUS_COMPLETE && isEnabledNextPtrAtOnce())) {
            mStatus = PTR_STATUS_PREPARE;
            mPtrUIHandlerHolder.onUIRefreshPrepare(this);
            LogUtils.d(String.format("PtrUIHandler: onUIRefreshPrepare, mFlag %s", mFlag));
        }
        // back to initiated position
        if (mPtrIndicator.hasJustBackToStartPosition()) {
            tryToNotifyReset();
            // recover event to children
            if (isUnderTouch) {
                sendDownEvent();
            }
        }
        // Pull to Refresh
        if (mStatus == PTR_STATUS_PREPARE) {
            // reach fresh height while moving from top to bottom
            if (isUnderTouch && !isAutoRefresh() && mPullToRefresh && mPtrIndicator.crossRefreshLineFromTopToBottom()) {
                tryToPerformRefresh();
            }
            // reach header height while auto refresh
            if (performAutoRefreshButLater() && mPtrIndicator.hasJustReachedHeaderHeightFromTopToBottom()) {
                tryToPerformRefresh();
            }
        }
        LogUtils.d(String.format("updatePos: change: %s, current: %s, last: %s, top: %s, headerHeight: %s", change, mPtrIndicator.getCurrentPosY(), mPtrIndicator.getLastPosY(), mContent.getTop(), mHeaderHeight));
        mHeaderView.offsetTopAndBottom(change);
        if (!isPinContent()) {
            mContent.offsetTopAndBottom(change);
        }
        invalidate();
        if (mPtrUIHandlerHolder.hasHandler()) {
            mPtrUIHandlerHolder.onUIPositionChange(this, isUnderTouch, mStatus, mPtrIndicator);
        }
        onPostionChange(isUnderTouch, mStatus, mPtrIndicator);
    }

    protected void onPostionChange(boolean isInTouching, byte status, PtrIndicator mPtrIndicator) {

    }

    public int getHeaderHeight() {
        return mHeaderHeight;
    }

    private void onRelease(boolean stayForLoading) {
        tryToPerformRefresh();
        if (mStatus == PTR_STATUS_LOADING) {
            // keep header for fresh
            if (mKeepHeaderWhenRefresh) {
                // scroll header back
                if (mPtrIndicator.isOverOffsetToKeepHeaderWhileLoading() && !stayForLoading) {
                    mScrollChecker.tryToScrollTo(mPtrIndicator.getOffsetToKeepHeaderWhileLoading(), mDurationToClose);
                } else {
                    // do nothing
                }
            } else {
                tryScrollBackToTopWhileLoading();
            }
        } else {
            if (mStatus == PTR_STATUS_COMPLETE) {
                notifyUIRefreshComplete(false);
            } else {
                tryScrollBackToTopAbortRefresh();
            }
        }
    }

    /**
     * please DO REMEMBER resume the hook
     *
     * @param hook
     */
    public void setRefreshCompleteHook(PtrUIHandlerHook hook) {
        mRefreshCompleteHook = hook;
        hook.setResumeAction(new Runnable() {
            @Override
            public void run() {
                LogUtils.d("mRefreshCompleteHook resume.");
            }

            notifyUIRefreshComplete(true);
        });
    }

    /**
     * Scroll back to top if is not under touch
     */
    private void tryScrollBackToTop() {
        if (!mPtrIndicator.isUnderTouch()) {
            mScrollChecker.tryToScrollTo(PtrIndicator.POS_START, mDurationToCloseHeader);
        }
    }

    /**
     * just make easier to understand
     */
    private void tryScrollBackToTopWhileLoading() {
        tryScrollBackToTop();
    }

    /**
     * just make easier to understand
     */
    private void tryScrollBackToTopAfterComplete() {
        tryScrollBackToTop();
    }

    /**
     * just make easier to understand
     */
    private void tryScrollBackToTopAbortRefresh() {
        tryScrollBackToTop();
    }

    private boolean tryToPerformRefresh() {
        if (mStatus != PTR_STATUS_PREPARE) {
            return false;
        }
        if ((mPtrIndicator.isOverOffsetToKeepHeaderWhileLoading() && isAutoRefresh()) || mPtrIndicator.isOverOffsetToRefresh()) {
            mStatus = PTR_STATUS_LOADING;
            performRefresh();
        }
        return false;
    }

    private void performRefresh() {
        mLoadingStartTime = System.currentTimeMillis();
        if (mPtrUIHandlerHolder.hasHandler()) {
            mPtrUIHandlerHolder.onUIRefreshBegin(this);
            LogUtils.d("PtrUIHandler: onUIRefreshBegin");
        }
        if (null != mPtrHandler) {
            mPtrHandler.onRefreshBegin(this);
        }
    }

    /**
     * If at the top and not in loading, reset
     *
     * @return
     */
    private boolean tryToNotifyReset() {
        if ((mStatus == PTR_STATUS_COMPLETE || mStatus == PTR_STATUS_PREPARE) && mPtrIndicator.isInStartPosition()) {
            if (mPtrUIHandlerHolder.hasHandler()) {
                mPtrUIHandlerHolder.onUIReset(this);
                LogUtils.d("PtrUIHandler: onUIReset");
            }
            mStatus = PTR_STATUS_INIT;
            clearFlag();
            return true;
        }
        return false;
    }

    protected void onPtrScrollAbort() {
        if (mPtrIndicator.hasLeftStartPosition() && isAutoRefresh()) {
            LogUtils.d("call onRelease after scroll abort");
        }
        onRelease(true);
    }

    protected void onPtrScrollFinish() {
        if (mPtrIndicator.hasLeftStartPosition() && isAutoRefresh()) {
            LogUtils.d("call onRelease after scroll finish");
        }
        onRelease(true);
    }

    /**
     * Detect whether is refreshing.
     *
     * @return
     */
    public boolean isRefreshing() {
        return mStatus == PTR_STATUS_LOADING;
    }

    /**
     * Call this when data is loaded.
     * The UI will perform complete at once or after a delay, depends on the time elapsed is greater then {@link #mLoadingMinTime} or not
     */
    public final void refreshComplete() {
        LogUtils.d("refreshComplete");
        if (null != mRefreshCompleteHook) {
            mRefreshCompleteHook.reset();
        }
        int delay = (int) (mLoadingMinTime - (System.currentTimeMillis() - mLoadingStartTime));
        if (delay <= 0) {
            LogUtils.d("performRefreshComplete at once");
            performRefreshComplete();
        } else {
            postDelayed(mPerformRefreshCompleteDelay, delay);
            LogUtils.d(String.format("performRefreshComplete after delay: %s", delay));
        }
    }

    /**
     * Do refresh complete work when time elapsed is greater than {@link #mLoadingMinTime}
     */
    private void performRefreshComplete() {
        mStatus = PTR_STATUS_COMPLETE;
        // if is auto refresh do nothing, wait scroller stop
        if (mScrollChecker.mIsRunning && isAutoRefresh()) {
            LogUtils.d(String.format("performRefreshComplete do nothing, scrolling: %s, auto refresh: %s", mScrollChecker.mIsRunning, mFlag));
            return;
        }
        notifyUIRefreshComplete(false);
    }

    /**
     * Do real refresh work. If there is a hook, execute the hook first.
     *
     * @param ignoreHook
     */
    private void notifyUIRefreshComplete(boolean ignoreHook) {
        /**
         * After hook operation is done, {@link #notifyUIRefreshComplete} will be call in resume action to ignore hook.
         */
        if (mPtrIndicator.hasLeftStartPosition() && !ignoreHook && mRefreshCompleteHook != null) {
            LogUtils.d("notifyUIRefreshComplete mRefreshCompleteHook run.");
            mRefreshCompleteHook.takeOver();
            return;
        }
        if (mPtrUIHandlerHolder.hasHandler()) {
            LogUtils.d("PtrUIHandler: onUIRefreshComplete");
            mPtrUIHandlerHolder.onUIRefreshComplete(this);
        }
        mPtrIndicator.onUIRefreshComplete();
        tryScrollBackToTopAfterComplete();
        tryToNotifyReset();
    }

    public void autoRefresh() {
        autoRefresh(true, mDurationToCloseHeader);
    }

    public void autoRefresh(boolean atOnce) {
        autoRefresh(atOnce, mDurationToCloseHeader);
    }

    private void clearFlag() {
        // remove auto fresh flag
        mFlag = mFlag & ~MASK_AUTO_REFRESH;
    }

    public void autoRefresh(boolean atOnce, int duration) {
        if (mStatus != PTR_STATUS_INIT) {
            return;
        }
        mFlag |= atOnce ? FLAG_AUTO_REFRESH_AT_ONCE : FLAG_AUTO_REFRESH_BUT_LATER;
        mStatus = PTR_STATUS_PREPARE;
        if (mPtrUIHandlerHolder.hasHandler()) {
            mPtrUIHandlerHolder.onUIRefreshPrepare(this);
            LogUtils.d(String.format("PtrUIHandler: onUIRefreshPrepare, mFlag %s", mFlag));
        }
        mScrollChecker.tryToScrollTo(mPtrIndicator.getOffsetToRefresh(), duration);
        if (atOnce) {
            mStatus = PTR_STATUS_LOADING;
            performRefresh();
        }
    }

    public boolean isAutoRefresh() {
        return (mFlag & MASK_AUTO_REFRESH) > 0;
    }

    public boolean performAutoRefreshButLater() {
        return (mFlag & MASK_AUTO_REFRESH) == FLAG_AUTO_REFRESH_BUT_LATER;
    }

    public boolean isEnabledNextPtrAtOnce() {
        return (mFlag & FLAG_ENABLE_NEXT_PTR_ATR_ONCE) > 0;
    }

    /**
     * If @param enable has been set to true. The user can perform next PTR at once.
     *
     * @param enable
     */
    public void setEnabledNextPtrAtOnce(boolean enable) {
        if (enable) {
            mFlag = mFlag | FLAG_ENABLE_NEXT_PTR_ATR_ONCE;
        } else {
            mFlag = mFlag & ~FLAG_ENABLE_NEXT_PTR_ATR_ONCE;
        }
    }

    public boolean isPinContent() {
        return (mFlag & FLAG_PIN_CONTENT) > 0;
    }

    /**
     * The content view will now move when {@param pinContent} set to true.
     *
     * @param pinContent
     */
    public void setPinContent(boolean pinContent) {
        if (pinContent) {
            mFlag = mFlag | FLAG_PIN_CONTENT;
        } else {
            mFlag = mFlag & ~FLAG_PIN_CONTENT;
        }
    }

    /**
     * It's useful when working with viewpager.
     *
     * @param disable
     */
    public void disableWhenHorizontalMove(boolean disable) {
        mDisableWhenHorizontalMove = disable;
    }

    /**
     * loading will last at least for so long
     *
     * @param time
     */
    public void setLoadingMinTime(int time) {
        mLoadingMinTime = time;
    }

    /**
     * Not necessary any longer. Once moved, cancel event will be sent to child.
     *
     * @param yes
     */
    @Deprecated
    public void setInterceptEventWhileWorking(boolean yes) {

    }

    public View getContentView() {
        return mContent;
    }

    public void setPtrHandler(PtrHandler ptrHandler) {
        mPtrHandler = ptrHandler;
    }

    public void addPtrUIHandler(PtrUIHandler ptrUIHandler) {
        PtrUIHandlerHolder.addHandler(mPtrUIHandlerHolder, ptrUIHandler);
    }

    public void removePtrUIHandler(PtrUIHandler ptrUIHandler) {
        mPtrUIHandlerHolder = PtrUIHandlerHolder.removeHandler(mPtrUIHandlerHolder, ptrUIHandler);
    }

    public void setPtrIndicator(PtrIndicator slider) {
        if (null != mPtrIndicator && mPtrIndicator != slider) {
            slider.convertFrom(mPtrIndicator);
        }
        mPtrIndicator = slider;
    }

    public float getResistance() {
        return mPtrIndicator.getResistance();
    }

    public float getDurationToClose() {
        return mDurationToClose;
    }

    /**
     * The duration to return back to the refresh position
     *
     * @param duration
     */
    public void setDurationToClose(int duration) {
        mDurationToClose = duration;
    }

    public long getDurationToCloseHeader() {
        return mDurationToCloseHeader;
    }

    /**
     * The duration to close time
     *
     * @param duration
     */
    public void setDurationToCloseHeader(int duration) {
        mDurationToCloseHeader = duration;
    }

    public void setRatioOfHeaderHeightToRefresh(float ratio) {
        mPtrIndicator.setRatioOfHeaderHeightToRefresh(ratio);
    }

    public int getOffsetToRefresh() {
        return mPtrIndicator.getOffsetToRefresh();
    }

    public void setOffsetToRefresh(int offset) {
        mPtrIndicator.setOffsetToRefresh(offset);
    }

    public float getRatioOfHeaderToHeightRefresh() {
        return mPtrIndicator.getRatioOfHeaderHeightToRefresh();
    }

    public int getOffsetToKeepHeaderWhileLoading() {
        return mPtrIndicator.getOffsetToKeepHeaderWhileLoading();
    }

    public void setOffsetToKeepHeaderWhileLoading(int offset) {
        mPtrIndicator.setOffsetToKeepHeaderWhileLoading(offset);
    }

    public boolean isKeepHeaderWhenRefresh() {
        return mKeepHeaderWhenRefresh;
    }

    public void setKeepHeaderWhenRefresh(boolean keepOrNot) {
        mKeepHeaderWhenRefresh = keepOrNot;
    }

    public boolean isPullToRefresh() {
        return mPullToRefresh;
    }

    public void setPullToRefresh(boolean pullToRefresh) {
        mPullToRefresh = pullToRefresh;
    }

    public View getHeaderView() {
        return mHeaderView;
    }

    public void setHeaderView(View header) {
        if (null != mHeaderView && null != header && mHeaderView != header) {
            removeView(mHeaderView);
        }
        ViewGroup.LayoutParams lp = header.getLayoutParams();
        if (null == lp) {
            lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            header.setLayoutParams(lp);
        }
        mHeaderView = header;
        addView(header);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p != null && p instanceof LayoutParams;
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    private void sendCancelEvent() {
        LogUtils.d("send cancel event");
        // The ScrollChecker will update position and lead to send cancel event when mLastMoveEvent is null.
        if (null == mLastMoveEvent) {
            return;
        }
        MotionEvent last = mLastMoveEvent;
        MotionEvent e = MotionEvent.obtain(last.getDownTime(), last.getEventTime() + ViewConfiguration.getLongPressTimeout(), MotionEvent.ACTION_CANCEL, last.getX(), last.getY(), last.getMetaState());
        dispatchTouchEventSupper(e);
    }

    private void sendDownEvent() {
        LogUtils.d("send down event");
        final MotionEvent last = mLastMoveEvent;
        MotionEvent e = MotionEvent.obtain(last.getDownTime(), last.getEventTime(), MotionEvent.ACTION_DOWN, last.getX(), last.getY(), last.getMetaState());
        dispatchTouchEventSupper(e);
    }

    public static class LayoutParams extends MarginLayoutParams {
        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }

    class ScrollChecker implements Runnable {
        private int mLastFlingY;
        private Scroller mScroller;
        private boolean mIsRunning = false;
        private int mStart;
        private int mTo;

        public ScrollChecker() {
            mScroller = new Scroller(getContext());
        }

        @Override
        public void run() {
            boolean finish = !mScroller.computeScrollOffset() || mScroller.isFinished();
            int curY = mScroller.getCurrY();
            int deltaY = curY - mLastFlingY;
            if (deltaY != 0) {
                LogUtils.d(String.format("scroll: %s, start: %s, to: %s, currentPos: %s, current: %s, last: %s, delta: %s", finish, mStart, mTo, mPtrIndicator.getCurrentPosY(), curY, mLastFlingY, deltaY));
            }
            if (!finish) {
                mLastFlingY = curY;
                movePos(deltaY);
                post(this);
            } else {
                finish();
            }
        }

        private void finish() {
            LogUtils.d(String.format("finish, currentPos: %s", mPtrIndicator.getCurrentPosY()));
            reset();
            onPtrScrollFinish();
        }

        private void reset() {
            mIsRunning = false;
            mLastFlingY = 0;
            removeCallbacks(this);
        }

        private void destroy() {
            reset();
            if (!mScroller.isFinished()) {
                mScroller.forceFinished(true);
            }
        }

        public void abortIfWorking() {
            if (mIsRunning) {
                if (!mScroller.isFinished()) {
                    mScroller.forceFinished(true);
                }
                onPtrScrollAbort();
                reset();
            }
        }

        public void tryToScrollTo(int to, int duration) {
            if (mPtrIndicator.isAlreadyHere(to)) {
                return;
            }
            mStart = mPtrIndicator.getCurrentPosY();
            mTo = to;
            int distance = to - mStart;
            LogUtils.d(String.format("tryToScrollTo: start: %s, distance: %s, to: %s", mStart, distance, to));
            removeCallbacks(this);
            mLastFlingY = 0;
            if (!mScroller.isFinished()) {
                mScroller.forceFinished(true);
            }
            mScroller.startScroll(0, 0, 0, distance, duration);
            post(this);
            mIsRunning = true;
        }
    }
}
