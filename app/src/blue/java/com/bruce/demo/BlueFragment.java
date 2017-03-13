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
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bruce.demo.demon.KDemonService;
import com.bruce.demo.demon.KService;
import com.bruce.demo.keeplive.OnepxReceiver;
import com.bruceutils.base.BaseFragment;
import com.bruceutils.utils.PublicUtil;
import com.bruceutils.utils.StorageUtil;
import com.bruceutils.utils.logdetails.LogDetails;

/**
 * Created by BruceHurrican on 17/1/9.
 */

public class BlueFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams
                .MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setBackgroundColor(getResources().getColor(android.R.color.black));

        TextView tv1 = new TextView(getActivity());
        tv1.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        tv1.setText("Blue module");
        tv1.setTextColor(getResources().getColor(android.R.color.holo_blue_dark));

        linearLayout.addView(tv1);

        Button btn1 = new Button(getActivity());
        btn1.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        btn1.setText("获取当前屏幕亮度和模式");
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showScreenModeandValue();
            }
        });

        linearLayout.addView(btn1);

        Button btn2 = new Button(getActivity());
        btn2.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        btn2.setText("开启新进程");
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().startService(new Intent(getActivity(), KService.class));
                getActivity().startService(new Intent(getActivity(), KDemonService.class));
            }
        });

        linearLayout.addView(btn2);

        Button btn3 = new Button(getActivity());
        btn3.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        btn3.setText("ping ip: 14.215.177.38"); // ping baidu ip
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PublicUtil.pingIpAddress("14.215.177.38");
                LogDetails.i("获取SDCARD剩余存储空间" + StorageUtil.getAvailableExternalMemorySize
                        (getActivity()));
            }
        });

        linearLayout.addView(btn3);

        Button btn4 = new Button(getActivity());
        btn4.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams
                .WRAP_CONTENT));
        btn4.setText("启动1像素保活");
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OnepxReceiver.register1pxReceiver(getActivity());
            }
        });

        linearLayout.addView(btn4);

        return linearLayout;
    }

    private void showScreenModeandValue() {
        // 获取屏幕亮度设置模式
        try {
            int screenMode = Settings.System.getInt(getActivity().getContentResolver(), Settings
                    .System.SCREEN_BRIGHTNESS_MODE);
            LogDetails.d("当前设备屏幕亮度模式为: " + screenMode);
            switch (screenMode) {
                case Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC:
                    LogDetails.d("当前设备屏幕亮度模式为自动调节屏幕亮度");
                    break;
                case Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL:
                    LogDetails.d("当前设备屏幕亮度模式为手动调节屏幕亮度");
                    break;
            }

            // 获取屏幕亮度值
            int screenValue = Settings.System.getInt(getActivity().getContentResolver(), Settings
                    .System.SCREEN_BRIGHTNESS, -1);
            LogDetails.d("当前设备亮度值为: " + screenValue);
            showToastShort(String.format("屏亮模式为 %s,值为 %d", screenMode == 1 ? "自动调节" : "手动调节",
                    screenValue));
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        OnepxReceiver.unregister1pxReceiver(getActivity());
    }
}
