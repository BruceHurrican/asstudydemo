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

package com.study.bruce.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.study.bruce.MainActivityDemo;
import com.study.bruce.demo.base.BaseActivity;
import com.study.bruce.demo.studydata.fragments.FragmentsActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 主Activity
 * Created by BruceHurrican on 2015/5/24.
 */
public class MainActivity extends BaseActivity implements AdapterView.OnItemClickListener, Serializable {
    private static final long serialVersionUID = -3277762441808693645L;
    private List<Class<? extends Activity>> demos;
    private List<String> demoNamesList;
    private Intent it;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        initContainer();
    }

    @Override
    public String getTAG() {
        return "MainActivity -- >";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * 初始化demo容器
     */
    private void initContainer() {
        demos = new ArrayList<>(5);
        demoNamesList = new ArrayList<>(5);
        ListView lv_demo_list = (ListView) findViewById(R.id.lv__demo_list);
//        lv_demo_list.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, demoNamesList));
        lv_demo_list.setAdapter(new ArrayAdapter<>(this, R.layout.main_item, demoNamesList));
        it = new Intent();

        addDemoContainer(FragmentsActivity.class, "fragment 入口");
        addDemoContainer(MainActivityDemo.class, "谷歌AS模板");

        lv_demo_list.setOnItemClickListener(this);
        logI("加载列表完成");
    }

    /**
     * 增加demo
     *
     * @param cls  demo class
     * @param name demo 名称
     */
    private void addDemoContainer(Class<? extends Activity> cls, String name) {
        demos.add(cls);
        demoNamesList.add(name);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//        Toast.makeText(MainActivity.this, "你点击了第" + (position + 1) + "条Demo", Toast.LENGTH_SHORT).show();
//        showToastShort("你点击了第" + (position + 1) + "条Demo--" + demoNamesList.get(position));
        showToastShort(String.format("你点击了第 %s 条Demo %s", position + 1, demoNamesList.get(position)));
        it.setClass(MainActivity.this, demos.get(position));
//        Logs.i(TAG, "你点击了第" + (position + 1) + "条Demo--"+demoNamesList.get(position));
        logI(String.format("你点击了第 %s 条Demo %s", position + 1, demoNamesList.get(position)));
        logI("当前线程为 -->" + Thread.currentThread());
        startActivity(it);
    }
}
