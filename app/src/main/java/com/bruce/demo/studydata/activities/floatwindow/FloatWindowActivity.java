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

package com.bruce.demo.studydata.activities.floatwindow;

import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.bruce.demo.base.BaseActivity;


/**
 * 悬浮窗
 * Created by BruceHurrican on 2016/5/24.
 */
public class FloatWindowActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        Button btn1 = new Button(this);
        btn1.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        btn1.setText("启动悬浮窗");
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                    Toast.makeText(FloatWindowActivity.this, "aa", Toast.LENGTH_SHORT).show();
//                    Log.i("aa2", "->" + Settings.canDrawOverlays(FloatWindowActivity.this));
//                    if (!Settings.canDrawOverlays(FloatWindowActivity.this)) {
//                        requestAlertWindowPermission();
//                    } else {
//                        startService(new Intent(FloatWindowActivity.this, FWService.class));
//                    }
//                } else {
//                    startService(new Intent(FloatWindowActivity.this, FWService.class));
//                }
                FWmanager.createSmallWindow(FloatWindowActivity.this);
            }
        });

        linearLayout.addView(btn1);
        setContentView(linearLayout);
    }

    @Override
    public String getTAG() {
        return null;
    }

    private static final int REQUEST_CODE = 1;

    @TargetApi(Build.VERSION_CODES.M)
    private void requestAlertWindowPermission() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, REQUEST_CODE);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (Settings.canDrawOverlays(this)) {
                Log.i("KK2", "onActivityResult granted");
                startService(new Intent(FloatWindowActivity.this, FWService.class));
            }
        }
    }
}
