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

package com.bruce.demo;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.bruce.demo.base.BaseActivity;
import com.bruce.demo.facebook.stetho.StethoDemoActivity;
import com.bruce.demo.mvp.view.MVPActivity;
import com.bruce.demo.social.qq.QQActivity;
import com.bruce.demo.studydata.activities.media.CameraActivity;
import com.bruce.demo.studydata.fragments.FragmentsActivity;
import com.bruce.demo.studydata.game.game2048.activity.GameActivity;
import com.bruce.demo.utils.DataCleanManager;
import com.bruce.demo.utils.LogUtils;
import com.bruce.demo.utils.PublicUtil;
import com.bruce.demo.widget.AnimListView;
import com.bruce.demo.widget.TitleBar;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;

/**
 * 主Activity
 * Created by BruceHurrican on 2015/5/24.
 */
public class MainActivity extends BaseActivity implements AdapterView.OnItemClickListener, Serializable, TitleBar.OnTitleBarClickListener {
    private static final long serialVersionUID = -3277762441808693645L;
    @Bind(R.id.titlebar)
    TitleBar titlebar;
    @Bind(R.id.alv_demo_list)
    AnimListView alv_demo_list;
    @Bind(R.id.bottomBar)
    RadioGroup bottomBar;
    @Bind(R.id.btn_favourite)
    RadioButton btn_favourite;
    @Bind(R.id.btn_search)
    RadioButton btn_search;
    @Bind(R.id.btn_pocket)
    RadioButton btn_pocket;
    @Bind(R.id.btn_mine)
    RadioButton btn_mine;
    private List<Class<? extends Activity>> demos;
    private List<String> demoNamesList;
    private Intent it;
    private NetWorkAvailableReceiver netWorkAvailableReceiver = new NetWorkAvailableReceiver();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        LogUtils.i("当前进程ID：" + android.os.Process.myPid());
        initContainer();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netWorkAvailableReceiver, filter);
        titlebar.setOnTitleBarClickListener(this);
        bottomBar.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.btn_favourite:
                        LogUtils.d("收藏按钮被点击");
                        break;
                    case R.id.btn_search:
                        LogUtils.d("搜索按钮被点击");
                        break;
                    case R.id.btn_pocket:
                        LogUtils.d("钱包按钮被点击");
                        break;
                    case R.id.btn_mine:
                        LogUtils.d("我的按钮被点击");
                        break;
                }
            }
        });
    }

    @Override
    public String getTAG() {
        return "MainActivity -- >";
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(netWorkAvailableReceiver);
        ButterKnife.unbind(this);
        super.onDestroy();
//        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * 初始化demo容器
     */
    private void initContainer() {
        demos = new ArrayList<>(5);
        demoNamesList = new ArrayList<>(5);
//        ListView lv_demo_list = (ListView) findViewById(R.id.lv__demo_list);
//        lv_demo_list.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, demoNamesList));
        alv_demo_list.setAdapter(new ArrayAdapter<>(this, R.layout.main_item, demoNamesList));
        it = new Intent();

        addDemoContainer(FragmentsActivity.class, "fragment 入口");
        addDemoContainer(QQActivity.class, "QQ 登录分享 QQ空间分享");
        addDemoContainer(CameraActivity.class, "调用系统相机相册获取相片");
        addDemoContainer(GameActivity.class, "2048游戏");
        addDemoContainer(StethoDemoActivity.class, "stetho demo");
        addDemoContainer(MVPActivity.class, "mvp demo");

        alv_demo_list.setOnItemClickListener(this);
        LogUtils.i("加载列表完成");
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
        LogUtils.i(String.format("你点击了第 %s 条Demo %s", position + 1, demoNamesList.get(position)));
        LogUtils.i("当前线程为 -->" + Thread.currentThread());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(it, ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle());
        } else {
            startActivity(it);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @OnCheckedChanged({R.id.btn_favourite, R.id.btn_search, R.id.btn_pocket, R.id.btn_mine})
    public void bottomBarBtnTxtPressed(CompoundButton buttonView, boolean isChecked) {
        LogUtils.d("buttonView:" + buttonView + "\nisChecked:" + isChecked);
        switch (buttonView.getId()) {
            case R.id.btn_favourite:
                btn_favourite.setTextColor(getResources().getColor(isChecked ? R.color.bottombartxt_pressed : R.color.bottombartxt_unpressed));
                break;
            case R.id.btn_search:
                btn_search.setTextColor(getResources().getColor(isChecked ? R.color.bottombartxt_pressed : R.color.bottombartxt_unpressed));
                break;
            case R.id.btn_pocket:
                btn_pocket.setTextColor(getResources().getColor(isChecked ? R.color.bottombartxt_pressed : R.color.bottombartxt_unpressed));
                break;
            case R.id.btn_mine:
                btn_mine.setTextColor(getResources().getColor(isChecked ? R.color.bottombartxt_pressed : R.color.bottombartxt_unpressed));
                break;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.testmenu1:
                LogUtils.d(item.getTitle() + " 菜单按钮被点击");
                LogUtils.i("隐式启动 Intent");
                startActivity(new Intent("START_FRAGMENT_ACTIVITY"));
                break;
            case R.id.testmenu2:
                LogUtils.d(item.getTitle() + " 菜单按钮被点击");
                LogUtils.i("本机DPI：" + getResources().getDisplayMetrics().densityDpi + "\nXDPI：" + getResources().getDisplayMetrics().xdpi + "\nYDPI：" + getResources().getDisplayMetrics().ydpi);
                showToastShort("本机DPI：" + getResources().getDisplayMetrics().densityDpi + "\nXDPI：" + getResources().getDisplayMetrics().xdpi + "\nYDPI：" + getResources().getDisplayMetrics().ydpi);
                break;
            case R.id.menu_3:
                try {
                    showToastShort("缓存文件大小->" + DataCleanManager.getFormatSize(this, getCacheDir()));
//                    LogUtils.i("缓存文件大小->" + DataCleanManager.getFileSize(getCacheDir())
//                            + "\n数据文件大小->" + DataCleanManager.getFileSize(getFilesDir())
//                            + "\nsharePreference大小->" + DataCleanManager.getFileSize(new File("/data/data/" + getPackageName() + "/shared_prefs"))
//                            + "\nSD卡中缓存文件大小->" + DataCleanManager.getFileSize(getExternalCacheDir())
//                            + "\nSD卡中数据文件大小->" + DataCleanManager.getFileSize(getExternalFilesDir(Environment.DIRECTORY_PICTURES))
//                            + "\n整个应用数据文件大小->" + DataCleanManager.getFileSize(new File("/data/data/" + getPackageName())));
                    LogUtils.i("缓存文件大小->" + DataCleanManager.getFormatSize(this, getCacheDir()) + "\n数据文件大小->" + DataCleanManager.getFormatSize(this, getFilesDir()) + "\nsharePreference大小->" + DataCleanManager.getFormatSize(this, new File("/data/data/" + getPackageName() + "/shared_prefs")) + "\nSD卡中缓存文件大小->" + DataCleanManager.getFormatSize(this, getExternalCacheDir()) + "\nSD卡中数据文件大小->" + DataCleanManager.getFormatSize(this, getExternalFilesDir(Environment.DIRECTORY_PICTURES)) + "\n整个应用数据文件大小->" + DataCleanManager.getFormatSize(this, new File("/data/data/" + getPackageName())));
                } catch (Exception e) {
                    LogUtils.e(e.toString());
                }
                break;
            case R.id.menu_4:
                showToastShort("清除缓存完毕");
                DataCleanManager.cleanApplicationData(MainActivity.this, "/data/data/" + getPackageName());
                break;
            case R.id.menu_5:
                try {
                    PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
                    LogUtils.i("应用名称：" + info.versionName + "\n应用版本：" + info.versionCode + "\n应用包名：" + info.packageName);
                    showToastShort("应用名称：" + info.versionName + "\n应用版本：" + info.versionCode + "\n应用包名：" + info.packageName);
                } catch (PackageManager.NameNotFoundException e) {
                    LogUtils.e(e.toString());
                }
                break;
        }
        return true;
    }

    @Override
    public void onLeftBtnClick() {
        LogUtils.d("左侧按钮被点击");
    }

    @Override
    public void onRightBtnClick() {
        LogUtils.d("右侧按钮被点击");
    }

    private class NetWorkAvailableReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtils.i("网络状态发生改变 ");
            if (PublicUtil.isNetWorkAvailable(MainActivity.this)) {
                LogUtils.d("当前设备已经联网");
            } else {
                showToastShort("亲的网络不给力啊╮(╯3╰)╭");
            }
        }
    }
}
