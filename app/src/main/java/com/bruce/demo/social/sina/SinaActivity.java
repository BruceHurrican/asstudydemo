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

package com.bruce.demo.social.sina;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.bruce.demo.R;
import com.bruce.demo.base.BaseActivity;
import com.bruce.demo.utils.LogUtils;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by BruceHurrican on 16-3-29.
 */
public class SinaActivity extends BaseActivity {
    public static final String SINA_APPID = "2045436852";
    public static final String SINA_REDIRECT_URL= "https://api.weibo.com/oauth2/default.html";
    public static final String SINA_SCOPE =
            "email,direct_messages_read,direct_messages_write,"
                    + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                    + "follow_app_official_microblog," + "invitation_write";
    @Bind(R.id.btn_sina_login)
    Button btnSinaLogin;
    @Bind(R.id.btn_sina_share)
    Button btnSinaShare;
    private Oauth2AccessToken accessToken;
    private SsoHandler ssoHandler;
    private AuthInfo authInfo;

    @Override
    public String getTAG() {
        return SinaActivity.class.getSimpleName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sina_activity);
        ButterKnife.bind(this);

        authInfo = new AuthInfo(SinaActivity.this,SINA_APPID,SINA_REDIRECT_URL,SINA_SCOPE);
        ssoHandler = new SsoHandler(SinaActivity.this, authInfo);

        IWeiboShareAPI weiboShareAPI = WeiboShareSDK.createWeiboAPI(SinaActivity.this,SINA_APPID);
        weiboShareAPI.registerApp();
    }

    @OnClick({R.id.btn_sina_login,R.id.btn_sina_share})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.btn_sina_login:
                ssoHandler.authorize(new WeiboAuthListener() {
                    @Override
                    public void onComplete(Bundle bundle) {
                        LogUtils.i("bundle->"+bundle);
                        accessToken = Oauth2AccessToken.parseAccessToken(bundle);
                        if (accessToken.isSessionValid()){
                            AccessTokenKeeper.writeAccessToken(SinaActivity.this,accessToken);
                        } else {

                        }
                    }

                    @Override
                    public void onWeiboException(WeiboException e) {
                        LogUtils.e("授权失败->"+e.toString());
                    }

                    @Override
                    public void onCancel() {
                        LogUtils.i("取消新浪登录");
                    }
                });
                break;
            case R.id.btn_sina_share:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (ssoHandler!=null){
            ssoHandler.authorizeCallBack(requestCode,resultCode,data);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
