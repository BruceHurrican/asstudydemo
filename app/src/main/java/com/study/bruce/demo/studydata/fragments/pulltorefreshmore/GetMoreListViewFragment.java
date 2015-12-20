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

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;

import com.study.bruce.demo.R;
import com.study.bruce.demo.base.BaseFragment;
import com.study.bruce.demo.studydata.fragments.pulltorefreshmore.widget.GetMoreListView;

import java.util.ArrayList;
import java.util.List;

/**
 * 下拉刷新和加载更多 练习
 * Created by BruceHurrican on 2015/12/20.
 */
public class GetMoreListViewFragment extends BaseFragment {
    private static final int PAGE_NUM = 10;
    private GetMoreListView gmlvMain;
    private BaseAdapter adapter;
    private FrameLayout fl_ptfm;
    private int currentPage = 0;
    private Handler handler;
    private List<String> imageUrlList;

    @Override
    public String getTAG() {
        return "GetMoreListViewFragment";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handler = new Handler();
        imageUrlList = new ArrayList<>(5);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.ptfm_fragment, container, false);
        initView(v);
        return v;
    }

    private void initView(View v) {
        gmlvMain = (GetMoreListView) v.findViewById(R.id.gmlv_main);
        gmlvMain.setOnGetMoreListener(new GetMoreListView.OnGetMoreListener() {
            @Override
            public void onGetMore() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getData(false);
                    }
                }, 2000);
            }
        });

        adapter = new ListViewAdapter(getActivity(), imageUrlList);
        gmlvMain.setAdapter(adapter);
        fl_ptfm = (FrameLayout) v.findViewById(R.id.fl_ptfm);
        final Windmill
    }
}
