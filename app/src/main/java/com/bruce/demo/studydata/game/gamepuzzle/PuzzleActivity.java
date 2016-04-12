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

package com.bruce.demo.studydata.game.gamepuzzle;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bruce.demo.R;
import com.bruce.demo.base.BaseActivity;
import com.bruce.demo.studydata.game.gamepuzzle.adapter.GridPicListAdapter;
import com.bruce.demo.studydata.game.gamepuzzle.util.ScreenUtil;
import com.bruceutils.utils.LogUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * 拼图练习参考自安卓群英传
 * Created by BruceHurrican on 2016/4/9.
 */
public class PuzzleActivity extends BaseActivity {
    public static final String IMG_PIC_PATH = "mPicPath";
    public static final String IMG_TYPE = "mType";
    public static final String PIC_SELECTED_ID = "picSelectedID";
    // 返回码：系统图库
    private static final int RESULT_IMAGE = 100;
    // 返回码：相机
    private static final int RESULT_CAMERA = 200;
    // IMAGE TYPE
    private static final String IMAGE_TYPE = "image/*";
    // Temp照片路径
    public static String TEMP_IMAGE_PATH;
    // 显示Type
    @Bind(R.id.tv_puzzle_main_type_selected)
    TextView tv_puzzle_main_type_selected;
    @Bind(R.id.ll_puzzle_main_spinner)
    LinearLayout ll_puzzle_main_spinner;
    // GridView 显示图片
    @Bind(R.id.gv_xpuzzle_main_pic_list)
    GridView gv_xpuzzle_main_pic_list;
    private List<Bitmap> mPicList;
    // 主页图片资源ID
    private int[] mResPicId;
    private LayoutInflater mLayoutInflater;
    private PopupWindow mPopupWindow;
    private View mPopupView;
    private TextView mTvType2;
    private TextView mTvType3;
    private TextView mTvType4;
    // 游戏类型N*N
    private int mType = 2;
    // 本地图册、相机选择
    private String[] mCustomItems = new String[]{"本地图册", "相机拍照"};

    @Override
    public String getTAG() {
        return PuzzleActivity.class.getSimpleName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_puzzle_activity);
        ButterKnife.bind(this);
        TEMP_IMAGE_PATH = Environment.getExternalStorageDirectory().getPath() + "/temp.png";
        mPicList = new ArrayList<>();
        initViews();
        // 数据适配器
        gv_xpuzzle_main_pic_list.setAdapter(new GridPicListAdapter(PuzzleActivity.this, mPicList));
    }

    @OnClick(R.id.tv_puzzle_main_type_selected)
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_puzzle_main_type_selected:
                // 弹出popup window
                popupShow(view);
                break;

        }
    }

    @OnItemClick(R.id.gv_xpuzzle_main_pic_list)
    public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
        if (position == mResPicId.length - 1) {
            // 选择本地图库 相机
            showDialogCustom();
        } else {
            // 选择默认图片
            Intent intent = new Intent(PuzzleActivity.this, PuzzleLoginActivity.class);
            intent.putExtra(PIC_SELECTED_ID, mResPicId[position]);
            intent.putExtra(IMG_TYPE, mType);
            startActivity(intent);
        }
    }

    // 显示选择系统图库 相机对话框
    private void showDialogCustom() {
        AlertDialog.Builder builder = new AlertDialog.Builder(PuzzleActivity.this);
        builder.setTitle("选择：");
        builder.setItems(mCustomItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (0 == which) {
                    // 本地图册
                    Intent intent = new Intent(Intent.ACTION_PICK, null);
                    intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_TYPE);
                    startActivityForResult(intent, RESULT_IMAGE);
                } else if (1 == which) {
                    // 系统相机
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    Uri photoUri = Uri.fromFile(new File(TEMP_IMAGE_PATH));
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(intent, RESULT_CAMERA);
                }
            }
        });
        builder.create().show();
    }

    /**
     * 调用图库相机回调方法
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == RESULT_IMAGE && data != null) {
                // 相册
                Cursor cursor = this.getContentResolver().query(data.getData(), null, null, null, null);
                if (cursor != null) {
                    cursor.moveToFirst();
                } else {
                    LogUtils.i("未找到照片资源");
                    showToastShort("未找到照片资源");
                    return;
                }
                String imagePath = cursor.getString(cursor.getColumnIndex("_data"));
                Intent intent = new Intent(PuzzleActivity.this, PuzzleLoginActivity.class);
                intent.putExtra(IMG_PIC_PATH, imagePath);
                intent.putExtra(IMG_TYPE, mType);
                cursor.close();
                startActivity(intent);
            } else if (requestCode == RESULT_CAMERA) {
                // 相机
                Intent intent = new Intent(PuzzleActivity.this, PuzzleLoginActivity.class);
                intent.putExtra(IMG_PIC_PATH, TEMP_IMAGE_PATH);
                intent.putExtra(IMG_TYPE, mType);
                startActivity(intent);
            }
        }
    }

    /**
     * 初始化Views
     */
    private void initViews() {
        // 初始化Bitmap数据
        mResPicId = new int[]{R.mipmap.game_puzzle_pic1, R.mipmap.game_puzzle_pic2, R.mipmap.game_puzzle_pic3, R.mipmap.game_puzzle_pic4, R.mipmap.game_puzzle_pic5, R.mipmap.game_puzzle_pic6, R.mipmap.game_puzzle_pic7, R.mipmap.game_puzzle_pic8, R.mipmap.game_puzzle_pic9, R.mipmap.game_puzzle_pic10, R.mipmap.game_puzzle_pic11, R.mipmap.game_puzzle_pic12, R.mipmap.game_puzzle_pic13, R.mipmap.game_puzzle_pic14, R.mipmap.game_puzzle_pic15, R.mipmap.ic_launcher};
        Bitmap[] bitmaps = new Bitmap[mResPicId.length];
        for (int i = 0; i < bitmaps.length; i++) {
            bitmaps[i] = BitmapFactory.decodeResource(getResources(), mResPicId[i]);
            mPicList.add(bitmaps[i]);
        }
        // 显示type
        mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        // mType view
        mPopupView = mLayoutInflater.inflate(R.layout.game_puzzle_main_type_selected, null);
        mTvType2 = (TextView) mPopupView.findViewById(R.id.tv_main_type_2);
        mTvType3 = (TextView) mPopupView.findViewById(R.id.tv_main_type_3);
        mTvType4 = (TextView) mPopupView.findViewById(R.id.tv_main_type_4);
        mTvType2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mType = 2;
                tv_puzzle_main_type_selected.setText("2 X 2");
                mPopupWindow.dismiss();
            }
        });
        mTvType3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mType = 3;
                tv_puzzle_main_type_selected.setText("3 X 3");
                mPopupWindow.dismiss();
            }
        });
        mTvType4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mType = 4;
                tv_puzzle_main_type_selected.setText("4 X 4");
                mPopupWindow.dismiss();
            }
        });
    }

    /**
     * 显示popup window
     *
     * @param view popup window
     */
    private void popupShow(View view) {
        int density = (int) ScreenUtil.getDeviceDensity(this);
        // 显示popup window
        mPopupWindow = new PopupWindow(mPopupView, 200 * density, 50 * density);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        Drawable transpent = new ColorDrawable(Color.BLACK);
        mPopupWindow.setBackgroundDrawable(transpent);
        // 获取位置
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        mPopupWindow.showAtLocation(view, Gravity.NO_GRAVITY, location[0] - 40 * density, location[1] + 30 * density);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
