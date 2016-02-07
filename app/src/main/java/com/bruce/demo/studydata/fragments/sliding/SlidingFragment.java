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

package com.bruce.demo.studydata.fragments.sliding;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bruce.demo.R;
import com.bruce.demo.base.BaseFragment;

/**
 * 滑动效果
 * Created by BruceHurrican on 2016/2/7.
 */
public class SlidingFragment extends BaseFragment {
    @Override
    public String getTAG() {
        return "SlidingFragment";
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        LinearLayout view = new LinearLayout(getActivity());
//        view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
//        QQSlidingView qqSlidingView = new QQSlidingView(getActivity());
//        qqSlidingView.setLayoutParams(new LinearLayout.LayoutParams(700, LinearLayout.LayoutParams.MATCH_PARENT));
//        TextView tv = new TextView(getActivity());
//        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) qqSlidingView.getLayoutParams();
//        params.topMargin = 120;
//        params.leftMargin = 210;
//        params.rightMargin = 130;
//        params.bottomMargin = 250;
//        tv.setLayoutParams(params);
//        tv.setText("hello world");
//        qqSlidingView.addView(tv);
//        view.addView(qqSlidingView);
        return inflater.inflate(R.layout.sliding_view, container, false);
    }
}
