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

package com.study.bruce.demo.studydata.fragments.io;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.study.bruce.demo.R;
import com.study.bruce.demo.base.BaseFragment;
import com.study.bruce.demo.utils.LogUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 数据持久化储存
 * Created by BruceHurrican on 2016/1/17.
 */
public class IOFragment extends BaseFragment {
    public static final String DEMO = "demo";
    @Bind(R.id.et_save)
    EditText et_save;
    @Bind(R.id.tv_show)
    TextView tv_show;
    @Bind(R.id.btn_save)
    Button btn_save;
    @Bind(R.id.btn_read)
    Button btn_read;
    private String saveInfo;

    @Override
    public String getTAG() {
        return "IOFragment";
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.io_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick({R.id.btn_save, R.id.btn_read})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_save:
                saveInfo = et_save.getText().toString().trim();
                if (TextUtils.isEmpty(saveInfo)) {
                    showToastShort("不能保存空信息到本地文件");
                    return;
                } else {
                    saveInfo2File(getActivity(), DEMO, saveInfo,Context.MODE_PRIVATE);
                }
                break;
            case R.id.btn_read:
                String content = readInfoFromFile(getActivity(), DEMO);
                tv_show.setText(content);
                break;
        }
    }

    public void saveInfo2File(Context context, String fileName, String content,int saveMode) {
        FileOutputStream fileOutputStream;
        BufferedWriter bufferedWriter = null;
        try {
            fileOutputStream = context.openFileOutput(fileName, saveMode);
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));
            bufferedWriter.write(content);
        } catch (IOException e) {
            LogUtils.e(e.toString());
        } finally {
            if (null != bufferedWriter) {
                try {
                    bufferedWriter.close();
                    LogUtils.i("保存信息成功");
                } catch (IOException e) {
                    LogUtils.e(e.toString());
                }
            }
        }
    }

    public String readInfoFromFile(Context context, String fileName) {
        FileInputStream fileInputStream;
        BufferedReader bufferedReader = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            fileInputStream = context.openFileInput(fileName);
            bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            String line;
            while (null != (line = bufferedReader.readLine())) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            LogUtils.e(e.toString());
            return "读取信息失败";
        } finally {
            if (null != bufferedReader) {
                try {
                    bufferedReader.close();
                    LogUtils.i("读取信息成功");
                } catch (IOException e) {
                    LogUtils.e(e.toString());
                    return "读取信息失败";
                }
            }
        }
        return stringBuilder.toString();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
