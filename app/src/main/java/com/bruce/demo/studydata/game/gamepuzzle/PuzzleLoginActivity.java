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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bruce.demo.R;
import com.bruce.demo.base.BaseActivity;
import com.bruce.demo.studydata.game.gamepuzzle.adapter.GridItemsAdapter;
import com.bruce.demo.studydata.game.gamepuzzle.bean.ItemBean;
import com.bruce.demo.studydata.game.gamepuzzle.util.GameUtil;
import com.bruce.demo.studydata.game.gamepuzzle.util.ImagesUtil;
import com.bruce.demo.studydata.game.gamepuzzle.util.ScreenUtil;
import com.bruceutils.utils.LogUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * Created by BruceHurrican on 2016/4/9.
 */
public class PuzzleLoginActivity extends BaseActivity {
    // 拼图完成时显示的最后一个图片
    public static Bitmap mLastBitmap;
    // 设置为N*N显示
    public static int TYPE = 2;
    // 步数显示
    public static int COUNT_INDEX = 0;
    // 计时显示
    public static int TIMER_INDEX = 0;
    @Bind(R.id.tv_puzzle_main_counts)
    TextView tv_puzzle_main_counts;
    @Bind(R.id.tv_puzzle_main_time)
    TextView tv_puzzle_main_time;
    @Bind(R.id.ll_puzzle_main_spinner)
    LinearLayout ll_puzzle_main_spinner;
    @Bind(R.id.btn_puzzle_main_img)
    Button btn_puzzle_main_img;
    @Bind(R.id.btn_puzzle_main_restart)
    Button btn_puzzle_main_restart;
    @Bind(R.id.btn_puzzle_main_back)
    Button btn_puzzle_main_back;
    @Bind(R.id.ll_puzzle_main_btns)
    LinearLayout ll_puzzle_main_btns;
    @Bind(R.id.gv_puzzle_main_detail)
    GridView gv_puzzle_main_detail;
    @Bind(R.id.rl_puzzle_main_main_layout)
    RelativeLayout rl_puzzle_main_main_layout;
    // 选择的图片
    private Bitmap mPicSelected;
    private int mResId;
    private String mPicPath;
    private ImageView mImageView;
    // 显示步数
    private TextView mTvPuzzleMainCounts;
    // 计时器
    private TextView mTvTimer;
    // 切图后的图片
    private List<Bitmap> mBitmapItemLists = new ArrayList<Bitmap>();
    // GridView适配器
    private GridItemsAdapter mAdapter;
    // Flag 是否已显示原图
    private boolean mIsShowImg;
    // 计时器类
    private Timer mTimer;
    /**
     * 计时器线程
     */
    private TimerTask mTimerTask;

