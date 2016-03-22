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

package com.bruce.demo.studydata.game.game2048.bean;

import android.content.Context;
import android.graphics.Color;
import android.text.TextPaint;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.bruce.demo.studydata.game.game2048.activity.GameActivity;

/**
 * Created by BruceHurrican on 2016/3/14.
 */
public class GameItem extends FrameLayout {
    // Item 显示数字
    private int mCardShowNum;
    // 数字 title
    private TextView mTvNum;
    // 数字 title LayoutParams;
    private LayoutParams mParams;

    public GameItem(Context context, int cardShowNum) {
        super(context);
        this.mCardShowNum = cardShowNum;
        initCardItem();
    }

    private void initCardItem() {
        // 设置面板背景色,是由 Frame 拼起来的
        setBackgroundColor(Color.GRAY);
        mTvNum = new TextView(getContext());
        setNum(mCardShowNum);
        // 修改 5X5时字体太大
        int gameLines = GameActivity.mSp.getInt(GameActivity.KEY_GAME_LINES, 4);
        if (gameLines == 4) {
            mTvNum.setTextSize(35);
        } else if (gameLines == 5) {
            mTvNum.setTextSize(25);
        } else {
            mTvNum.setTextSize(20);
        }
        TextPaint tp = mTvNum.getPaint();
        tp.setFakeBoldText(true);
        mTvNum.setGravity(Gravity.CENTER);
        mParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mParams.setMargins(5, 5, 5, 5);
        addView(mTvNum, mParams);
    }

    public View getItemView() {
        return mTvNum;
    }

    public int getNum() {
        return mCardShowNum;
    }

    public void setNum(int num) {
        this.mCardShowNum = num;
        if (num == 0) {
            mTvNum.setText("");
        } else {
            mTvNum.setText(num + "");
        }

        // 设置背景色
        switch (num) {
            case 0:
                mTvNum.setBackgroundColor(0x00000000);
                break;
            case 2:
                mTvNum.setBackgroundColor(0xffeee5db);
                break;
            case 4:
                mTvNum.setBackgroundColor(0xffeee0ca);
                break;
            case 8:
                mTvNum.setBackgroundColor(0xfff2c17a);
                break;
            case 16:
                mTvNum.setBackgroundColor(0xfff59667);
                break;
            case 32:
                mTvNum.setBackgroundColor(0xfff68c6f);
                break;
            case 64:
                mTvNum.setBackgroundColor(0xfff66e3c);
                break;
            case 128:
                mTvNum.setBackgroundColor(0xffedcf74);
                break;
            case 256:
                mTvNum.setBackgroundColor(0xffedcc64);
                break;
            case 512:
                mTvNum.setBackgroundColor(0xffedc854);
                break;
            case 1024:
                mTvNum.setBackgroundColor(0xffedc54f);
                break;
            case 2048:
                mTvNum.setBackgroundColor(0xffedc32e);
                break;
            default:
                mTvNum.setBackgroundColor(0xff3c4a34);
                break;
        }
    }
}
