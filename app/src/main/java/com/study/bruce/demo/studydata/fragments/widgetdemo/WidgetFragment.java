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

package com.study.bruce.demo.studydata.fragments.widgetdemo;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.study.bruce.demo.MainActivity;
import com.study.bruce.demo.R;
import com.study.bruce.demo.base.BaseFragment;
import com.study.bruce.demo.utils.LogUtils;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 系统控件练习
 * Created by BruceHurrican on 2016/1/23.
 */
public class WidgetFragment extends BaseFragment {
    public static final int NOTIFICATION_ID = 1;
    @Bind(R.id.btn_send_notification)
    Button btn_send_notification;

    @Override
    public String getTAG() {
        return null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.widget_demo_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @OnClick({R.id.btn_send_notification})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_send_notification:
                NotificationManager manager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
                Notification.Builder builder = new Notification.Builder(getActivity());
                builder.setContentInfo("补充内容");
                builder.setContentText("主内容区");
                builder.setContentTitle("通知标题");
                builder.setSmallIcon(R.mipmap.icon_demo);
                builder.setTicker("新消息");
                builder.setAutoCancel(true);
                builder.setWhen(System.currentTimeMillis());
                Intent intent = new Intent(getActivity(), MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                builder.setContentIntent(pendingIntent);
                File file = new File("/system/media/audio/ringtones/Bollywood.ogg");
                LogUtils.i("音频文件是否存在" + file.exists());
                Uri soundUri = Uri.fromFile(file);
                builder.setSound(soundUri); // 设置通知到来时的声音
                builder.setLights(Color.GREEN, 1000, 1000);
                Notification notification = builder.build();
                notification.flags = Notification.FLAG_SHOW_LIGHTS;
                manager.notify(NOTIFICATION_ID, notification);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
