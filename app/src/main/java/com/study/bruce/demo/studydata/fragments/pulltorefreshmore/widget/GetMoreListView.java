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
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.study.bruce.demo.R;

/**
 * 自定义加载更多控件
 * Created by BruceHurrican on 2015/12/20.
 */
public class GetMoreListView extends ListView {
    // 加载更多接口
    public interface OnGetMoreListener {
        void onGetMore();
    }

    private static final String TAG = GetMoreListView.class.getSimpleName();
    private LayoutInflater inflater;
    // 加载更多视图(底部视图)
    private View footView;
    // 加载更多文字
    private TextView tvFootTitle;
    // 加载更多忙碌框
    private ProgressBar pbFootRefreshing;
    // 是否已经添加了 footer
    private boolean addFooterFlag;
    // 是否还有数据标志
    private boolean hasMoreDataFlag = true;
    /**
     * Scroll 时到达最后一个 Item 的次数，只有第一次触发自动刷新
     */
    private int reachLastPositionCount = 0;
    private OnGetMoreListener getMoreListener;
    private boolean isGetMoreing = false;

    public GetMoreListView(Context context) {
        this(context, null);
    }

    public GetMoreListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GetMoreListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    /**
     * 控件初始化
     *
     * @param context
     * @param attrs
     */
    private void init(Context context, AttributeSet attrs) {
        inflater = LayoutInflater.from(context);
        // 底部
        footView = inflater.inflate(R.layout.ptfm_view_get_more_lv_foot, this, false);
        tvFootTitle = (TextView) footView.findViewById(R.id.tv_foot_title);
        pbFootRefreshing = (ProgressBar) footView.findViewById(R.id.pb_foot_refreshing);
        // 滑动监听
        setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                doOnScrollStateChanged(view, scrollState);
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                doOnScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
            }
        });
    }

    // 加载更多
    private void getMore() {
        if (null != getMoreListener) {
            isGetMoreing = true;
            pbFootRefreshing.setVisibility(VISIBLE);
            tvFootTitle.setText("正在加载...");
            getMoreListener.onGetMore();
        }
    }

    /**
     * 判断是否可以自动加载更多
     *
     * @return
     */
    private boolean checkCanAutoGetMore() {
        if (null == footView) {
            return false;
        }
        if (null == getMoreListener) {
            return false;
        }
        if (isGetMoreing) {
            return false;
        }
        if (!hasMoreDataFlag) {
            return false;
        }
        if (null == getAdapter()) {
            return false;
        }
        if (!canScroll(1) && !canScroll(-1)) {
            return false;
        }
        if (getLastVisiblePosition() != getAdapter().getCount() - 1) {
            return false;
        }
        return reachLastPositionCount == 1;

    }

    /**
     * 判断 listView 是否可以滑动
     *
     * @param direction
     * @return
     */
    private boolean canScroll(int direction) {
        final int childCount = getChildCount();
        if (0 == childCount) {
            return false;
        }

        final int firstPosition = getFirstVisiblePosition();
        final int listPaddingTop = getPaddingTop();
        final int listPaddingBottom = getPaddingTop();
        final int itemCount = getAdapter().getCount();
        if (direction > 0) {
            final int lastBottom = getChildAt(childCount - 1).getBottom();
            final int lastPosition = firstPosition + childCount;
            return lastPosition < itemCount || lastBottom > getHeight() - listPaddingBottom;
        } else {
            final int firstTop = getChildAt(0).getTop();
            return firstPosition > 0 || firstTop < listPaddingTop;
        }
    }

    /**
     * 设置加载更多监听器
     *
     * @param getMoreListener
     */
    public void setOnGetMoreListener(OnGetMoreListener getMoreListener) {
        this.getMoreListener = getMoreListener;
        if (!addFooterFlag) {
            addFooterFlag = true;
            this.addFooterView(footView);
        }
    }

    /**
     * 加载更多完成
     */
    public void getMoreComplete() {
        isGetMoreing = false;
        pbFootRefreshing.setVisibility(GONE);
        tvFootTitle.setText("加载更多");
    }

    /**
     * 设置没有更多的数据了
     * 不再显示加载更多按钮
     */
    public void setNoMore() {
        hasMoreDataFlag = false;
        if (null != footView) {
            footView.setVisibility(GONE);
        }
    }

    /**
     * 显示加载更多按钮
     */
    public void setHasMore() {
        hasMoreDataFlag = true;
        if (null != footView) {
            footView.setVisibility(VISIBLE);
        }
    }

    /**
     * 如果项目中其他地方需要重新设置 GetMoreListView 的 OnScrollListener
     * 请在新的 listener 中 onScrollStateChanged 方法内调用此方法，保证 PullListView 正常运行
     *
     * @param view
     * @param scrollState
     */
    public void doOnScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
            case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
                break;
            case OnScrollListener.SCROLL_STATE_FLING:
                // 滑动时候停止加载图片
                // todo 用其他控件替代
//                ImageLoader.getInstance().pause();
                break;
            case OnScrollListener.SCROLL_STATE_IDLE:
                // 停下后恢复加载图片
                // todo 用其他控件替代
//                ImageLoader.getInstance().resume();
                break;
            default:
                break;
        }
    }

    /**
     * 如果项目中其他地方需要重新设置 GetMoreListView 的 OnScrollListener
     * 请在新的 listener 中 onScroll 方法内调用此方法，保证 PullListView 正常运行
     *
     * @param view
     * @param firstVisibleItem
     * @param visibleItemCount
     * @param totalItemCount
     */
    public void doOnScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (null == getAdapter()) {
            return;
        }
        if (getLastVisiblePosition() == getAdapter().getCount() - 1) {
            reachLastPositionCount++;
        } else {
            reachLastPositionCount = 0;
        }
        if (checkCanAutoGetMore()) {
            getMore();
        }
    }
}
