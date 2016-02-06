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

package com.bruce.demo.studydata.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bruce.demo.R;
import com.bruce.demo.base.BaseFragmentActivity;
import com.bruce.demo.studydata.fragments.crash.CrashFragment;
import com.bruce.demo.studydata.fragments.database.DBFragment;
import com.bruce.demo.studydata.fragments.googlesample.api10.contact_manager.ContactManagerFragment;
import com.bruce.demo.studydata.fragments.googlesample.templates.BlankFragment;
import com.bruce.demo.studydata.fragments.io.IOFragment;
import com.bruce.demo.studydata.fragments.meituananimation.MTFragment;
import com.bruce.demo.studydata.fragments.rotate3danimation.Rotate3dFragment;
import com.bruce.demo.studydata.fragments.webviewjs.JSWebviewFragment;
import com.bruce.demo.studydata.fragments.widgetdemo.WidgetFragment;
import com.bruce.demo.utils.LogUtils;
import com.bruce.demo.widget.AnimListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * fragment 入口
 * Created by BruceHurrican on 2015/11/15.
 */
public class FragmentsActivity extends BaseFragmentActivity implements AdapterView.OnItemClickListener {
    @Bind(R.id.tv_title)
    TextView tv_title;
    @Bind(R.id.alv_fragment_list)
    AnimListView alv_fragment_list;
    @Bind(R.id.rl_container)
    RelativeLayout rl_container; // 显示 fragment 容器
    private List<Fragment> fragments;
    private List<String> fragmentNamesList;
    private FragmentManager fragmentManager;

    @Override
    public String getTAG() {
        return "FragmentsActivity->";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_activity);
        ButterKnife.bind(this);
        LogUtils.i("当前进程ID：" + android.os.Process.myPid());
        fragmentManager = getSupportFragmentManager();
        initContainer();
    }

    @Override
    protected void onDestroy() {
        ButterKnife.unbind(this);
        super.onDestroy();
    }

    private void initContainer() {
        fragments = new ArrayList<>(5);
        fragmentNamesList = new ArrayList<>(5);
        alv_fragment_list.setAdapter(new ArrayAdapter<>(this, R.layout.main_item, fragmentNamesList));

        addFragment2Container(new WidgetFragment(), "系统控件练习");
        addFragment2Container(new DBFragment(), "数据库");
        addFragment2Container(new IOFragment(), "文件储存");
        addFragment2Container(new JSWebviewFragment(), "webview js 交互");
        addFragment2Container(new Rotate3dFragment(), "利用 Camera 实现 3D 卡片翻转动画");
        addFragment2Container(new MTFragment(), "美团下拉刷新动画学习");
        addFragment2Container(new CrashFragment(), "测试 日志生成删除应用缓存本地文件");
        addFragment2Container(new ContactManagerFragment(), "查看联系人");
        addFragment2Container(new BlankFragment(), "谷歌模板-Blank");

        alv_fragment_list.setOnItemClickListener(this);
        LogUtils.i("加载 fragment 列表完成");
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
        showToastShort(String.format("你点击了第 %s 条Demo %s", position + 1, fragmentNamesList.get(position)));
        LogUtils.d(String.format("你点击了第 %s 条Demo %s", position + 1, fragmentNamesList.get(position)));
        LogUtils.i("当前线程为 -->" + Thread.currentThread());
        // setCustomAnimations 要写在 addToBackStack，replace 方法前面，否则没有效果
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        fragmentTransaction.addToBackStack(fragmentNamesList.get(position));
        fragmentTransaction.replace(R.id.rl_container, fragments.get(position));
        fragmentTransaction.commit();
        if (!rl_container.isShown()) {
            rl_container.setVisibility(View.VISIBLE);
        }
        if (alv_fragment_list.isShown()) {
            alv_fragment_list.setVisibility(View.GONE);
        }
    }

    public void fragment2fragment(Fragment fragment, String tag) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.rl_container, fragment);
        fragmentTransaction.addToBackStack(tag);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        // 将入栈的 fragment 按 FILO 规则依次出栈
        if (fragmentManager.getBackStackEntryCount() > 0 && fragmentManager.popBackStackImmediate(null, 0)) {
            LogUtils.d("fragment栈中最上层的 fragment 出栈");
            if (fragmentManager.getBackStackEntryCount() == 0) {
                if (rl_container.isShown()) {
                    rl_container.setVisibility(View.GONE);
                    alv_fragment_list.setVisibility(View.VISIBLE);
                }
            }
            return;
        }
        if (rl_container.isShown()) {
            rl_container.setVisibility(View.GONE);
            alv_fragment_list.setVisibility(View.VISIBLE);
        }
        super.onBackPressed();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }
}
