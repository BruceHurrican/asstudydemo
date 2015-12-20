/*
 * Copyright (c) 2015.
 *   This document is Bruce's individual learning the android demo, wherein the use of the code from the Internet, only to use as a learning exchanges.
 *   And where any person can download and use, but not for commercial purposes.
 *   Author does not assume the resulting corresponding disputes.
 *   If you have good suggestions for the code, you can contact BurrceHurrican@foxmail.com
 *   本文件为Bruce's个人学习android的demo, 其中所用到的代码来源于互联网，仅作为学习交流使用。
 *   任和何人可以下载并使用, 但是不能用于商业用途。
 *   作者不承担由此带来的相应纠纷。
 *   如果对本代码有好的建议，可以联系BurrceHurrican@foxmail.com
 */

package com.study.bruce.demo.studydata.fragments.pulltorefreshmore.widget;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

import com.study.bruce.demo.R;

/**
 * Created by BruceHurrican on 2015/12/20.
 */
public class WindmillDrawable extends Drawable implements Animatable {
    private static final String TAG = WindmillDrawable.class.getSimpleName();
    private Resources resources;
    private Bitmap windmill;
    private Matrix matrix;
    private View parent;
    private Animation animation;
    private boolean isFirstDraw = true;
    private boolean isAnimating;

    public WindmillDrawable(Context context, View parent) {
        resources = context.getResources();
        windmill = BitmapFactory.decodeResource(resources, R.mipmap.ptfm_windmill);
        matrix = new Matrix();
        this.parent = parent;
        animation = new RotateAnimation(360, 0, Animation.RELATIVE_TO_SELF, 0.5F, Animation.RELATIVE_TO_SELF, 0.5F);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(800);
        animation.setRepeatMode(Animation.RESTART);
        animation.setRepeatCount(Animation.INFINITE);
        animation.setFillAfter(true);
    }

    @Override
    public void draw(Canvas canvas) {
        if (isFirstDraw) {
            isFirstDraw = false;
            matrix.setTranslate((getBounds().width() - windmill.getWidth()) / 2, (getBounds().height() - windmill.getHeight()) / 2);
        }
        Paint p = new Paint();
        canvas.drawBitmap(windmill, matrix, p);
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return 0;
    }

    public void postRotation(int degree) {
        matrix.postRotate(degree, getBounds().exactCenterX(), getBounds().exactCenterY());
        invalidateSelf();
    }

    @Override
    public void start() {
        parent.startAnimation(animation);
        isAnimating = true;
    }

    @Override
    public void stop() {
        parent.clearAnimation();
        isAnimating = false;
    }

    @Override
    public boolean isRunning() {
        return isAnimating;
    }
}
