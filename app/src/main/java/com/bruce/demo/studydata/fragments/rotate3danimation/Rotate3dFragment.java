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

package com.bruce.demo.studydata.fragments.rotate3danimation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bruce.demo.base.BaseFragment;
import com.bruce.demo.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 利用 Camera 实现 3D 卡片翻转动画
 * 参考自 http://www.jianshu.com/p/153d9f31288d
 * Created by BruceHurrican on 2016/1/3.
 */
public class Rotate3dFragment extends BaseFragment {
    @Bind(R.id.btn_3d)
    Button btn_3d;
    @Bind(R.id.iv_3d)
    ImageView iv_3d;
    @Bind(R.id.rl_content)
    RelativeLayout rl_content;

    private int centerX, centerY;
    private int depthZ = 400;
    private int duration = 500;
    private Rotate3dAnim openAnimation, closeAnimation;
    private boolean isOpen = false;

    @Override
    public String getTAG() {
        return "Rotate3dFragment";
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.rotate3danim_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    private void initOpenAnim() {
        openAnimation = new Rotate3dAnim(0, 90, centerX, centerY, depthZ, true);
        openAnimation.setDuration(duration);
        openAnimation.setFillAfter(true);
        openAnimation.setInterpolator(new AccelerateInterpolator());
        openAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                iv_3d.setImageResource(R.mipmap.rotate3danim2);
                Rotate3dAnim rotate3dAnim = new Rotate3dAnim(270, 360, centerX, centerY, depthZ, false);
                rotate3dAnim.setDuration(duration);
                rotate3dAnim.setFillAfter(true);
                rotate3dAnim.setInterpolator(new DecelerateInterpolator());
                rl_content.startAnimation(rotate3dAnim);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void initCloseAnim() {
        closeAnimation = new Rotate3dAnim(360, 270, centerX, centerY, depthZ, true);
        closeAnimation.setDuration(duration);
        closeAnimation.setFillAfter(true);
        closeAnimation.setInterpolator(new AccelerateInterpolator());
        closeAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                iv_3d.setImageResource(R.mipmap.rotate3danim);
                Rotate3dAnim rotate3dAnim = new Rotate3dAnim(90, 0, centerX, centerY, depthZ, false);
                rotate3dAnim.setDuration(duration);
                rotate3dAnim.setFillAfter(true);
                rotate3dAnim.setInterpolator(new DecelerateInterpolator());
                rl_content.startAnimation(rotate3dAnim);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @OnClick(R.id.btn_3d)
    public void onClick() {
        centerX = rl_content.getWidth() / 2;
        centerY = rl_content.getHeight() / 2;
        if (null == openAnimation) {
            initOpenAnim();
            initCloseAnim();
        }
        if (openAnimation.hasStarted() && !openAnimation.hasEnded()) {
            return;
        }
        if (closeAnimation.hasStarted() && !closeAnimation.hasEnded()) {
            return;
        }
        rl_content.startAnimation(isOpen ? closeAnimation : openAnimation);
        isOpen = !isOpen;
        btn_3d.setText(isOpen ? "关闭" : "打开");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
