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

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorDescription;
import android.accounts.OnAccountsUpdateListener;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bruce.demo.R;
import com.bruce.demo.base.BaseFragment;
import com.bruce.demo.utils.LogUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 增加联系人界面
 * Created by BruceHurrican on 2015/12/13.
 */
public class ContactAdderFragment extends BaseFragment implements OnAccountsUpdateListener {
    public static final String ACCOUNT_NAME = "google_api10_sample_contact_adder_ACCOUNT_NAME";
    public static final String ACCOUNT_TYPE = "google_api10_sample_contact_adder_ACCOUNT_TYPE";
    @Bind(R.id.sp_accountSpinner)
    Spinner spAccountSpinner;
    @Bind(R.id.et_contactNameEditText)
    EditText etContactNameEditText;
    @Bind(R.id.et_contactPhone)
    EditText etContactPhone;
    @Bind(R.id.sp_contactPhoneType)
    Spinner spContactPhoneType;
    @Bind(R.id.et_contactEmail)
    EditText etContactEmail;
    @Bind(R.id.sp_contactEmail)
    Spinner spContactEmail;
    @Bind(R.id.btn_contactSave)
    Button btnContactSave;
    private List<AccountData> mAccounts;
    private List<Integer> mContactEmailTypes;
    private List<Integer> mContactPhoneTypes;
    private AccountData mSelectedAccount;
    private AccountAdapter mAccountAdapter;

