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

package com.study.bruce.demo.studydata.activities.media;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.study.bruce.demo.R;
import com.study.bruce.demo.base.BaseActivity;
import com.study.bruce.demo.utils.LogUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 调用系统照相机获取相片
 * Created by BruceHurrican on 2016/1/24.
 */
public class CameraActivity extends BaseActivity {
    public static final int TAKE_PHOTO = 1;
    public static final int CROP_PHOTO = 2;
    public static final int ALBUM_IMG = 3;
    @Bind(R.id.btn_camera)
    Button btn_amera;
    @Bind(R.id.btn_album)
    Button btn_album;
    @Bind(R.id.iv_show)
    ImageView iv_show;
    private Uri imageUri;
    private File tmpFile;

    @Override
    public String getTAG() {
        return "CameraActivity";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_activity);
        tmpFile = new File(Environment.getExternalStorageDirectory(), "tmpImage.jpg");
        ButterKnife.bind(this);
    }

    @OnClick({R.id.btn_camera, R.id.btn_album})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_camera:
                // 创建临时文件
                File outputImage = new File(Environment.getExternalStorageDirectory(), "tempImage.jpg");
                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e) {
                    LogUtils.e(e.toString());
                }
                imageUri = Uri.fromFile(outputImage);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, TAKE_PHOTO);
                break;
            case R.id.btn_album:
                Intent albumIntent = new Intent("android.intent.action.PICK");
                try {
                    if (tmpFile.exists()) {
                        tmpFile.delete();
                    }
                    tmpFile.createNewFile();
                } catch (IOException e) {
                    tmpFile = null;
                    LogUtils.e(e.toString());
                }
                if (tmpFile == null) {
                    return;
                }
                albumIntent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*");
                albumIntent.putExtra("output", Uri.fromFile(tmpFile));
                albumIntent.putExtra("crop", "true");
                albumIntent.putExtra("aspectX", 1);// 裁剪框比例
                albumIntent.putExtra("aspectY", 1);
                int crop = 180;
                albumIntent.putExtra("outputX", crop);// 输出图片大小
                albumIntent.putExtra("outputY", crop);
                startActivityForResult(albumIntent, ALBUM_IMG);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtils.i("requestCode:" + requestCode + "\nresultCode:" + resultCode + "\ndata:" + data);
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(imageUri, "image/*");
                    intent.putExtra("scale", true);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, CROP_PHOTO); // 启动裁剪程序
                }
                break;
            case CROP_PHOTO:
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                        iv_show.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        LogUtils.e(e.toString());
                    }
                }
                break;
            case ALBUM_IMG:
                if (resultCode == RESULT_OK) {
                    Bitmap bitmap;
                    if (null != tmpFile) {
                        bitmap = BitmapFactory.decodeFile(tmpFile.getAbsolutePath());
                        iv_show.setImageBitmap(bitmap);
                    }
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
