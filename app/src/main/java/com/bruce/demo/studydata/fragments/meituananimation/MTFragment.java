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

package com.bruce.demo.studydata.fragments.meituananimation;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.bruce.demo.R;
import com.bruce.demo.base.BaseFragment;
import com.bruce.demo.studydata.fragments.meituananimation.widget.MTListView;
import com.bruce.demo.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 美团下拉刷新动画
 * Created by BruceHurrican on 2015/12/27.
 */
public class MTFragment extends BaseFragment implements MTListView.OnMTRefreshListener {
    private static final int REFRESH_COMPLETE = 0;
    @Bind(R.id.mt_listView)
    MTListView mt_listView;
    private List<String> data;
    private ArrayAdapter<String> adapter;


    @Override
    public String getTAG() {
        return "MTFragment";
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mt_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        data = new ArrayList<>(10);
        for (int i = 0; i < 15; i++) {
            data.add("测试数据" + i);
        }
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, data);
        mt_listView.setAdapter(adapter);
        mt_listView.setOnMTRefreshListener(this);
        initUIHandler();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onRefresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    for (int i = 0; i < 5; i++) {
                        data.add("增加数据" + i);
                    }
                    getUIHandler().sendEmptyMessage(REFRESH_COMPLETE);
                } catch (InterruptedException e) {
                    LogUtils.e(e.toString());
                }
            }
        }).start();
    }

    @Override
    public void handleUIMessage(Message msg) {
        switch (msg.what) {
            case REFRESH_COMPLETE:
                mt_listView.setOnRefreshComplete();
                adapter.notifyDataSetChanged();
                mt_listView.setSelection(0);
                break;
        }
    }
}
