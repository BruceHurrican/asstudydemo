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

package com.bruce.demo.studydata.fragments.crash;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.format.Formatter;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bruce.demo.base.BaseFragment;
import com.bruce.demo.utils.PublicUtil;
import com.bruce.demo.utils.LogUtils;

import java.io.File;

/**
 * 打印日志，保存日志到文件，保存应用崩溃信息到本地文件,删除应用本地文件、缓存信息
 * Created by BruceHurrican on 2015/12/10.
 */
public class CrashFragment extends BaseFragment {
    @Override
    public String getTAG() {
        return "CrashFragment->";
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout linearLayout = new LinearLayout(getActivity());
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        final TextView textView = new TextView(getActivity());
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setText("文本内容");
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);//设置字体大小为30sp
        Button btn1 = new Button(getActivity());
        btn1.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        btn1.setText("打印日志");
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.d("打印日志成功");
            }
        });
        Button btn2 = new Button(getActivity());
        btn2.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        btn2.setText("打印日志到本地文件");
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.log2file(LogUtils.PATH_LOG_INFO, getTAG(), "保存日志到SD卡中，文件全路径名称为：" + LogUtils.PATH_LOG_INFO);
            }
        });
        Button btn3 = new Button(getActivity());
        final String aa = null;
        btn3.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        btn3.setText("保存异常日志到本地文件");
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aa.length();
            }
        });
        Button btn4 = new Button(getActivity());
        btn4.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        btn4.setText("日志所在文件夹及目录下文件总大小");
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.i("日志所在文件夹及目录下文件总大小" + Formatter.formatFileSize(getActivity(), PublicUtil.getFolderSize(new File(LogUtils.FILE_PATH_ROOT))));
                textView.setText("日志所在文件夹及目录下文件总大小" + Formatter.formatFileSize(getActivity(), PublicUtil.getFolderSize(new File(LogUtils.FILE_PATH_ROOT))));
            }
        });
        Button btn5 = new Button(getActivity());
        btn5.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        btn5.setText("删除日志所在文件夹及目录下文件");
        btn5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.d("删除日志文件及所在目录");
                PublicUtil.recursionDelFile(new File(LogUtils.FILE_PATH_ROOT));
                textView.setText("删除成功");
            }
        });
        Button btn6 = new Button(getActivity());
        btn6.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        btn6.setText("删除缓存所在文件夹及目录下文件");
        btn6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.d("删除缓存文件及所在目录");
                PublicUtil.recursionDelFile(getActivity().getCacheDir());
                textView.setText("删除成功");
            }
        });
        Button btn7 = new Button(getActivity());
        btn7.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        btn7.setText("删除webview缓存"); // data/data/程序包名
        btn7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.d("删除程序webview缓存");
                PublicUtil.recursionDelFile(new File(getActivity().getPackageName() + "/app_webview"));
                textView.setText("删除成功");
            }
        });
        linearLayout.addView(btn1);
        linearLayout.addView(btn2);
        linearLayout.addView(btn3);
        linearLayout.addView(btn4);
        linearLayout.addView(btn5);
        linearLayout.addView(btn6);
        linearLayout.addView(btn7);
        linearLayout.addView(textView);
        return linearLayout;
    }
}