    @Override
    public String getTAG() {
        return PuzzleLoginActivity.class.getSimpleName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_puzzle_detail_main);
        ButterKnife.bind(this);
        initUIHandler();
        // 获取选择的图片
        Bitmap picSelectedTemp;
        // 选择默认图片还是自定义图片
        mResId = getIntent().getExtras().getInt(PuzzleActivity.PIC_SELECTED_ID);
        mPicPath = getIntent().getExtras().getString(PuzzleActivity.IMG_PIC_PATH);
        if (mResId != 0) {
            picSelectedTemp = BitmapFactory.decodeResource(getResources(), mResId);
            LogUtils.i("picSelectedTemp->" + picSelectedTemp);
        } else {
            picSelectedTemp = BitmapFactory.decodeFile(mPicPath);
            LogUtils.i("picSelectedTemp->" + picSelectedTemp);
        }
        LogUtils.i("get picSelectedTemp->" + picSelectedTemp);
        TYPE = getIntent().getExtras().getInt(PuzzleActivity.IMG_TYPE, 2);
        // 对图片处理
        handlerImage(picSelectedTemp);
        // 初始化Views
        initViews();
        // 生成游戏数据
        generateGame();
    }

    @OnItemClick(R.id.gv_puzzle_main_detail)
    public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
        // 判断是否可移动
        if (GameUtil.isMoveable(position)) {
            // 交换点击Item与空格的位置
            GameUtil.swapItems(GameUtil.mItemBeans.get(position), GameUtil.mBlankItemBean);
            // 重新获取图片
            recreateData();
            // 通知GridView更改UI
            mAdapter.notifyDataSetChanged();
            // 更新步数
            COUNT_INDEX++;
            mTvPuzzleMainCounts.setText("" + COUNT_INDEX);
            // 判断是否成功
            if (GameUtil.isSuccess()) {
                // 将最后一张图显示完整
                recreateData();
                mBitmapItemLists.remove(TYPE * TYPE - 1);
                mBitmapItemLists.add(mLastBitmap);
                // 通知GridView更改UI
                mAdapter.notifyDataSetChanged();
                showToastShort("拼图成功!");
                gv_puzzle_main_detail.setEnabled(false);
                mTimer.cancel();
                mTimerTask.cancel();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void handleUIMessage(Message msg) {
        switch (msg.what) {
            case 1:
                // 更新计时器
                TIMER_INDEX++;
                mTvTimer.setText("" + TIMER_INDEX);
                break;
            default:
                break;
        }
    }

    /**
     * Button点击事件
     */
    @OnClick({R.id.btn_puzzle_main_back, R.id.btn_puzzle_main_img, R.id.btn_puzzle_main_restart})
    public void onClick(View v) {
        switch (v.getId()) {
            // 返回按钮点击事件
            case R.id.btn_puzzle_main_back:
                PuzzleLoginActivity.this.finish();
                break;
            // 显示原图按钮点击事件
            case R.id.btn_puzzle_main_img:
                Animation animShow = AnimationUtils.loadAnimation(PuzzleLoginActivity.this, R.anim.game_puzzle_image_show_anim);
                Animation animHide = AnimationUtils.loadAnimation(PuzzleLoginActivity.this, R.anim.game_puzzle_image_hide_anim);
                if (mIsShowImg) {
                    mImageView.startAnimation(animHide);
                    mImageView.setVisibility(View.GONE);
                    mIsShowImg = false;
                } else {
                    mImageView.startAnimation(animShow);
                    mImageView.setVisibility(View.VISIBLE);
                    mIsShowImg = true;
                }
                break;
            // 重置按钮点击事件
            case R.id.btn_puzzle_main_restart:
                cleanConfig();
                generateGame();
                recreateData();
                // 通知GridView更改UI
                mTvPuzzleMainCounts.setText("" + COUNT_INDEX);
                mAdapter.notifyDataSetChanged();
                gv_puzzle_main_detail.setEnabled(true);
                break;
            default:
                break;
        }
    }

    /**
     * 生成游戏数据
     */
    private void generateGame() {
        // 切图 获取初始拼图数据 正常顺序
        new ImagesUtil().createInitBitmaps(TYPE, mPicSelected, PuzzleLoginActivity.this);
        // 生成随机数据
        GameUtil.getPuzzleGenerator();
        // 获取Bitmap集合
        for (ItemBean temp : GameUtil.mItemBeans) {
            mBitmapItemLists.add(temp.getBitmap());
        }
        // 数据适配器
        mAdapter = new GridItemsAdapter(this, mBitmapItemLists);
        gv_puzzle_main_detail.setAdapter(mAdapter);
        // 启用计时器
        mTimer = new Timer(true);
        // 计时器线程
        mTimerTask = new TimerTask() {

            @Override
            public void run() {
                Message msg = new Message();
                msg.what = 1;
                getUIHandler().sendMessage(msg);
            }
        };
        // 每1000ms执行 延迟0s
        mTimer.schedule(mTimerTask, 0, 1000);
    }

    /**
     * 添加显示原图的View
     */
    private void addImgView() {
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.rl_puzzle_main_main_layout);
        mImageView = new ImageView(PuzzleLoginActivity.this);
        mImageView.setImageBitmap(mPicSelected);
        int x = (int) (mPicSelected.getWidth() * 0.9F);
        int y = (int) (mPicSelected.getHeight() * 0.9F);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(x, y);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        mImageView.setLayoutParams(params);
        relativeLayout.addView(mImageView);
        mImageView.setVisibility(View.GONE);
    }

    /**
     * 返回时调用
     */
    @Override
    protected void onStop() {
        super.onStop();
        // 清空相关参数设置
        cleanConfig();
        this.finish();
    }

    /**
     * 清空相关参数设置
     */
    private void cleanConfig() {
        // 清空相关参数设置
        GameUtil.mItemBeans.clear();
        // 停止计时器
        mTimer.cancel();
        mTimerTask.cancel();
        COUNT_INDEX = 0;
        TIMER_INDEX = 0;
        // 清除拍摄的照片
        if (mPicPath != null) {
            // 删除照片
            File file = new File(PuzzleActivity.TEMP_IMAGE_PATH);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    /**
     * 重新获取图片
     */
    private void recreateData() {
        mBitmapItemLists.clear();
        for (ItemBean temp : GameUtil.mItemBeans) {
            mBitmapItemLists.add(temp.getBitmap());
        }
    }

    /**
     * 对图片处理 自适应大小
     *
     * @param bitmap bitmap
     */
    private void handlerImage(Bitmap bitmap) {
        // 将图片放大到固定尺寸
        int screenWidth = ScreenUtil.getScreenSize(this).widthPixels;
        int screenHeigt = ScreenUtil.getScreenSize(this).heightPixels;
        mPicSelected = new ImagesUtil().resizeBitmap(screenWidth * 0.8f, screenHeigt * 0.6f, bitmap);
    }

    /**
     * 初始化Views
     */
    private void initViews() {
        // Flag 是否已显示原图
        mIsShowImg = false;
        // 设置为N*N显示
        gv_puzzle_main_detail.setNumColumns(TYPE);
        RelativeLayout.LayoutParams gridParams = new RelativeLayout.LayoutParams(mPicSelected.getWidth(), mPicSelected.getHeight());
        // 水平居中
        gridParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        // 其他格式属性
        gridParams.addRule(RelativeLayout.BELOW, R.id.ll_puzzle_main_spinner);
        // Grid显示
        gv_puzzle_main_detail.setLayoutParams(gridParams);
        gv_puzzle_main_detail.setHorizontalSpacing(0);
        gv_puzzle_main_detail.setVerticalSpacing(0);
        // TV步数
        mTvPuzzleMainCounts = (TextView) findViewById(R.id.tv_puzzle_main_counts);
        mTvPuzzleMainCounts.setText("" + COUNT_INDEX);
        // TV计时器
        mTvTimer = (TextView) findViewById(R.id.tv_puzzle_main_time);
        mTvTimer.setText("0秒");
        // 添加显示原图的View
        addImgView();
    }
}
