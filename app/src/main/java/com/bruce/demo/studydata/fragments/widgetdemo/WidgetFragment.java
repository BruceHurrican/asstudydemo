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

package com.bruce.demo.studydata.fragments.widgetdemo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bruce.demo.MainActivity;
import com.bruce.demo.R;
import com.bruce.demo.base.BaseFragment;
import com.bruce.demo.widget.ScratchCardView;
import com.bruce.demo.widget.TitleBar2;
import com.bruceutils.utils.LogUtils;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 控件练习
 * Created by BruceHurrican on 2016/1/23.
 */
public class WidgetFragment extends BaseFragment {
    public static final int NOTIFICATION_ID = 1;
    @Bind(R.id.rl_root)
    RelativeLayout rl_root;
    @Bind(R.id.btn_send_notification)
    Button btn_send_notification;
    @Bind(R.id.btn_reset)
    Button btn_reset;
    @Bind(R.id.btn_animate)
    Button btn_animate;
    @Bind(R.id.btn_svg)
    Button btn_svg;
    @Bind(R.id.btn_svg2)
    Button btn_svg2;
    @Bind(R.id.btn_svg3)
    Button btn_svg3;
    @Bind(R.id.btn_animate2)
    Button btn_animate2;
    @Bind(R.id.title_bar)
    TitleBar2 title_bar;
    @Bind(R.id.scratch_card_view)
    ScratchCardView scratch_card_view;
    @Bind(R.id.iv_svg2)
    ImageView iv_svg2;
    boolean animate2Flag = true;
    private ImageView iv;

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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LogUtils.d("=====onViewCreated======");

        iv = new ImageView(getActivity());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.topMargin = 15;
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layoutParams.addRule(RelativeLayout.BELOW, R.id.btn_animate);
        iv.setLayoutParams(layoutParams);
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            iv.setImageResource(R.drawable.svg_vector_anim);
        } else {
            iv.setImageResource(R.mipmap.rotate3danim2);
            LogUtils.e("系统当前版本不支持 SVG 动画");
            showToastShort("系统当前版本不支持 SVG 动画");
        }
        rl_root.addView(iv);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogUtils.d("=====onActivityCreated======");

        // 要在 onDestroy() 方法中 解除事件绑定，否则会造成内存泄漏
        title_bar.setOnTitleBarClickListener(new TitleBar2.TitleBarClickListener() {
            @Override
            public void onLeftClick(View view) {
                LogUtils.d("left btn clicked");
                showToastShort("left btn clicked");
            }

            @Override
            public void onRightClick(View view) {
                LogUtils.d("right btn clicked");
                showToastShort("right btn clicked");
            }
        });

        // 要在 onDestroy() 方法中 解除事件绑定，否则会造成内存泄漏
        scratch_card_view.setOnResetScratchViewListener(new ScratchCardView.ResetScratchViewListener() {
            @Override
            public void onResetFinished(String info) {
                LogUtils.d(info);
                showToastShort(info);
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @OnClick({R.id.btn_send_notification, R.id.btn_reset, R.id.btn_animate, R.id.btn_svg, R.id.btn_svg2, R.id.btn_svg3, R.id.btn_animate2})
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
            case R.id.btn_reset:
                scratch_card_view.resetScratchView("再抽一次吧");
                break;
            case R.id.btn_animate:
                final AtomicBoolean atomicBoolean = new AtomicBoolean(true);
                scratch_card_view.animate().alpha(0.5f).y(250f).rotationBy(90f).scaleX(0.5f).setDuration(2000).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        LogUtils.i("=====属性动画开始1=======");
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            if (atomicBoolean.get()) {
                                // 控件显示阴影
                                scratch_card_view.animate().translationZ(150f);
                                atomicBoolean.set(false);
                            } else {
                                scratch_card_view.animate().translationZ(0f);
                                atomicBoolean.set(true);
                            }
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        LogUtils.i("=====属性动画结束1=======");
                    }
                }).withStartAction(new Runnable() {
                    @Override
                    public void run() {
                        showToastShort("属性动画开始");
                        LogUtils.i("=====属性动画开始2=======");
                    }
                }).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        showToastShort("属性动画结束");
                        LogUtils.i("=====属性动画结束2=======");
                        scratch_card_view.animate().cancel();
                    }
                });
                break;
            case R.id.btn_svg:
//                ((Animatable) iv_svg.getDrawable()).start();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ((Animatable) iv.getDrawable()).start();
                } else {
                    LogUtils.e("系统当前版本不支持 SVG 动画");
                    showToastShort("系统当前版本不支持 SVG 动画");
                }
                break;
            case R.id.btn_svg2:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    iv_svg2.setImageResource(R.drawable.svg_delete_anim);
                    ((Animatable) iv_svg2.getDrawable()).start();
                } else {
                    iv_svg2.setImageResource(R.mipmap.rotate3danim2);
                    LogUtils.e("系统当前版本不支持 SVG 动画");
                    showToastShort("系统当前版本不支持 SVG 动画");
                }
                break;
            case R.id.btn_svg3:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    iv_svg2.setImageResource(R.drawable.svg_search_anim);
                    ((Animatable) iv_svg2.getDrawable()).start();
                    btn_svg2.setTranslationZ(100f);
                } else {
                    iv_svg2.setImageResource(R.mipmap.rotate3danim2);
                    LogUtils.e("系统当前版本不支持 SVG 动画");
                    showToastShort("系统当前版本不支持 SVG 动画");
                }
                break;
            case R.id.btn_animate2:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (animate2Flag) {
                        Animator animator = ViewAnimationUtils.createCircularReveal(iv_svg2, iv_svg2.getWidth() / 2, iv_svg2.getHeight() / 2, iv_svg2.getWidth(), 0);
                        animator.setInterpolator(new AccelerateDecelerateInterpolator());
                        animator.setDuration(2000);
                        animator.start();
                        animate2Flag = false;
                    } else {
                        Animator animator = ViewAnimationUtils.createCircularReveal(iv_svg2, 0, 0, 0, (float) Math.hypot(iv_svg2.getWidth(), iv_svg2.getHeight()));
                        animator.setInterpolator(new AccelerateDecelerateInterpolator());
                        animator.setDuration(2000);
                        animator.start();
                        animate2Flag = true;
                    }
                } else {
                    iv_svg2.setImageResource(R.mipmap.rotate3danim2);
                    LogUtils.e("系统当前版本不支持 CircularReveal 动画");
                    showToastShort("系统当前版本不支持 CircularReveal 动画");
                }
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 解除事件绑定防止内存泄漏
        title_bar.setOnTitleBarClickListener(null);
        scratch_card_view.setOnResetScratchViewListener(null);
        ButterKnife.unbind(this);
    }
}
