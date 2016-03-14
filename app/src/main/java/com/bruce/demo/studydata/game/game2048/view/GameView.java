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

package com.bruce.demo.studydata.game.game2048.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.GridLayout;

import com.bruce.demo.DemoApplication;
import com.bruce.demo.studydata.game.game2048.activity.GameActivity;
import com.bruce.demo.studydata.game.game2048.bean.GameItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BruceHurrican on 2016/3/14.
 */
public class GameView extends GridLayout implements View.OnTouchListener {
    // GameView对应矩阵
    private GameItem[][] mGameMatrix;
    // 空格List
    private List<Point> mBlanks;
    // 矩阵行列数
    private int mGameLines;
    // 记录坐标
    private int mStartX, mStartY, mEndX, mEndY;
    // 辅助数组
    private List<Integer> mCalList;
    private int mKeyItemNum = -1;
    // 历史记录数组
    private int[][] mGameMatrixHistory;
    // 历史记录分数
    private int mScoreHistory;
    // 最高记录
    private int mHighScore;
    // 目标分数
    private int mTarget;

    public GameView(Context context) {
        super(context);
        mTarget = DemoApplication.mSp.getInt(DemoApplication.KEY_GAME_GOAL, 2048);
        initGameMatrix();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initGameMatrix();
    }

    public void startGame() {
        initGameMatrix();
        initGameView(DemoApplication.mItemSize);
    }

