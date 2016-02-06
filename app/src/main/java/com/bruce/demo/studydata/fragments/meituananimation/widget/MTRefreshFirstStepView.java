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

package com.bruce.demo.studydata.fragments.meituananimation.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.bruce.demo.R;

/**
 * Created by BruceHurrican on 2015/12/27.
 */
public class MTRefreshFirstStepView extends View {
    private Bitmap initialBitmap;
    private Bitmap scaledBitmap;
    private Bitmap endBitmap;
    private int measuredWidth, measuredHeight;
    private float mCurrentProgress;

    public MTRefreshFirstStepView(Context context) {
        this(context, null);
    }

    public MTRefreshFirstStepView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MTRefreshFirstStepView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // 初始椭圆形图片
        initialBitmap = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.mt_pull_image));
        /*
            这个是第二个状态娃娃图片，之所以要这张图片，是因为第二个状态和第三个状态的图片的大小是一致的，
            而第一阶段椭圆形图片的大小与第二阶段和第三阶段不一致，因此我们需要根据这张图片来决定第一张图片的宽高，来保证第一阶段和第二、三
            阶段的 View 的宽高一致
         */
        endBitmap = Bitmap.createBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.mt_pull_end_image_frame_05));
    }

    /**
     * 重写目的在于设置 wrap_content 时，View 的大小
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureWidth(widthMeasureSpec) * endBitmap.getHeight() / endBitmap.getWidth());
    }


    /**
     * 当 wrap_content 的时候，宽度即为第二阶段娃娃图片的宽度
     *
     * @param widMeasureSpec
     * @return
     */
    private int measureWidth(int widMeasureSpec) {
        int result = 0;
        int size = MeasureSpec.getSize(widMeasureSpec);
        int mode = MeasureSpec.getMode(widMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            result = size;
        } else {
            result = endBitmap.getWidth();
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }
        return result;
    }

    /**
     * 在 onLayout 获得测试后 View 的宽高
     *
     * @param changed
     * @param left
     * @param top
     * @param right
     * @param bottom
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        measuredWidth = getMeasuredWidth();
        measuredHeight = getMeasuredHeight();
        // 根据第二阶段娃娃宽高， 给椭圆形图片进行等比例的缩放
        scaledBitmap = Bitmap.createScaledBitmap(initialBitmap, measuredWidth, measuredWidth * initialBitmap.getHeight() / initialBitmap.getWidth(), true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 这个方法是对画布进行缩放，从而达到椭圆形图片的缩放，第一个参数为宽度缩放比例，第二参数为高度缩放比例
        canvas.scale(mCurrentProgress, mCurrentProgress, measuredWidth / 2, measuredHeight / 2);
        // 将等比例缩放后的椭圆形画在画布上面
        canvas.drawBitmap(scaledBitmap, 0, measuredHeight / 4, null);
    }

    /**
     * 设置缩放比例，从0到1，0为最小
     *
     * @param currentProgress
     */
    public void setCurrentProgress(float currentProgress) {
        this.mCurrentProgress = currentProgress;
    }
}
