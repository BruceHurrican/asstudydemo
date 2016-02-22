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

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import com.bruce.demo.utils.LogUtils;

/**
 * An animation that rotates the view on the Y axis between two specified angles.
 * This animation also adds a translation on Z axis (depth) to improve the effect.
 * Created by BruceHurrican on 2016/1/3.
 */
public class Rotate3dAnim extends Animation {
    private float fromDegrees;
    private float toDegrees;
    private float centerX;
    private float centerY;
    private float depthZ;
    private boolean reverse;
    private Camera camera;

    /**
     * Creates a new 3D rotation on the Y axis. The rotation is defined by its
     * start angle and its end angle. Both angles are in degrees. The rotation
     * is performed around a center point on the 2D space, defined by a pair of
     * X and Y coordinates, called centerX and centerY. When the animation starts,
     * a translation on the Z axis (depth) is performed. The length of the translation can
     * be specified, as well as whether the translation should be reversed in time.
     *
     * @param fromDegrees the start angle of the 3D rotation
     * @param toDegrees   the end angle of the 3D rotation
     * @param centerX     the X center of the 3D rotation
     * @param centerY     the Y center of the 3D rotation
     * @param depthZ
     * @param reverse     true -- translation should be reversed
     */
    public Rotate3dAnim(float fromDegrees, float toDegrees, float centerX, float centerY, float depthZ, boolean reverse) {
        this.fromDegrees = fromDegrees;
        this.toDegrees = toDegrees;
        this.centerX = centerX;
        this.centerY = centerY;
        this.depthZ = depthZ;
        this.reverse = reverse;
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
        camera = new Camera();
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        float tmpFromDegrees = fromDegrees;
        float degrees = tmpFromDegrees + ((toDegrees - tmpFromDegrees) * interpolatedTime);
        float tmpCenterX = centerX;
        float tmpCenterY = centerY;
        Camera tmpCamera = camera;
        Matrix matrix = t.getMatrix();
        LogUtils.d("interpolatedTime: " + interpolatedTime);
        tmpCamera.save();
        if (reverse) {
            tmpCamera.translate(0f, 0f, depthZ * interpolatedTime);
        } else {
            tmpCamera.translate(0f, 0f, depthZ * (1.0f - interpolatedTime));
        }
        tmpCamera.rotateY(degrees);
        tmpCamera.getMatrix(matrix);
        camera.restore();
        matrix.preTranslate(-tmpCenterX, -tmpCenterY);
        matrix.postTranslate(tmpCenterX, tmpCenterY);
        super.applyTransformation(interpolatedTime, t);
    }
}
