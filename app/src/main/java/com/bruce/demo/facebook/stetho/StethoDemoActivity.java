/*
 * Copyright (c) 2014-present, Facebook, Inc.
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree. An additional grant
 * of patent rights can be found in the PATENTS file in the same directory.
 */

package com.bruce.demo.facebook.stetho;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bruce.demo.BuildConfig;
import com.bruce.demo.R;
import com.bruce.demo.base.BaseActivity;
import com.bruce.demo.utils.LogUtils;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StethoDemoActivity extends BaseActivity {
    private final View.OnClickListener mMainButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.settings_btn) {
                StethoDemoSettingsActivity.show(StethoDemoActivity.this);
            } else if (id == R.id.apod_btn) {
                APODActivity.show(StethoDemoActivity.this);
            }
        }
    };
    private final SharedPreferences.OnSharedPreferenceChangeListener mToastingPrefListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Object value = sharedPreferences.getAll().get(key);
            Toast.makeText(StethoDemoActivity.this, String.format("%s is now \\'%s\\'", key, value), Toast.LENGTH_SHORT).show();
        }
    };
    @Bind(R.id.settings_btn)
    Button settingsBtn;
    @Bind(R.id.apod_btn)
    Button apodBtn;
    @Bind(R.id.url_btn)
    Button urlBtn;

    private static boolean isStethoPresent() {
        try {
            Class.forName("com.facebook.stetho.Stetho");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stetho_demo_activity);
        ButterKnife.bind(this);

        // Demonstrate that it is removed from the release build...
        if (!isStethoPresent()) {
            Toast.makeText(this, String.format("Stetho missing in %s build!", BuildConfig.BUILD_TYPE), Toast.LENGTH_LONG).show();
        }

        findViewById(R.id.settings_btn).setOnClickListener(mMainButtonClicked);
        findViewById(R.id.apod_btn).setOnClickListener(mMainButtonClicked);
    }

    @OnClick({R.id.url_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.url_btn:
                Networker.HttpRequest imageRequest = Networker.HttpRequest.newBuilder().method(Networker.HttpMethod.GET).url("http://www.baidu.com/").build();
                Networker.get().submit(imageRequest, new Networker.Callback() {
                    @Override
                    public void onResponse(Networker.HttpResponse result) {
                        LogUtils.d("Got " + ": " + result.statusCode + ", " + result.body.length);
                        if (result.statusCode == 200) {
                            showToastShort(result.statusCode + "");
//                            final Bitmap bitmap = BitmapFactory.decodeByteArray(result.body, 0, result.body.length);
//                            StethoDemoActivity.this.runOnUiThread(new Runnable() {
//
//                                @Override
//                                public void run() {
//                                    holder.image.setImageDrawable(new BitmapDrawable(bitmap));
//                                }
//                            });
                        }
                    }

                    @Override
                    public void onFailure(IOException e) {
                        // Let Stetho demonstrate the errors :)
                    }
                });
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPrefs().registerOnSharedPreferenceChangeListener(mToastingPrefListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        getPrefs().unregisterOnSharedPreferenceChangeListener(mToastingPrefListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public String getTAG() {
        return StethoDemoActivity.class.getSimpleName();
    }

    private SharedPreferences getPrefs() {
        return PreferenceManager.getDefaultSharedPreferences(this /* context */);
    }
}
