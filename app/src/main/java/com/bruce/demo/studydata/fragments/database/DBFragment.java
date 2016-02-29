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

package com.bruce.demo.studydata.fragments.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.bruce.demo.R;
import com.bruce.demo.base.BaseFragment;
import com.bruce.demo.utils.LogUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 数据库练习
 * Created by BruceHurrican on 2016/1/18.
 */
public class DBFragment extends BaseFragment {
    @Bind(R.id.btn_createDB)
    Button btn_createDB;
    @Bind(R.id.btn_add)
    Button btn_add;
    @Bind(R.id.btn_query)
    Button btn_query;
    @Bind(R.id.btn_update)
    Button btn_update;
    @Bind(R.id.btn_delete)
    Button btn_delete;
    private DBHelper dbHelper;
    private SQLiteDatabase sqLiteDatabase;

    @Override
    public String getTAG() {
        return "DBFragment";
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.db_fragment, container, false);
        ButterKnife.bind(this, view);
        dbHelper = new DBHelper(getActivity(), "demo.db", null, 4);
        return view;
    }

    @OnClick({R.id.btn_createDB, R.id.btn_add, R.id.btn_query, R.id.btn_update, R.id.btn_delete})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_createDB:
                sqLiteDatabase = dbHelper.getWritableDatabase();
                break;
            case R.id.btn_add:
                if (sqLiteDatabase != null) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("name", "DemoBook");
                    contentValues.put("author", "bruce");
                    contentValues.put("pages", 121);
                    contentValues.put("price", 22.36);
                    sqLiteDatabase.insert("Book", null, contentValues); // 插入第一条数据

                    contentValues.clear();

                    contentValues.put("name", "Booking");
                    contentValues.put("author", "Max");
                    contentValues.put("pages", 321);
                    contentValues.put("price", 432.21);
                    sqLiteDatabase.insert("Book", null, contentValues); // 插入第二条数据

                }
                break;
            case R.id.btn_query:
                if (sqLiteDatabase != null) {
                    // 查询 Book 表中所有数据
                    Cursor cursor = sqLiteDatabase.query("Book", null, null, null, null, null, null);
                    if (cursor.moveToFirst()) {
                        do {
                            // 遍历 Cursor ，取出数据
                            String name = cursor.getString(cursor.getColumnIndex("name"));
                            String author = cursor.getString(cursor.getColumnIndex("author"));
                            int pages = cursor.getInt(cursor.getColumnIndex("pages"));
                            double price = cursor.getDouble(cursor.getColumnIndex("price"));
                            LogUtils.i("book name is " + name + "\nbook author is " + author +
                                    "\nbook page is " + pages + "\nbook price is " + price);
                        } while (cursor.moveToNext());
                        cursor.close();
                    }
                }
                break;
            case R.id.btn_update:
                if (sqLiteDatabase != null) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("price", 65.09);
                    sqLiteDatabase.update("Book", contentValues, "name = ?", new String[]{"Booking"});
                }
                break;
            case R.id.btn_delete:
                if (sqLiteDatabase != null) {
                    sqLiteDatabase.delete("Book", "pages > ?", new String[]{"200"});
                }
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
