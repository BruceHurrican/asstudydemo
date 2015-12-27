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

package com.study.bruce.demo.studydata.fragments.meituananimation.widget;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.study.bruce.demo.R;
import com.study.bruce.demo.utils.LogUtils;

/**
 * Created by BruceHurrican on 2015/12/27.
 */
public class MTListView extends ListView implements AbsListView.OnScrollListener {
    public static final int DONE = 0;
    public static final int PULL_TO_REFRESH = 1;
    public static final int RELEASE_TO_REFRESH = 2;
    public static final int REFRESHING = 3;
    public static final int RATIO = 3;
    private LinearLayout headerView;
    private int headerViewHeight;
    private float startY, offsetY;
    private TextView tv_pull_to_refresh;
    private OnMTRefreshListener mtRefreshListener;
    private int state, mFirstVisibleItem;
    private boolean isRecord, isEnd, isRefreshable;
    private MTRefreshFirstStepView mtRefreshFirstStepView;
    private MTRefreshSecondStepView mtRefreshSecondStepView;
    private MTRefreshThirdStepView mtRefreshThirdStepView;
    private AnimationDrawable secondAnim, thirdAnim;

    public MTListView(Context context) {
        this(context, null);
    }

    public MTListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MTListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setOverScrollMode(View.OVER_SCROLL_NEVER);
        setOnScrollListener(this);
        headerView = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.mt_item, null, false);
        mtRefreshFirstStepView = (MTRefreshFirstStepView) headerView.findViewById(R.id.mt_first);
        mtRefreshSecondStepView = (MTRefreshSecondStepView) headerView.findViewById(R.id.mt_second);
        mtRefreshThirdStepView = (MTRefreshThirdStepView) headerView.findViewById(R.id.mt_third);
        tv_pull_to_refresh = (TextView) headerView.findViewById(R.id.tv_pull_to_refresh);

        mtRefreshSecondStepView.setBackgroundResource(R.drawable.mt_anim_pull_to_refresh_second);
        secondAnim = (AnimationDrawable) mtRefreshSecondStepView.getBackground();
        mtRefreshThirdStepView.setBackgroundResource(R.drawable.mt_anim_pull_to_refresh_third);
        thirdAnim = (AnimationDrawable) mtRefreshThirdStepView.getBackground();

        measureView(headerView);
        addHeaderView(headerView);
        headerViewHeight = headerView.getMeasuredHeight();
        headerView.setPadding(0, -headerViewHeight, 0, 0);
        LogUtils.d("headerViewHeight= " + headerViewHeight);

        state = DONE;
        isEnd = true;
        isRefreshable = false;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        mFirstVisibleItem = firstVisibleItem;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (isEnd && isRefreshable) { // 如果现在是结束的状态，即刷新完毕了，可以再次刷新了，在 onRefreshComplete 中设置
            // 如果现在是可刷新状态， 在 setOnMTRefreshListener 中设置为 true
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    // 如果当前是在 listView 顶部并且没有记录 y 坐标
                    if (mFirstVisibleItem == 0 && !isRecord) {
                        isRecord = true; // 表明现在已经记录 y 坐标
                        startY = ev.getY();
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    float tempY = ev.getY(); // 再次得到 y 坐标，用来和 startY 来计算 offsetY 值
                    if (mFirstVisibleItem == 0 && !isRecord) {
                        isRecord = true;
                        startY = tempY;
                    }
                    // 如果当前状态不是正在刷新的状态，并且已经记录了 y 坐标
                    if (state != REFRESHING && isRecord) {
                        offsetY = tempY - startY;
                        float currentHeight = (-headerViewHeight + offsetY / 3);
                        // 用当前滑动的高度和头部 headerView 的总高度进行比，计算当前滑动的百分比 0到1
                        float currentProgress = 1 + currentHeight / headerViewHeight;
                        if (currentProgress >= 1) {
                            currentProgress = 1; // 目的是让第一个状态的椭圆不再继续变大
                        }
                        // 如果当前的状态是放开刷新，并且已经记录 y 坐标
                        if (state == RELEASE_TO_REFRESH && isRecord) {
                            setSelection(0);
                            // 如果当前滑动的距离小于 headerView 的总高度
                            if (-headerViewHeight + offsetY / RATIO < 0) {
                                state = PULL_TO_REFRESH;
                                changeHeaderByState(state);
                            } else if (offsetY <= 0) { // headerView 隐藏
                                state = DONE;
                                changeHeaderByState(state);
                            }
                        }
                        if (state == PULL_TO_REFRESH && isRecord) {
                            setSelection(0);
                            // 如果下拉距离大于等于 headerView 的总高度
                            if (-headerViewHeight + offsetY >= 0) {
                                state = RELEASE_TO_REFRESH;
                                changeHeaderByState(state);
                            } else if (offsetY <= 0) {
                                state = DONE;
                                changeHeaderByState(state);
                            }
                        }
                        if (state == DONE && isRecord && offsetY >= 0) {
                            state = PULL_TO_REFRESH;
                        }
                        if (state == PULL_TO_REFRESH) {
                            // 改变 headerView 的 padding 来实现下拉的效果
                            headerView.setPadding(0, (int) (-headerViewHeight + offsetY / RATIO), 0, 0);
                            mtRefreshFirstStepView.setCurrentProgress(currentProgress);
                            mtRefreshFirstStepView.postInvalidate(); // 重新绘制界面
                        }
                        if (state == RELEASE_TO_REFRESH) {
                            headerView.setPadding(0, (int) (-headerViewHeight + offsetY / RATIO), 0, 0);
                            mtRefreshFirstStepView.setCurrentProgress(currentProgress);
                            mtRefreshFirstStepView.postInvalidate();
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (state == PULL_TO_REFRESH) {
                        this.smoothScrollBy((int) ((-headerViewHeight + offsetY / RATIO) + headerViewHeight), 500); // 平滑的隐藏 headerView
                        changeHeaderByState(state);
                    }
                    if (state == RELEASE_TO_REFRESH) {
                        this.smoothScrollBy((int) (-headerViewHeight + offsetY / RATIO), 500);
                        state = REFRESHING;
                        mtRefreshListener.onRefresh();
                        changeHeaderByState(state);
                    }
                    isRecord = false;
                    break;
            }
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 回调方法，实现下拉刷新的 listView 需调用此方法
     *
     * @param onMTRefreshListener
     */
    public void setOnMTRefreshListener(OnMTRefreshListener onMTRefreshListener) {
        mtRefreshListener = onMTRefreshListener;
        isRefreshable = true;
    }

    /**
     * 刷新完毕，从主线程发送过来，并且改变 headerView 的状态和文字动画信息
     */
    public void setOnRefreshComplete() {
        // 一定要将 isEnd 设置为 true， 以便于下次的下拉刷新
        isEnd = true;
        state = DONE;
        changeHeaderByState(state);
    }

    /**
     * 根据状态改变 headerView 的动画和文字显示
     *
     * @param pState
     */
    private void changeHeaderByState(int pState) {
        switch (pState) {
            case DONE:
                // 当前状态是隐藏，设置 headerView 的 padding 为隐藏
                tv_pull_to_refresh.setText("下拉刷新");
                headerView.setPadding(0, -headerViewHeight, 0, 0);
                mtRefreshFirstStepView.setVisibility(VISIBLE);
                mtRefreshSecondStepView.setVisibility(GONE);
                secondAnim.stop();
                mtRefreshThirdStepView.setVisibility(GONE);
                thirdAnim.stop();
                break;
            case RELEASE_TO_REFRESH:
                // 当前状态是 放开刷新
                tv_pull_to_refresh.setText("放开刷新");
                mtRefreshFirstStepView.setVisibility(GONE);
                mtRefreshSecondStepView.setVisibility(VISIBLE);
                secondAnim.start();
                mtRefreshFirstStepView.setVisibility(GONE);
                thirdAnim.stop();
                break;
            case PULL_TO_REFRESH:
                // 当前状态为下拉刷新
                tv_pull_to_refresh.setText("下拉刷新");
                mtRefreshFirstStepView.setVisibility(VISIBLE);
                mtRefreshSecondStepView.setVisibility(GONE);
                secondAnim.stop();
                mtRefreshThirdStepView.setVisibility(GONE);
                thirdAnim.stop();
                break;
            case REFRESHING:
                tv_pull_to_refresh.setText("正在刷新...");
                mtRefreshFirstStepView.setVisibility(GONE);
                mtRefreshSecondStepView.setVisibility(GONE);
                secondAnim.stop();
                mtRefreshThirdStepView.setVisibility(VISIBLE);
                thirdAnim.start();
                break;
        }
    }

    private void measureView(View child) {
        ViewGroup.LayoutParams params = child.getLayoutParams();
        if (null == params) {
            params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, params.width);
        int lpHeight = params.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    public interface OnMTRefreshListener {
        void onRefresh();
    }
}
