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

package com.bruce.demo.social.qq;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.bruce.demo.R;
import com.bruce.demo.base.BaseActivity;
import com.bruce.demo.social.ShareData;
import com.bruce.demo.utils.LogUtils;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * QQ登录分享、QQ空间分享
 * Created by BruceHurrican on 2016/3/27.
 */
public class QQActivity extends BaseActivity {
    public static final String QQ_APPID = "222222";// QQ提供测试ID
    public static Tencent tencent;
    private static String token;
    private static String expires;
    private static String openId;
    @Bind(R.id.btn_qq_login)
    Button btn_qq_login;
    @Bind(R.id.btn_qq_share)
    Button btn_qq_share;
    @Bind(R.id.btn_qq_login_out)
    Button btn_qq_login_out;
    IUiListener loginListener, qqShareListener, qzoneListener;
    @Bind(R.id.btn_qzone_share)
    Button btn_qzone_share;
    private UserInfo mInfo;

    public static void initOpenidAndToken(JSONObject jsonObject) {
        try {
            // todo 可以将此处三个变量 token，expires，openId 本地持久化储存
            token = jsonObject.getString(Constants.PARAM_ACCESS_TOKEN);
            expires = jsonObject.getString(Constants.PARAM_EXPIRES_IN);
            openId = jsonObject.getString(Constants.PARAM_OPEN_ID);
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires) && !TextUtils.isEmpty(openId)) {
                tencent.setAccessToken(token, expires);
                tencent.setOpenId(openId);
            }
        } catch (Exception e) {
            LogUtils.e(e.toString());
        }
    }

    @Override
    public String getTAG() {
        return QQActivity.class.getSimpleName();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qq_activity);
        ButterKnife.bind(this);
        if (null == tencent) {
//            tencent = Tencent.createInstance(QQ_APPID, getApplicationContext());
        }
        initListener();
    }

    private void initListener() {
        loginListener = new BaseUiListener() {
            @Override
            protected void doComplete(JSONObject values) {
                Log.d("SDKQQAgentPref", "AuthorSwitch_SDK:" + SystemClock.elapsedRealtime());
                showToastShort("登录成功~");
//                initOpenidAndToken(values);
                updateUserInfo();
            }
        };
        qqShareListener = new IUiListener() {
            @Override
            public void onComplete(Object response) {
                QQUtil.toastMessage(QQActivity.this, "onComplete: " + response.toString());
                if (null == response) {
                    QQUtil.showResultDialog(QQActivity.this, "返回为空", "分享失败");
                    return;
                }
                QQUtil.toastMessage(QQActivity.this, response.toString(), "分享成功");
            }

            @Override
            public void onError(UiError uiError) {
                QQUtil.toastMessage(QQActivity.this, "onError: " + uiError.errorMessage, "uiError");
            }

            @Override
            public void onCancel() {
                QQUtil.toastMessage(QQActivity.this, "onCancel:分享取消 ");
            }
        };

        qzoneListener = new IUiListener() {

            @Override
            public void onCancel() {
                QQUtil.toastMessage(QQActivity.this, "onCancel:分享取消 ");
            }

            @Override
            public void onError(UiError e) {
                QQUtil.toastMessage(QQActivity.this, "onError: " + e.errorMessage, "e");
            }

            @Override
            public void onComplete(Object response) {
                QQUtil.toastMessage(QQActivity.this, "onComplete: " + response.toString());
            }

        };
    }

    @OnClick({R.id.btn_qq_login, R.id.btn_qq_share, R.id.btn_qzone_share, R.id.btn_qq_login_out})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_qq_login:
                login();
                break;
            case R.id.btn_qq_login_out:
                loginOut();
                break;
            case R.id.btn_qq_share:
                qqShare();
                break;
            case R.id.btn_qzone_share:
                qzoneShare();
                break;
        }
    }

    private void loginOut() {
//        if (null != tencent) {
//            tencent.logout(this);
//        }
        QQLoginAndShare.getInstance().loginOut();
    }

    private void login() {
//        if (null != tencent && !tencent.isSessionValid()) {
//            tencent.setAccessToken(token, expires);
//            tencent.setOpenId(openId);
//            String scope = "all"; // 应用需要获得哪些API的权限，由“，”分隔。 例如：SCOPE =“get_user_info,add_t”；所有权限用“all”
//            tencent.login(QQActivity.this, scope, loginListener);
//        }
        QQLoginAndShare.getInstance().init(QQ_APPID, QQActivity.this).login(new QQLoginAndShare.QQListener() {

            @Override
            public void onOperating(Object result) {

            }

            @Override
            public void onSucceed(Object result) {
                LogUtils.i("result->" + result.toString());
                showToastShort("login succeed~");
            }

            @Override
            public void onFailed(Object result) {
                LogUtils.i("result->" + result.toString());
                showToastShort("login failed~");
            }

            @Override
            public void onCanceled() {
                LogUtils.i("取消登录");
                showToastShort("取消QQ授权登录");
            }
        });
    }

    /**
     * QQ分享
     */
    private void qqShare() {
//        Bundle bundle = new Bundle();
//        //这条分享消息被好友点击后的跳转URL。
//        bundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL, "https://github.com/BruceHurrican");
//        //分享的标题。注：PARAM_TITLE、PARAM_IMAGE_URL、PARAM_	SUMMARY不能全为空，最少必须有一个是有值的。
//        bundle.putString(QQShare.SHARE_TO_QQ_TITLE, "我在测试");
//        //分享的图片URL
//        bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, "http://img3.cache.netease.com/photo/0005/2013-03-07/8PBKS8G400BV0005.jpg");
//        //分享的消息摘要，最长50个字
//        bundle.putString(QQShare.SHARE_TO_QQ_SUMMARY, "测试");
//        if (null!=tencent) {
//            tencent.shareToQQ(QQActivity.this, bundle, qqShareListener);
//        }
        ShareData shareData = new ShareData();
        shareData.targetUrl = "https://github.com/BruceHurrican";
        shareData.title = "github";
        shareData.content = "bruceGithub";
        shareData.imgUrl = "http://img.ivsky.com/img/bizhi/img/201108/03/quarter_of_silence-005.jpg";
        QQLoginAndShare.getInstance().init(QQ_APPID, QQActivity.this).qqShare(shareData,new QQLoginAndShare.QQListener() {
            @Override
            public void onOperating(Object result) {

            }

            @Override
            public void onSucceed(Object result) {
                LogUtils.i("result->" + result.toString());
                showToastShort("qq分享成功");
            }

            @Override
            public void onFailed(Object result) {
                LogUtils.i("result->" + result.toString());
                showToastShort("qq分享失败");
            }

            @Override
            public void onCanceled() {
                LogUtils.i("取消qq分享");
                showToastShort("取消QQ分享");
            }
        });
    }

    /**
     * QQ空间分享
     */
    private void qzoneShare() {
//        Bundle bundle = new Bundle();
//        bundle.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
//        bundle.putString(QzoneShare.SHARE_TO_QQ_TITLE, "test title");
//        bundle.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, "test content");
//        bundle.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, "https://github.com/BruceHurrican");
//        ArrayList<String> imgList = new ArrayList<String>(1);
//        imgList.add("http://p2.so.qhimg.com/t01dba9a5ac5641a797.jpg");
//        bundle.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imgList);
//        if (null!=tencent) {
//            tencent.shareToQzone(QQActivity.this, bundle, qzoneListener);
//        }
        ShareData shareData = new ShareData();
        shareData.title = "bruce";
        shareData.content = "bruceDemo";
        shareData.targetUrl = "http://www.qq.com/";
        ArrayList<String> imgList = new ArrayList<>(5);
        imgList.add("http://wenwen.soso.com/p/20090628/20090628172613-989009059.jpg");
        shareData.imgList = imgList;
        QQLoginAndShare.getInstance().init(QQ_APPID, QQActivity.this).qzoneShare(shareData, new QQLoginAndShare.QQListener() {
            @Override
            public void onOperating(Object result) {

            }

            @Override
            public void onSucceed(Object result) {
                LogUtils.i("result->" + result.toString());
                showToastShort("qq空间分享成功");
            }

            @Override
            public void onFailed(Object result) {
                LogUtils.i("result->" + result.toString());
                showToastShort("qq空间分享失败");
            }

            @Override
            public void onCanceled() {
                LogUtils.i("取消qq空间分享");
                showToastShort("取消QQ空间分享");
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        LogUtils.i("requestCode-> " + requestCode + "\nresultCode-> " + resultCode);
//        switch (requestCode) {
//            case Constants.REQUEST_LOGIN:
//            case Constants.REQUEST_APPBAR:
//                Tencent.onActivityResultData(requestCode, resultCode, data, loginListener);
//                break;
//            case Constants.REQUEST_QQ_SHARE:
//                Tencent.onActivityResultData(requestCode, resultCode, data, qqShareListener);
//                break;
//            case Constants.REQUEST_QZONE_SHARE:
//                Tencent.onActivityResultData(requestCode, resultCode, data, qzoneListener);
//                break;
//        }
        QQLoginAndShare.getInstance().QQCallBack(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        if (null != tencent) {
            tencent.releaseResource();
        }
        QQLoginAndShare.getInstance().releaseTencent();
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    private void updateUserInfo() {
        if (tencent != null && tencent.isSessionValid()) {
            IUiListener listener = new IUiListener() {

                @Override
                public void onError(UiError e) {
                    showToastShort("授权失败");
                }

                @Override
                public void onComplete(final Object response) {
                    showToastShort("授权成功");
                    Message msg = new Message();
                    msg.obj = response;
                    msg.what = 0;
                    sendUIMessage(msg);
                    new Thread() {

                        @Override
                        public void run() {
                            JSONObject json = (JSONObject) response;
                            if (json.has("figureurl")) {
                                Bitmap bitmap = null;
                                try {
                                    bitmap = QQUtil.getbitmap(json.getString("figureurl_qq_2"));
                                } catch (JSONException e) {

                                }
                                Message msg = new Message();
                                msg.obj = bitmap;
                                msg.what = 1;
                                sendUIMessage(msg);
                            }
                        }

                    }.start();
                }

                @Override
                public void onCancel() {
                    showToastShort("授权取消");
                }
            };
            mInfo = new UserInfo(this, tencent.getQQToken());
            mInfo.getUserInfo(listener);

        } else {
//            iv_user.setVisibility(View.GONE);
        }
    }

    private class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(Object response) {
            if (null == response) {
                QQUtil.showResultDialog(QQActivity.this, "返回为空", "登录失败");
                return;
            }
            JSONObject jsonResponse = (JSONObject) response;
            if (null != jsonResponse && jsonResponse.length() == 0) {
                QQUtil.showResultDialog(QQActivity.this, "返回为空", "登录失败");
                return;
            }
            QQUtil.showResultDialog(QQActivity.this, response.toString(), "登录成功");
            doComplete((JSONObject) response);
        }

        protected void doComplete(JSONObject values) {

        }

        @Override
        public void onError(UiError e) {
            QQUtil.toastMessage(QQActivity.this, "onError: " + e.errorDetail);
            QQUtil.dismissDialog();
        }

        @Override
        public void onCancel() {
            QQUtil.toastMessage(QQActivity.this, "onCancel: ");
            QQUtil.dismissDialog();
        }
    }
}
