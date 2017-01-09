/*
 * BruceHurrican
 * Copyright (c) 2016.
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 *    This document is Bruce's individual learning the android demo, wherein the use of the code from the Internet, only to use as a learning exchanges.
 *    And where any person can download and use, but not for commercial purposes.
 *    Author does not assume the resulting corresponding disputes.
 *    If you have good suggestions for the code, you can contact BurrceHurrican@foxmail.com
 *    本文件为Bruce's个人学习android的作品, 其中所用到的代码来源于互联网，仅作为学习交流使用。
 *    任和何人可以下载并使用, 但是不能用于商业用途。
 *    作者不承担由此带来的相应纠纷。
 *    如果对本代码有好的建议，可以联系BurrceHurrican@foxmail.com
 */

package com.bruce.demo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bruceutils.base.BaseFragmentActivity;
import com.bruceutils.utils.LogUtils;
import com.bruceutils.utils.logdetails.LogDetails;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 主模块入口
 * Created by BruceHurrican on 17/1/6.
 */

public class BlueActivity extends BaseFragmentActivity {

    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.btn_list)
    Button btnList;
    @Bind(R.id.btn_grid)
    Button btnGrid;
    @Bind(R.id.btn_db_back)
    Button btnDbBack;
    @Bind(R.id.btn_db_restore)
    Button btnDbRestore;
    @Bind(R.id.rv_container)
    RecyclerView rvContainer;
    @Bind(R.id.rl_blue_container)
    RelativeLayout rlBlueContainer;

    private DemosAdapter adapter;
    private List<String> dataList = new ArrayList<>(10);
    private Intent intent;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.blue_activity);
        ButterKnife.bind(this);
        LogDetails.d("init ok");
        LogUtils.d("init ok");

        fragmentManager = getSupportFragmentManager();

        adapter = new DemosAdapter();

        dataList.add("LibActivity");
        dataList.add("BlueFragment");

        adapter.setDataList(dataList);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,
                false);
        rvContainer.setLayoutManager(manager);

        rvContainer.setAdapter(adapter);

        intent = new Intent();

        adapter.setItemClickListener(new DemosAdapter.CardViewItemClickListener() {
            @Override
            public void itemClick(View view, int position) {
                String flag = dataList.get(position);
                if (flag.equals("LibActivity")) {
                    intent.setClass(BlueActivity.this, LibActiviy.class);
                    startActivity(intent);
                } else if (flag.equals("BlueFragment")) {
                    fragmentManager.beginTransaction().replace(R.id.rl_blue_container, new
                            BlueFragment()).addToBackStack(null).commit();
                }

                if (flag.toUpperCase().contains("FRAGMENT")) {
                    changeContainerVisible();
                }
            }
        });
    }

    private void changeContainerVisible() {
        if (!rlBlueContainer.isShown()) {
            rlBlueContainer.setVisibility(View.VISIBLE);
        }
        if (rvContainer.isShown()) {
            rvContainer.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.btn_list, R.id.btn_grid, R.id.btn_db_back, R.id.btn_db_restore})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_list:
                LinearLayoutManager manager = new LinearLayoutManager(BlueActivity.this,
                        LinearLayoutManager.VERTICAL, false);
                rvContainer.setLayoutManager(manager);
                break;
            case R.id.btn_grid:
                StaggeredGridLayoutManager staggeredGridLayoutManager = new
                        StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
                rvContainer.setLayoutManager(staggeredGridLayoutManager);
                break;
            case R.id.btn_db_back:
                break;
            case R.id.btn_db_restore:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        // 将入栈的 fragment 按 FILO 规则依次出栈
        if (fragmentManager.getBackStackEntryCount() > 0 && fragmentManager.popBackStackImmediate
                (null, 0)) {
            LogUtils.d("fragment栈中最上层的 fragment 出栈");
            if (fragmentManager.getBackStackEntryCount() == 0) {
                if (rlBlueContainer.isShown()) {
                    rlBlueContainer.setVisibility(View.GONE);
                    rvContainer.setVisibility(View.VISIBLE);
                }
            }
            return;
        }
        if (rlBlueContainer.isShown()) {
            rlBlueContainer.setVisibility(View.GONE);
            rvContainer.setVisibility(View.VISIBLE);
        }
        super.onBackPressed();
    }
}
