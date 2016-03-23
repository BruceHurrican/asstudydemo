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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bruce.demo.R;
import com.bruce.demo.base.BaseActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by BruceHurrican on 2016/3/14.
 */
public class ConfigPreferenceActivity extends BaseActivity {
    @Bind(R.id.tv_title)
    TextView tvTitle;
    @Bind(R.id.view1)
    View view1;
    @Bind(R.id.btn_gamelines)
    Button mBtnGameLines;
    @Bind(R.id.tv_contact)
    TextView tvContact;
    @Bind(R.id.tv_targetgoal)
    TextView tvTargetgoal;
    @Bind(R.id.tv_gamelines)
    TextView tvGamelines;
    @Bind(R.id.btn_goal)
    Button mBtnGoal;
    @Bind(R.id.view2)
    View view2;
    @Bind(R.id.btn_back)
    Button mBtnBack;
    @Bind(R.id.btn_done)
    Button mBtnDone;
    private String[] mGameLinesList;

    private String[] mGameGoalList;

    private AlertDialog.Builder mBuilder;

    @Override
    public String getTAG() {
        return ConfigPreferenceActivity.class.getSimpleName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_2048_activity_config_preference);
        ButterKnife.bind(this);
        mBtnGameLines.setText("" + GameActivity.mSp.getInt(GameActivity.KEY_GAME_LINES, 4));
        mBtnGoal.setText("" + GameActivity.mSp.getInt(GameActivity.KEY_GAME_GOAL, 2048));
        mGameLinesList = new String[]{"4", "5", "6"};
        mGameGoalList = new String[]{"1024", "2048", "4096"};
    }

    private void saveConfig() {
        SharedPreferences.Editor editor = GameActivity.mSp.edit();
        editor.putInt(GameActivity.KEY_GAME_LINES, Integer.parseInt(mBtnGameLines.getText().toString()));
        editor.putInt(GameActivity.KEY_GAME_GOAL, Integer.parseInt(mBtnGoal.getText().toString()));
        editor.commit();
    }

    @OnClick({R.id.btn_gamelines, R.id.btn_goal, R.id.btn_back, R.id.btn_done})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_gamelines:
                mBuilder = new AlertDialog.Builder(this);
                mBuilder.setTitle("choose the lines of the game");
                mBuilder.setItems(mGameLinesList, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mBtnGameLines.setText(mGameLinesList[which]);
                    }
                });
                mBuilder.create().show();
                break;
            case R.id.btn_goal:
                mBuilder = new AlertDialog.Builder(this);
                mBuilder.setTitle("choose the goal of the game");
                mBuilder.setItems(mGameGoalList, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mBtnGoal.setText(mGameGoalList[which]);
                    }
                });
                mBuilder.create().show();
                break;
            case R.id.btn_back:
                this.finish();
                break;
            case R.id.btn_done:
                saveConfig();
                setResult(RESULT_OK);
                this.finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
