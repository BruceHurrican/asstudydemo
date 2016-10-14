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
 *   This document is Bruce's individual learning the android demo, wherein the use of the code from the Internet, only to use as a learning exchanges.
 *   And where any person can download and use, but not for commercial purposes.
 *   Author does not assume the resulting corresponding disputes.
 *   If you have good suggestions for the code, you can contact BurrceHurrican@foxmail.com
 *   本文件为Bruce's个人学习android的demo, 其中所用到的代码来源于互联网，仅作为学习交流使用。
 *   任和何人可以下载并使用, 但是不能用于商业用途。
 *   作者不承担由此带来的相应纠纷。
 *   如果对本代码有好的建议，可以联系BurrceHurrican@foxmail.com
 */

package com.bruce.demo.studydata.fragments.widgetdemo;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bruce.demo.R;
import com.bruce.demo.base.BaseFragment;
import com.bruce.demo.keeplive.KeepLiveBindService;
import com.bruce.demo.keeplive.KeepLiveService;
import com.bruceutils.utils.logdetails.LogDetails;

/**
 * 调用系统 API
 * Created by hrk on 16/6/2.
 */
public class SystemAPIFragment extends BaseFragment {
    private NotificationManager notificationManager;

    @Override
    public String getTAG() {
        return null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        ScrollView view = new ScrollView(getActivity());
        view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        LogDetails.getLogConfig().configTagPrefix("系统APIdemo").configShowBorders(true);
        LogDetails.d("初始化界面");
        final WifiManager wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        Button btn1 = new Button(getActivity());
        btn1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        btn1.setText("开启wifi");
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifiManager.setWifiEnabled(true);
                LogDetails.tag("开启 wifi").i("wifi 开启");
            }
        });
        linearLayout.addView(btn1);

        Button btn2 = new Button(getActivity());
        btn2.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        btn2.setText("关闭wifi");
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifiManager.setWifiEnabled(false);
                LogDetails.tag("关闭 wifi").i("wifi 关闭");
            }
        });
        linearLayout.addView(btn2);

        Button btn3 = new Button(getActivity());
        btn3.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        btn3.setText("开启常驻状态栏");
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNotification();
                LogDetails.tag("常驻状态栏").i("开启");
            }
        });
        linearLayout.addView(btn3);

        Button btn4 = new Button(getActivity());
        btn4.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        btn4.setText("关闭常驻状态栏");
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeNotiofication();
                LogDetails.tag("常驻状态栏").i("关闭");
            }
        });
        linearLayout.addView(btn4);

        final Intent intent = new Intent(getActivity(), KeepLiveBindService.class);
        final ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                // 待处理业务逻辑
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        Button btn5 = new Button(getActivity());
        btn5.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        btn5.setText("开启进程保活服务");
        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogDetails.tag("进程保活服务").i("开启");
                getActivity().startService(intent);
            }
        });
        linearLayout.addView(btn5);

        Button btn6 = new Button(getActivity());
        btn6.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        btn6.setText("绑定进程保活服务");
        btn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogDetails.tag("进程保活服务").i("绑定");
                getActivity().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
            }
        });
        linearLayout.addView(btn6);

        Button btn7 = new Button(getActivity());
        btn7.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        btn7.setText("关闭进程保活服务");
        btn7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogDetails.tag("进程保活服务").i("关闭");
                getActivity().stopService(intent);
                getActivity().stopService(new Intent(getActivity(), KeepLiveService.class));
            }
        });
        linearLayout.addView(btn7);

        TextView tv1 = new TextView(getActivity());
        tv1.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        String txt = "<font color='black'>1、有某蜡烛图喼帽电焊机</font><font color='red'>军火早班恒易风情万种</font><font color='black'>中，祝" +
                "有实力！2、有些人貂皮大衣</font><font color='red'>写数学只属于你</font><font color='black'>，铁炉堡四面楚歌。</font>";
        tv1.setText(Html.fromHtml(txt));
        linearLayout.addView(tv1);


        final EditText et = new EditText(getActivity());
        et.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        et.setHint("haha");
        et.setInputType(InputType.TYPE_CLASS_NUMBER);
        et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                LogDetails.i(s);
                if (count == 1 && !TextUtils.isEmpty(et.getText()) && et.getText().toString().trim().length() == 1) {
                    et.setText("-"+s);
                    et.setSelection(et.getText().toString().length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                LogDetails.d(s);
                LogDetails.d("et txt: " + et.getText().toString().trim());
            }
        });

        linearLayout.addView(et);

        view.addView(linearLayout);
        return view;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void showNotification() {
        notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        Notification.Builder builder = new Notification.Builder(getActivity());
        builder.setSmallIcon(R.mipmap.icon_demo);
        builder.setAutoCancel(false);
        builder.setContentText("aa1");
        builder.setContentInfo("aa2");
        builder.setContentTitle("aa3");
        builder.setSubText("aa4");
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.icon_workdemo));
        builder.setOngoing(true);
        notificationManager.notify(11, builder.build());
    }

    private void closeNotiofication() {
        notificationManager.cancel(11);
    }
}
