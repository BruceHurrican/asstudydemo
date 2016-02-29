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

package com.bruce.demo.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.bruce.demo.R;

/**
 * 自定义刮刮卡
 * Created by BruceHurrican on 2016/2/11.
 */
public class ScratchCardView extends View {
    /**
     * 背景图
     */
    private Bitmap backgroundBitmap;
    /**
     * 前景图
     */
    private Bitmap foregroundBitmap;
    private Paint paint;
    private Canvas canvas;
    private Path path;
    private ResetScratchViewListener resetScratchViewListener;

    public ScratchCardView(Context context) {
        this(context, null);
    }

    public ScratchCardView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScratchCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAlpha(0);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(100);
        paint.setStrokeCap(Paint.Cap.ROUND);

        path = new Path();

        backgroundBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.view_scratch_card);
        foregroundBitmap = Bitmap.createBitmap(backgroundBitmap.getWidth(), backgroundBitmap.getHeight(), Bitmap.Config.ARGB_8888);

        canvas = new Canvas(foregroundBitmap);
        canvas.drawColor(Color.GRAY);

        setLayerType(View.LAYER_TYPE_SOFTWARE, null); // 关闭硬件加速
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                path.reset();
                path.moveTo(event.getX(), event.getY());
                canvas.drawPath(path, paint);
                break;
            case MotionEvent.ACTION_UP:
                paint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(event.getX(), event.getY(), 50, paint);
                paint.setStyle(Paint.Style.STROKE);
                break;
            case MotionEvent.ACTION_MOVE:
                path.lineTo(event.getX(), event.getY());
                canvas.drawPath(path, paint);
                break;
        }
        invalidate();
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(backgroundBitmap, 0, 0, null);
        canvas.drawBitmap(foregroundBitmap, 0, 0, null);
    }

    public void resetScratchView(String txt) {
        init();
        resetScratchViewListener.onResetFinished(txt);
    }

    public void setOnResetScratchViewListener(ResetScratchViewListener listener) {
        resetScratchViewListener = listener;
    }

    public interface ResetScratchViewListener {
        void onResetFinished(String info);
    }
}
