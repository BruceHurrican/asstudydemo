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

package com.bruceutils.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Build;
import android.util.ArrayMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 加载帧配置文件工具类
 * Created by BruceHurrican on 2015/11/30.
 */
public class JsonUtils {
    public static final String TAG = "ConfigureUtils->";

    /**
     * 从JSON 数组中获取 json 数据
     *
     * @param context
     * @param fileName json文件名
     * @return CfgObject 实例
     */
    public static JsonData getJsonData(Context context, String fileName) {
        JsonData jsonData = new JsonData();
        Map<String, String> data;
//        List<String> keysList = new ArrayList<String>(5);
        if (Build.VERSION.SDK_INT >= 19) {
            data = new ArrayMap<String, String>(5);
//            cfgObject.cfgMap2 = new ArrayMap<Integer, Map<String, String>>(5);
            jsonData.keyMap = new ArrayMap<Integer, List<String>>(5);
        } else {
            data = new HashMap<String, String>(5);
//            cfgObject.cfgMap2 = new HashMap<Integer, Map<String, String>>(5);
            jsonData.keyMap = new HashMap<Integer, List<String>>(5);
        }
        String line;
        StringBuilder sb = new StringBuilder();
        AssetManager assetManager = context.getAssets();
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new InputStreamReader(assetManager.open(fileName)));
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            try {
                JSONArray jsonArray = new JSONArray(sb.toString());
                JSONObject object;
                Iterator<String> iterator;
                for (int j = 0; j < jsonArray.length(); j++) {
                    List<String> keysList2 = new ArrayList<String>(5);
                    object = jsonArray.getJSONObject(j);
                    LogUtils.i(TAG + "jsonArray size->" + jsonArray.length() + "+object.length()+" + object.length());
                    iterator = object.keys();
                    while (iterator.hasNext()) {
                        keysList2.add(iterator.next());
                    }
                    for (int i = 0; i < object.length(); i++) {
                        data.put(keysList2.get(i), object.getString(keysList2.get(i)));
                    }
//                    cfgObject.cfgMap2.put(j,cfgMap);
                    jsonData.keyMap.put(j, keysList2);
                }
                jsonData.data = data;
                return jsonData;
            } catch (JSONException e) {
                LogUtils.e(TAG + e.toString());
                return null;
            }
        } catch (IOException e) {
            LogUtils.e(TAG + e.toString());
            return null;
        } finally {
            if (null != bufferedReader) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    LogUtils.e(TAG + e.toString());
                }
            }
        }
    }

    public static class JsonData {
        public Map<String, String> data; // json 数据 包括 key value eg: {"key01":"data01"}
        public Map<Integer, List<String>> keyMap; // json 数组中多个jsonobject 中的key,为保证数据唯一,jsonobject中的key不能相同,eg:[{"key01":"data01"},{"key02":"data02"}]

        public JsonData() {
        }
    }
}
