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

package com.bruce.demo.studydata.game.game2048.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bruce.demo.DemoApplication;
import com.bruce.demo.R;
import com.bruce.demo.base.BaseActivity;
import com.bruce.demo.studydata.game.game2048.view.GameView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 2048 游戏
 * 参考自《Android群英传》
 * Created by BruceHurrican on 2016/3/14.
 */
public class GameActivity extends BaseActivity {
    // activity 的引用
    private static GameActivity mGameActivity;
    @Bind(R.id.tv_Goal)
    TextView mTvGoal;
    @Bind(R.id.score_title)
    TextView scoreTitle;
    @Bind(R.id.score)
    TextView mTvScore;
    @Bind(R.id.record_title)
    TextView recordTitle;
    @Bind(R.id.record)
    TextView mTvHighScore;
    @Bind(R.id.game_panel_rl)
    RelativeLayout relativeLayout;
    @Bind(R.id.game_panel)
    FrameLayout frameLayout;
    @Bind(R.id.btn_revert)
    Button mBtnRevert;
    @Bind(R.id.btn_restart)
    Button mBtnRestart;
    @Bind(R.id.btn_option)
    Button mBtnOptions;
    @Bind(R.id.container)
    LinearLayout container;
    private int mHighScore;
    private int mGoal;
    private GameView mGameView;

    public GameActivity() {
        mGameActivity = this;
    }

    /**
     * 获取当前 Activity 的引用
     *
     * @return
     */
    public static GameActivity getGameActivity() {
        return mGameActivity;
    }

    @Override
    public String getTAG() {
        return GameActivity.class.getSimpleName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_2048_activity_game);
        ButterKnife.bind(this);

        mHighScore = DemoApplication.mSp.getInt(DemoApplication.KEY_HIGH_SCORE, 0);
        mGoal = DemoApplication.mSp.getInt(DemoApplication.KEY_GAME_GOAL, 2048);
        mTvHighScore.setText(mHighScore + "");
        mTvGoal.setText(mGoal + "");
        mTvScore.setText("0");
        setScore(0, 0);
        mGameView = new GameView(this);
        relativeLayout.addView(mGameView);
    }

    public void setScore(int score, int flag) {
        switch (flag) {
            case 0:
                mTvScore.setText(score + "");
                break;
            case 1:
                mTvHighScore.setText(String.format("%d", score));
                break;
            default:
                break;
        }
    }

    public void setGoal(int num) {
        mTvGoal.setText(num);
    }

    @OnClick({R.id.btn_restart, R.id.btn_revert, R.id.btn_option})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_restart:
                mGameView.startGame();
                setScore(0, 0);
                break;
            case R.id.btn_revert:
                mGameView.revertGame();
                break;
            case R.id.btn_option:
                Intent intent = new Intent(GameActivity.this, ConfigPreferenceActivity.class);
                startActivityForResult(intent, 0);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            mGoal = DemoApplication.mSp.getInt(DemoApplication.KEY_GAME_GOAL, 2048);
            mTvGoal.setText("" + mGoal);
            getHightScore();
            mGameView.startGame();
        }
    }

    /**
     * 获取最高分数
     */
    private void getHightScore() {
        int score = DemoApplication.mSp.getInt(DemoApplication.KEY_HIGH_SCORE, 0);
        setScore(score, 1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