    @Override
    public String getTAG() {
        return "ContactAdderFragment";
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contact_fragment_adder, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContactPhoneTypes = new ArrayList<>(5);
        mContactPhoneTypes.add(ContactsContract.CommonDataKinds.Phone.TYPE_HOME);
        mContactPhoneTypes.add(ContactsContract.CommonDataKinds.Phone.TYPE_WORK);
        mContactPhoneTypes.add(ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
        mContactPhoneTypes.add(ContactsContract.CommonDataKinds.Phone.TYPE_OTHER);
        mContactEmailTypes = new ArrayList<>(5);
        mContactEmailTypes.add(ContactsContract.CommonDataKinds.Email.TYPE_HOME);
        mContactEmailTypes.add(ContactsContract.CommonDataKinds.Email.TYPE_WORK);
        mContactEmailTypes.add(ContactsContract.CommonDataKinds.Email.TYPE_MOBILE);
        mContactEmailTypes.add(ContactsContract.CommonDataKinds.Email.TYPE_OTHER);

        // Prepare model for account spinner
        mAccounts = new ArrayList<>(5);
        mAccountAdapter = new AccountAdapter(getActivity(), mAccounts);
        spAccountSpinner.setAdapter(mAccountAdapter);
        //Populate list of account types for phone
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Iterator<Integer> iter = mContactPhoneTypes.iterator();
        while (iter.hasNext()) {
            adapter.add(ContactsContract.CommonDataKinds.Phone.getTypeLabel(getResources(), iter.next(), "未定义").toString());
        }
        spContactPhoneType.setAdapter(adapter);
        spContactPhoneType.setPrompt("选择标签");

        // Populate list of account types for email
        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        iter = mContactEmailTypes.iterator();
        while (iter.hasNext()) {
            adapter.add(ContactsContract.CommonDataKinds.Email.getTypeLabel(getResources(), iter.next(), "未定义").toString());
        }
        spContactEmail.setAdapter(adapter);
        spContactEmail.setPrompt("选择标签");

        // Prepare the system account manager. On registering the listener below, we also ask for an
        // initial callback to pre-populate the account list.
        AccountManager.get(getActivity()).addOnAccountsUpdatedListener(this, null, true);

        // Register handlers for UI elements
        spAccountSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Update account selection. If NO_ACCOUNT is selected, then we prohibit inserting new contacts.
                // Read current account selection
                mSelectedAccount = (AccountData) spAccountSpinner.getSelectedItem();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // We don't need to worry about nothing being selected, since Spinners don't allow this
            }
        });

    }

    @OnClick(R.id.btn_contactSave)
    public void onClick(View view) {
        LogUtils.d("保存按钮被点击");
        createContactEntry();
//        testAddContacts1();
//        testAddContacts2();
        getActivity().onBackPressed();
    }

    /**
     * 一条一条按序添加联系人数据
     */
    public void testAddContacts1() {
        //插入raw_contacts表，并获取_id属性
        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        ContentResolver resolver = this.getContext().getContentResolver();
        ContentValues values = new ContentValues();
        long contact_id = ContentUris.parseId(resolver.insert(uri, values));
        //插入data表
        uri = Uri.parse("content://com.android.contacts/data");
        //add Name
        values.put("raw_contact_id", contact_id);
        values.put(ContactsContract.Data.MIMETYPE, "vnd.android.cursor.item/name");
        values.put("data2", "lala");
        values.put("data1", "hehe");
        resolver.insert(uri, values);
        values.clear();
        //add Phone
        values.put("raw_contact_id", contact_id);
        values.put(ContactsContract.Data.MIMETYPE, "vnd.android.cursor.item/phone_v2");
        values.put("data2", "2");   //手机
        values.put("data1", "13232321212");
        resolver.insert(uri, values);
        values.clear();
        //add email
        values.put("raw_contact_id", contact_id);
        values.put(ContactsContract.Data.MIMETYPE, "vnd.android.cursor.item/email_v2");
        values.put("data2", "2");   //单位
        values.put("data1", "aa@qq.com");
        resolver.insert(uri, values);
    }

    /**
     * 用批量添加数据方式向联系人列表中插入数据
     */
    public void testAddContacts2() {
        Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
        ContentResolver resolver = getContext().getContentResolver();
        ArrayList<ContentProviderOperation> operations = new ArrayList<ContentProviderOperation>();
        ContentProviderOperation op1 = ContentProviderOperation.newInsert(uri).withValue("account_name", null).build();
        operations.add(op1);

        uri = Uri.parse("content://com.android.contacts/data");
        ContentProviderOperation op2 = ContentProviderOperation.newInsert(uri).withValueBackReference("raw_contact_id", 0).withValue("mimetype", "vnd.android.cursor.item/name").withValue("data2", "bruce").build();
        operations.add(op2);

        ContentProviderOperation op3 = ContentProviderOperation.newInsert(uri).withValueBackReference("raw_contact_id", 0).withValue("mimetype", "vnd.android.cursor.item/phone_v2").withValue("data1", "13112341234").withValue("data2", "2").build();
        operations.add(op3);

        ContentProviderOperation op4 = ContentProviderOperation.newInsert(uri).withValueBackReference("raw_contact_id", 0).withValue("mimetype", "vnd.android.cursor.item/email_v2").withValue("data1", "aa@qq.com").withValue("data2", "2").build();
        operations.add(op4);

        try {
            LogUtils.d("执行插入数据操作");
            resolver.applyBatch("com.android.contacts", operations);
            LogUtils.d("结束插入数据操作");
        } catch (RemoteException | OperationApplicationException e) {
            LogUtils.e("创建联系人失败,日志内容：" + e.toString());
        }
    }

    /**
     * Creates a contact entry from the current UI values in the account named by mSelectedAccount
     */
    private void createContactEntry() {
        String name = etContactNameEditText.getText().toString();
        String phone = etContactPhone.getText().toString();
        String email = etContactEmail.getText().toString();
        int phoneType = mContactPhoneTypes.get(spContactPhoneType.getSelectedItemPosition());
        int emailType = mContactEmailTypes.get(spContactEmail.getSelectedItemPosition());
        // 模拟器环境下 mSelectedAccount 为 null，导致插入数据成功后，在联系人列表不显示(这种场景是因为 ContactManagerFragment.java 中显示联系人的条件是根据 DISPLAY_NAME 来显示的)，实际上数据已经插入到联系人列表中
        if (null == mSelectedAccount) {
            mSelectedAccount = new AccountData();
        }
        /* Prepare contact creation request
            Note: We use RawContacts because this data must be associated with a particular account.
            The system will aggregate this with any other data for this contact and create a coresponding entry in the ContactsContract.Contacts provider for us.
         */
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>(5);
        ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI).withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, mSelectedAccount.getmType()).withValue(ContactsContract.RawContacts.ACCOUNT_NAME, mSelectedAccount.getmName()).build());
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0).withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE).withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name).build());
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0).withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE).withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, phone).withValue(ContactsContract.CommonDataKinds.Phone.TYPE, phoneType).build());
        ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI).withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0).withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE).withValue(ContactsContract.CommonDataKinds.Email.DATA, email).withValue(ContactsContract.CommonDataKinds.Email.TYPE, emailType).build());

        // Ask the Contact provider to create a new contact
        LogUtils.i("Selected account: " + mSelectedAccount.getmName() + " (" + mSelectedAccount.getmType() + ")");
        LogUtils.i("Creating contact: " + name);
        try {
            getContext().getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (RemoteException | OperationApplicationException e) {
            LogUtils.e("创建联系人失败,日志内容：" + e.toString());
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        AccountManager.get(getContext()).removeOnAccountsUpdatedListener(this);
        ButterKnife.unbind(this);
    }

    /**
     * Updates account list spinner when the list of Accounts on the system changes. Satisfies OnAccountsUpdateListener implementation.
     *
     * @param accounts
     */
    @Override
    public void onAccountsUpdated(Account[] accounts) {
        LogUtils.i("Account list update detected");
        // Clear out any old data to prevent duplicates.
        mAccounts.clear();

        // Get account data from system
        AuthenticatorDescription[] accountTypes = AccountManager.get(getContext()).getAuthenticatorTypes();
        // Populate tables
        for (int i = 0; i < accounts.length; i++) {
            // The user may have multiple accounts with the same name, so we need to construct a meaningful display name for each.
            String systemAccountType = accounts[i].type;
            AuthenticatorDescription ad = getAuthenticatorDescription(systemAccountType, accountTypes);
            AccountData data = new AccountData(accounts[i].name, ad);
            mAccounts.add(data);
        }

        // Update the account spinner
        mAccountAdapter.notifyDataSetChanged();
    }

    private AuthenticatorDescription getAuthenticatorDescription(String type, AuthenticatorDescription[] dictionary) {
        for (int i = 0; i < dictionary.length; i++) {
            if (dictionary[i].type.equals(type)) {
                return dictionary[i];
            }
        }
        // No match found
        throw new RuntimeException("Unable to find matching authenticator");
    }

    /**
     * A container class used to represent all known information about an account.
     */
    private class AccountData {
        private String mName;
        private String mType;
        private CharSequence mTypeLabel;
        private Drawable mIcon;

        public AccountData() {
            this.mName = "defaultName";
            this.mType = "defaultType";
            this.mTypeLabel = "defatultTypeLabel";
            this.mIcon = getResources().getDrawable(android.R.drawable.sym_def_app_icon);
        }

        public AccountData(String name, AuthenticatorDescription description) {
            mName = name;
            if (null != description) {
                mType = description.type;
                // The type string is stored in a resource, so we need to convert it into something human readable.
                String packageName = description.packageName;
                PackageManager pm = getActivity().getPackageManager();
                if (description.labelId != 0) {
                    mTypeLabel = pm.getText(packageName, description.labelId, null);
                    if (null == mTypeLabel) {
                        throw new IllegalArgumentException("LabelID provided, but label not fount");
                    }
                } else {
                    mTypeLabel = "";
                }

                if (description.iconId != 0) {
                    mIcon = pm.getDrawable(packageName, description.iconId, null);
                    if (null == mIcon) {
                        throw new IllegalArgumentException("IconID provided, but drawable not found");
                    }
                } else {
                    mIcon = getResources().getDrawable(android.R.drawable.sym_def_app_icon);
                }
            }
        }

        public Drawable getmIcon() {
            return mIcon;
        }

        public CharSequence getmTypeLabel() {
            return mTypeLabel;
        }

        public String getmType() {
            return mType;
        }

        public String getmName() {
            return mName;
        }

        @Override
        public String toString() {
            return mName;
        }
    }

    class AccountAdapter extends ArrayAdapter<AccountData> {
        public AccountAdapter(Context context, List<AccountData> accountData) {
            super(context, android.R.layout.simple_spinner_item, accountData);
            setDropDownViewResource(R.layout.contact_account_entry);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (null == convertView) {
                LayoutInflater layoutInflater = getActivity().getLayoutInflater();
                convertView = layoutInflater.inflate(R.layout.contact_account_entry, parent, false);
                holder = new ViewHolder(convertView);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            AccountData data = getItem(position);
            holder.tvFirstAccountLine.setText(data.getmName());
            holder.tvSecondAccountLine.setText(data.getmTypeLabel());
            Drawable icon = data.getmIcon();
            if (null == icon) {
                icon = getResources().getDrawable(android.R.drawable.ic_menu_search);
            }
            holder.ivAccountIcon.setImageDrawable(icon);
            return convertView;
        }


        /**
         * This class contains all butterknife-injected Views & Layouts from layout file 'contact_account_entry.xml'
         * for easy to all layout elements.
         *
         * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
         */
        class ViewHolder {
            @Bind(R.id.iv_accountIcon)
            ImageView ivAccountIcon;
            @Bind(R.id.tv_secondAccountLine)
            TextView tvSecondAccountLine;
            @Bind(R.id.tv_firstAccountLine)
            TextView tvFirstAccountLine;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }
}