    private void initGameView(int cardSize) {
        removeAllViews();
        GameItem card;
        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                card = new GameItem(getContext(), 0);
                addView(card, cardSize, cardSize);
                // 初始化GameMatrix全部为0 空格List为所有
                mGameMatrix[i][j] = card;
                mBlanks.add(new Point(i, j));
            }
        }
        // 添加随机数字
        addRandomNum();
        addRandomNum();
    }

    /**
     * 撤销上次移动
     */
    public void revertGame() {
        // 第一次不能撤销
        int sum = 0;
        for (int[] element : mGameMatrixHistory) {
            for (int i : element) {
                sum += i;
            }
        }
        if (sum != 0) {
            GameActivity.getGameActivity().setScore(mScoreHistory, 0);
            DemoApplication.SCORE = mScoreHistory;
            for (int i = 0; i < mGameLines; i++) {
                for (int j = 0; j < mGameLines; j++) {
                    mGameMatrix[i][j].setNum(mGameMatrixHistory[i][j]);
                }
            }
        }
    }

    /**
     * 添加随机数字
     */
    private void addRandomNum() {
        getBlanks();
        if (mBlanks.size() > 0) {
            int randomNum = (int) (Math.random() * mBlanks.size());
            Point randomPoint = mBlanks.get(randomNum);
            mGameMatrix[randomPoint.x][randomPoint.y].setNum(Math.random() > 0.2d ? 2 : 4);
            animCreate(mGameMatrix[randomPoint.x][randomPoint.y]);
        }
    }

    /**
     * 生成动画
     *
     * @param target GameItem
     */
    private void animCreate(GameItem target) {
        ScaleAnimation sa = new ScaleAnimation(0.1f, 1, 0.1f, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        sa.setDuration(100);
        target.setAnimation(null);
        target.getItemView().startAnimation(sa);
    }

    /**
     * super模式下添加一个指定数字
     */
    private void addSuperNum(int num) {
        if (checkSuperNum(num)) {
            getBlanks();
            if (mBlanks.size() > 0) {
                int randomNum = (int) (Math.random() * mBlanks.size());
                Point randomPoint = mBlanks.get(randomNum);
                mGameMatrix[randomPoint.x][randomPoint.y].setNum(num);
                animCreate(mGameMatrix[randomPoint.x][randomPoint.y]);
            }
        }
    }

    /**
     * 检查添加的数是否是指定的数
     *
     * @param num num
     * @return 添加的数
     */
    private boolean checkSuperNum(int num) {
        return (num == 2 || num == 4 || num == 8 || num == 16 || num == 32 || num == 64 || num == 128 || num == 256 || num == 512 || num == 1024);
    }

    /**
     * 获取空格Item数组
     */
    private void getBlanks() {
        mBlanks.clear();
        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                if (mGameMatrix[i][j].getNum() == 0) {
                    mBlanks.add(new Point(i, j));
                }
            }
        }
    }

    /**
     * 初始化View
     */
    private void initGameMatrix() {
        // 初始化矩阵
        removeAllViews();
        mScoreHistory = 0;
        DemoApplication.SCORE = 0;
        DemoApplication.mGameLines = DemoApplication.mSp.getInt(DemoApplication.KEY_GAME_LINES, 4);
        mGameLines = DemoApplication.mGameLines;
        mGameMatrix = new GameItem[mGameLines][mGameLines];
        mGameMatrixHistory = new int[mGameLines][mGameLines];
        mCalList = new ArrayList<Integer>();
        mBlanks = new ArrayList<Point>();
        mHighScore = DemoApplication.mSp.getInt(DemoApplication.KEY_HIGH_SCORE, 0);
        setColumnCount(mGameLines);
        setRowCount(mGameLines);
        setOnTouchListener(this);
        // 初始化View参数
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        display.getMetrics(metrics);
        DemoApplication.mItemSize = metrics.widthPixels / DemoApplication.mGameLines;
        initGameView(DemoApplication.mItemSize);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                saveHistoryMatrix();
                mStartX = (int) event.getX();
                mStartY = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                mEndX = (int) event.getX();
                mEndY = (int) event.getY();
                judgeDirection(mEndX - mStartX, mEndY - mStartY);
                if (isMoved()) {
                    addRandomNum();
                    // 修改显示分数
                    GameActivity.getGameActivity().setScore(DemoApplication.SCORE, 0);
                }
                checkCompleted();
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 保存历史记录
     */
    private void saveHistoryMatrix() {
        mScoreHistory = DemoApplication.SCORE;
        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                mGameMatrixHistory[i][j] = mGameMatrix[i][j].getNum();
            }
        }
    }

    private int getDeviceDensity() {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(metrics);
        return (int) metrics.density;
    }

    /**
     * 根据偏移量判断移动方向
     *
     * @param offsetX offsetX
     * @param offsetY offsetY
     */
    private void judgeDirection(int offsetX, int offsetY) {
        int density = getDeviceDensity();
        int slideDis = 5 * density;
        int maxDis = 200 * density;
        boolean flagNormal = (Math.abs(offsetX) > slideDis || Math.abs(offsetY) > slideDis) &&
                (Math.abs(offsetX) < maxDis) &&
                (Math.abs(offsetY) < maxDis);
        boolean flagSuper = Math.abs(offsetX) > maxDis || Math.abs(offsetY) > maxDis;
        if (flagNormal && !flagSuper) {
            if (Math.abs(offsetX) > Math.abs(offsetY)) {
                if (offsetX > slideDis) {
                    swipeRight();
                } else {
                    swipeLeft();
                }
            } else {
                if (offsetY > slideDis) {
                    swipeDown();
                } else {
                    swipeUp();
                }
            }
        } else if (flagSuper) { // 启动超级用户权限来添加自定义数字
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            final EditText et = new EditText(getContext());
            builder.setTitle("Back Door").setView(et).setPositiveButton("OK", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    if (!TextUtils.isEmpty(et.getText())) {
                        addSuperNum(Integer.parseInt(et.getText().toString()));
                        checkCompleted();
                    }
                }
            }).setNegativeButton("ByeBye", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    arg0.dismiss();
                }
            }).create().show();
        }
    }

    /**
     * 检测所有数字 看是否有满足条件的
     *
     * @return 0:结束 1:正常 2:成功
     */
    private int checkNums() {
        getBlanks();
        if (mBlanks.size() == 0) {
            for (int i = 0; i < mGameLines; i++) {
                for (int j = 0; j < mGameLines; j++) {
                    if (j < mGameLines - 1) {
                        if (mGameMatrix[i][j].getNum() == mGameMatrix[i][j + 1].getNum()) {
                            return 1;
                        }
                    }
                    if (i < mGameLines - 1) {
                        if (mGameMatrix[i][j].getNum() == mGameMatrix[i + 1][j].getNum()) {
                            return 1;
                        }
                    }
                }
            }
            return 0;
        }
        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                if (mGameMatrix[i][j].getNum() == mTarget) {
                    return 2;
                }
            }
        }
        return 1;
    }

    /**
     * 判断是否结束
     * <p/>
     * 0:结束 1:正常 2:成功
     */
    private void checkCompleted() {
        int result = checkNums();
        if (result == 0) {
            if (DemoApplication.SCORE > mHighScore) {
                SharedPreferences.Editor editor = DemoApplication.mSp.edit();
                editor.putInt(DemoApplication.KEY_HIGH_SCORE, DemoApplication.SCORE);
                editor.apply();
                GameActivity.getGameActivity().setScore(DemoApplication.SCORE, 1);
                DemoApplication.SCORE = 0;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("GameActivity Over").setPositiveButton("Again", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    startGame();
                }
            }).create().show();
            DemoApplication.SCORE = 0;
        } else if (result == 2) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Mission Accomplished").setPositiveButton("Again", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    // 重新开始
                    startGame();
                }
            }).setNegativeButton("Continue", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    // 继续游戏 修改target
                    SharedPreferences.Editor editor = DemoApplication.mSp.edit();
                    if (mTarget == 1024) {
                        editor.putInt(DemoApplication.KEY_GAME_GOAL, 2048);
                        mTarget = 2048;
                        GameActivity.getGameActivity().setGoal(2048);
                    } else if (mTarget == 2048) {
                        editor.putInt(DemoApplication.KEY_GAME_GOAL, 4096);
                        mTarget = 4096;
                        GameActivity.getGameActivity().setGoal(4096);
                    } else {
                        editor.putInt(DemoApplication.KEY_GAME_GOAL, 4096);
                        mTarget = 4096;
                        GameActivity.getGameActivity().setGoal(4096);
                    }
                    editor.apply();
                }
            }).create().show();
            DemoApplication.SCORE = 0;
        }
    }

    /**
     * 判断是否移动过(是否需要新增Item)
     *
     * @return 是否移动
     */
    private boolean isMoved() {
        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                if (mGameMatrixHistory[i][j] != mGameMatrix[i][j].getNum()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 滑动事件：上
     */
    private void swipeUp() {
        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                int currentNum = mGameMatrix[j][i].getNum();
                if (currentNum != 0) {
                    if (mKeyItemNum == -1) {
                        mKeyItemNum = currentNum;
                    } else {
                        if (mKeyItemNum == currentNum) {
                            mCalList.add(mKeyItemNum * 2);
                            DemoApplication.SCORE += mKeyItemNum * 2;
                            mKeyItemNum = -1;
                        } else {
                            mCalList.add(mKeyItemNum);
                            mKeyItemNum = currentNum;
                        }
                    }
                } else {
                    continue;
                }
            }
            if (mKeyItemNum != -1) {
                mCalList.add(mKeyItemNum);
            }
            // 改变Item值
            for (int j = 0; j < mCalList.size(); j++) {
                mGameMatrix[j][i].setNum(mCalList.get(j));
            }
            for (int m = mCalList.size(); m < mGameLines; m++) {
                mGameMatrix[m][i].setNum(0);
            }
            // 重置行参数
            mKeyItemNum = -1;
            mCalList.clear();
        }
    }

    /**
     * 滑动事件：下
     */
    private void swipeDown() {
        for (int i = mGameLines - 1; i >= 0; i--) {
            for (int j = mGameLines - 1; j >= 0; j--) {
                int currentNum = mGameMatrix[j][i].getNum();
                if (currentNum != 0) {
                    if (mKeyItemNum == -1) {
                        mKeyItemNum = currentNum;
                    } else {
                        if (mKeyItemNum == currentNum) {
                            mCalList.add(mKeyItemNum * 2);
                            DemoApplication.SCORE += mKeyItemNum * 2;
                            mKeyItemNum = -1;
                        } else {
                            mCalList.add(mKeyItemNum);
                            mKeyItemNum = currentNum;
                        }
                    }
                } else {
                    continue;
                }
            }
            if (mKeyItemNum != -1) {
                mCalList.add(mKeyItemNum);
            }
            // 改变Item值
            for (int j = 0; j < mGameLines - mCalList.size(); j++) {
                mGameMatrix[j][i].setNum(0);
            }
            int index = mCalList.size() - 1;
            for (int m = mGameLines - mCalList.size(); m < mGameLines; m++) {
                mGameMatrix[m][i].setNum(mCalList.get(index));
                index--;
            }
            // 重置行参数
            mKeyItemNum = -1;
            mCalList.clear();
            index = 0;
        }
    }

    /**
     * 滑动事件：左
     */
    private void swipeLeft() {
        for (int i = 0; i < mGameLines; i++) {
            for (int j = 0; j < mGameLines; j++) {
                int currentNum = mGameMatrix[i][j].getNum();
                if (currentNum != 0) {
                    if (mKeyItemNum == -1) {
                        mKeyItemNum = currentNum;
                    } else {
                        if (mKeyItemNum == currentNum) {
                            mCalList.add(mKeyItemNum * 2);
                            DemoApplication.SCORE += mKeyItemNum * 2;
                            mKeyItemNum = -1;
                        } else {
                            mCalList.add(mKeyItemNum);
                            mKeyItemNum = currentNum;
                        }
                    }
                } else {
                    continue;
                }
            }
            if (mKeyItemNum != -1) {
                mCalList.add(mKeyItemNum);
            }
            // 改变Item值
            for (int j = 0; j < mCalList.size(); j++) {
                mGameMatrix[i][j].setNum(mCalList.get(j));
            }
            for (int m = mCalList.size(); m < mGameLines; m++) {
                mGameMatrix[i][m].setNum(0);
            }
            // 重置行参数
            mKeyItemNum = -1;
            mCalList.clear();
        }
    }

    /**
     * 滑动事件：右
     */
    private void swipeRight() {
        for (int i = mGameLines - 1; i >= 0; i--) {
            for (int j = mGameLines - 1; j >= 0; j--) {
                int currentNum = mGameMatrix[i][j].getNum();
                if (currentNum != 0) {
                    if (mKeyItemNum == -1) {
                        mKeyItemNum = currentNum;
                    } else {
                        if (mKeyItemNum == currentNum) {
                            mCalList.add(mKeyItemNum * 2);
                            DemoApplication.SCORE += mKeyItemNum * 2;
                            mKeyItemNum = -1;
                        } else {
                            mCalList.add(mKeyItemNum);
                            mKeyItemNum = currentNum;
                        }
                    }
                } else {
                    continue;
                }
            }
            if (mKeyItemNum != -1) {
                mCalList.add(mKeyItemNum);
            }
            // 改变Item值
            for (int j = 0; j < mGameLines - mCalList.size(); j++) {
                mGameMatrix[i][j].setNum(0);
            }
            int index = mCalList.size() - 1;
            for (int m = mGameLines - mCalList.size(); m < mGameLines; m++) {
                mGameMatrix[i][m].setNum(mCalList.get(index));
                index--;
            }
            // 重置行参数
            mKeyItemNum = -1;
            mCalList.clear();
            index = 0;
        }
    }
}
