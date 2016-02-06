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

package com.bruce.demo.studydata.fragments.googlesample.api10.contact_manager;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.bruce.demo.base.BaseFragment;
import com.bruce.demo.studydata.fragments.FragmentsActivity;
import com.bruce.demo.R;
import com.bruce.demo.utils.LogUtils;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;
import butterknife.OnClick;

/**
 * google api10 sample ContactManager 查看联系人
 * Created by BruceHurrican on 2015/12/13.
 */
public class ContactManagerFragment extends BaseFragment {
    @Bind(R.id.lv_contact)
    ListView lv_contact;
    @Bind(R.id.cb_contact)
    CheckBox cb_contact;
    @Bind(R.id.btn_contact)
    Button btn_contact;
    private boolean isShowInvisible;

    @Override
    public String getTAG() {
        return "ContactManagerFragment";
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contact_fragment_manager, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isShowInvisible = false;
        cb_contact.setChecked(isShowInvisible);
        populateContactList();
    }

    @OnClick(R.id.btn_contact)
    public void onClick(View view) {
        LogUtils.d("添加联系人按钮被点击");
        ((FragmentsActivity) getActivity()).fragment2fragment(new ContactAdderFragment(), "添加联系人界面");
        if (getActivity().getClass().isInstance(FragmentsActivity.class)) {
            LogUtils.d("fragment跳转fragment");
        }
    }

    @OnCheckedChanged(R.id.cb_contact)
    public void onChecked(boolean checked) {
        LogUtils.d("是否显示不可见联系人：" + checked);
        isShowInvisible = checked;
        populateContactList();
    }

    /**
     * Populate the contact list based on account currently selected in the account spinner
     */
    private void populateContactList() {
        Cursor cursor = getContacts();
        String[] fileds = new String[]{ContactsContract.Data.DISPLAY_NAME};
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(getActivity(), R.layout.contact_entry, cursor, fileds, new int[]{R.id.tv_contact_entry});
        lv_contact.setAdapter(adapter);
    }

    /**
     * Obtain the contact list for the currently selected account.
     *
     * @return A cursor for accessing the contact list.
     */
    private Cursor getContacts() {
        // Run query
        Uri uri = ContactsContract.Contacts.CONTENT_URI;
        String[] projection = new String[]{ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME};
        String selection = ContactsContract.Contacts.IN_VISIBLE_GROUP + " = '" + (isShowInvisible ? "0" : "1") + "'";
        String[] selectionArgs = null;
        String sortOrder = ContactsContract.Contacts.DISPLAY_NAME + " COLLATE LOCALIZED ASC";
        return getActivity().managedQuery(uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
