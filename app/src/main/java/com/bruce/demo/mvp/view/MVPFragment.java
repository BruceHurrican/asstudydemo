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

package com.bruce.demo.mvp.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bruce.demo.R;
import com.bruce.demo.base.BaseFragment;
import com.bruce.demo.mvp.presenter.CalculatePresenter;
import com.bruceutils.utils.LogUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by BruceHurrican on 16-3-23.
 */
public class MVPFragment extends BaseFragment {
    @Bind(R.id.et_number1)
    EditText etNumber1;
    @Bind(R.id.et_number2)
    EditText etNumber2;
    @Bind(R.id.btn_calculate)
    Button btnCalculate;
    @Bind(R.id.tv_add)
    TextView tvAdd;
    @Bind(R.id.tv_subtract)
    TextView tvSubtract;
    @Bind(R.id.tv_multiply)
    TextView tvMultiply;
    @Bind(R.id.tv_divide)
    TextView tvDivide;

    private CalculatePresenter calculatePresenter;

    @Override
    public String getTAG() {
        return MVPFragment.class.getSimpleName();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mvp_view_result, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        calculatePresenter = new CalculatePresenter(this); // 这种写法会造成内存泄漏
//        calculatePresenter = new CalculatePresenter(new MyIResult(this));
        calculatePresenter = new CalculatePresenter(new IResult() {
            @Override
            public void showAdd(int result) {
                LogUtils.d("add: " + result);
                showToastShort("add: " + result);
                tvAdd.setText("add: " + result);
            }

            @Override
            public void showSubtract(int result) {
                LogUtils.d("subtract: " + result);
                showToastShort("subtract: " + result);
                tvSubtract.setText("subtract: " + result);
            }

            @Override
            public void showMultiply(int result) {
                LogUtils.d("multiply: " + result);
                showToastShort("multiply: " + result);
                tvMultiply.setText("multiply: " + result);
            }

            @Override
            public void showDivide(double result) {
                LogUtils.d("divide: " + result);
                showToastShort("divide: " + result);
                tvDivide.setText("divide: " + result);
            }
        });
    }

    @OnClick({R.id.btn_calculate})
    void calculate() {
        if (TextUtils.isEmpty(etNumber1.getText().toString().trim()) || TextUtils.isEmpty(etNumber2.getText().toString().trim())) {
            showToastShort("please input numbers");
            return;
        }
        if (calculatePresenter != null) {
            calculatePresenter.initUser(Integer.valueOf(etNumber1.getText().toString().trim()), Integer.valueOf(etNumber2.getText().toString().trim()));
        }
    }

    @Override
    public void onDestroyView() {
        btnCalculate.setOnClickListener(null); // 用于解决内存泄漏
        calculatePresenter.clear();
        calculatePresenter = null;
        ButterKnife.unbind(this);
        super.onDestroyView();
    }
}
