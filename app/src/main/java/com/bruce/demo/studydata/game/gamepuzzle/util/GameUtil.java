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

package com.bruce.demo.studydata.game.gamepuzzle.util;


import com.bruce.demo.studydata.game.gamepuzzle.PuzzleLoginActivity;
import com.bruce.demo.studydata.game.gamepuzzle.bean.ItemBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 拼图工具类：实现拼图的交换与生成算法
 *
 * @author xys
 */
public class GameUtil {

    // 游戏信息单元格Bean
    public static List<ItemBean> mItemBeans = new ArrayList<ItemBean>();
    // 空格单元格
    public static ItemBean mBlankItemBean = new ItemBean();

    /**
     * 判断点击的Item是否可移动
     *
     * @param position position
     * @return 能否移动
     */
    public static boolean isMoveable(int position) {
        int type = PuzzleLoginActivity.TYPE;
        // 获取空格Item
        int blankId = GameUtil.mBlankItemBean.getItemId() - 1;
        // 不同行 相差为type
        if (Math.abs(blankId - position) == type) {
            return true;
        }
        // 相同行 相差为1
        return (blankId / type == position / type) && Math.abs(blankId - position) == 1;
    }

    /**
     * 交换空格与点击Item的位置
     *
     * @param from  交换图
     * @param blank 空白图
     */
    public static void swapItems(ItemBean from, ItemBean blank) {
        ItemBean tempItemBean = new ItemBean();
        // 交换BitmapId
        tempItemBean.setBitmapId(from.getBitmapId());
        from.setBitmapId(blank.getBitmapId());
        blank.setBitmapId(tempItemBean.getBitmapId());
        // 交换Bitmap
        tempItemBean.setBitmap(from.getBitmap());
        from.setBitmap(blank.getBitmap());
        blank.setBitmap(tempItemBean.getBitmap());
        // 设置新的Blank
        GameUtil.mBlankItemBean = from;
    }

    /**
     * 生成随机的Item
     */
    public static void getPuzzleGenerator() {
        int index = 0;
        // 随机打乱顺序
        for (int i = 0; i < mItemBeans.size(); i++) {
            index = (int) (Math.random() *
                    PuzzleLoginActivity.TYPE * PuzzleLoginActivity.TYPE);
            swapItems(mItemBeans.get(index), GameUtil.mBlankItemBean);
        }
        List<Integer> data = new ArrayList<Integer>();
        for (int i = 0; i < mItemBeans.size(); i++) {
            data.add(mItemBeans.get(i).getBitmapId());
        }
        // 判断生成是否有解
        if (canSolve(data)) {
            return;
        } else {
            getPuzzleGenerator();
        }
    }

    /**
     * 是否拼图成功
     *
     * @return 是否拼图成功
     */
    public static boolean isSuccess() {
        for (ItemBean tempBean : GameUtil.mItemBeans) {
            if (tempBean.getBitmapId() != 0 && (tempBean.getItemId()) == tempBean.getBitmapId()) {
                continue;
            } else if (tempBean.getBitmapId() == 0 && tempBean.getItemId() == PuzzleLoginActivity.TYPE * PuzzleLoginActivity.TYPE) {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }

    /**
     * 该数据是否有解
     *
     * @param data 拼图数组数据
     * @return 该数据是否有解
     */
    public static boolean canSolve(List<Integer> data) {
        // 获取空格Id
        int blankId = GameUtil.mBlankItemBean.getItemId();
        // 可行性原则
        if (data.size() % 2 == 1) {
            return getInversions(data) % 2 == 0;
        } else {
            // 从底往上数,空格位于奇数行
            if (((blankId - 1) / PuzzleLoginActivity.TYPE) % 2 == 1) {
                return getInversions(data) % 2 == 0;
            } else {
                // 从底往上数,空位位于偶数行
                return getInversions(data) % 2 == 1;
            }
        }
    }

    /**
     * 计算倒置和算法
     *
     * @param data 拼图数组数据
     * @return 该序列的倒置和
     */
    public static int getInversions(List<Integer> data) {
        int inversions = 0;
        int inversionCount = 0;
        for (int i = 0; i < data.size(); i++) {
            for (int j = i + 1; j < data.size(); j++) {
                int index = data.get(i);
                if (data.get(j) != 0 && data.get(j) < index) {
                    inversionCount++;
                }
            }
            inversions += inversionCount;
            inversionCount = 0;
        }
        return inversions;
    }
}
