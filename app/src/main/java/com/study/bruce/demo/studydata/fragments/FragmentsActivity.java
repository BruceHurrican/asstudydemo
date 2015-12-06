/*
 * Copyright (c) 2015.
 *   This document is just for Bruce's personal study.
 *   Some resources come from the Internet. Everyone can download and use it for study, but can
 *   not be used for commercial purpose. The author does not bear the
 *   corresponding disputes arising therefrom.
 *   Please delete within 24 hours after download.
 *   If you have good suggestions for this code, you can contact BurrceHurrican@foxmail.com.
 *   本文件为Bruce's个人学习android的demo, 其中所用到的代码来源于互联网，仅作为学习交流使用。
 *   任和何人可以下载并使用, 但是不能用于商业用途。
 *   作者不承担由此带来的相应纠纷。
 *   如果对本代码有好的建议，可以联系BurrceHurrican@foxmail.com
 */

package com.study.bruce.demo.studydata.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.study.bruce.demo.R;
import com.study.bruce.demo.base.BaseFragmentActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * fragment 入口
 * Created by BruceHurrican on 2015/11/15.
 */
public class FragmentsActivity extends BaseFragmentActivity implements AdapterView.OnItemClickListener {
    private List<Fragment> fragments;
    private List<String> fragmentNamesList;
    private FragmentManager fragmentManager;
    private RelativeLayout rl_container; // 显示 fragment 容器
    private ListView lv_demo_list;

    @Override
    public String getTAG() {
        return "FragmentsActivity->";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_activity);
        fragmentManager = getSupportFragmentManager();
        initContainer();
    }

    private void initContainer() {
        fragments = new ArrayList<>(5);
        fragmentNamesList = new ArrayList<>(5);
        rl_container = (RelativeLayout) findViewById(R.id.rl_container);
        lv_demo_list = (ListView) findViewById(R.id.lv_fragment_list);
        lv_demo_list.setAdapter(new ArrayAdapter<>(this, R.layout.main_item, fragmentNamesList));

//        addFragment2Container(new JsonFragment(), "json练习");

        lv_demo_list.setOnItemClickListener(this);
        logI("加载 fragment 列表完成");
    }

    /**
     * @param fragment
     * @param fragmentName
     */
    private void addFragment2Container(Fragment fragment, String fragmentName) {
        fragmentNamesList.add(fragmentName);
        fragments.add(fragment);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        logI(String.format("你点击了第 %s 条Demo %s", position + 1, fragmentNamesList.get(position)));
        logI("当前线程为 -->" + Thread.currentThread());
        fragmentTransaction.replace(R.id.rl_container, fragments.get(position));
        fragmentTransaction.addToBackStack(fragmentNamesList.get(position));
        fragmentTransaction.commit();
        if (!rl_container.isShown()) {
            rl_container.setVisibility(View.VISIBLE);
        }
        if (lv_demo_list.isShown()) {
            lv_demo_list.setVisibility(View.GONE);
        }
    }

    @Override
    public void onBackPressed() {
        // 将入栈的 fragment 按 FILO 规则依次出栈
        if (fragmentManager.popBackStackImmediate(null, 0)) {
            logD("fragment栈中最上层的 fragment 出栈");
            return;
        }
        if (rl_container.isShown()) {
            rl_container.setVisibility(View.GONE);
            lv_demo_list.setVisibility(View.VISIBLE);
        }
        super.onBackPressed();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }
}
