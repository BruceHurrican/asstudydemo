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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.bruce.demo.social.ShareData;
import com.bruce.demo.utils.LogUtils;
import com.tencent.connect.common.Constants;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

/**
 * QQ 登录分享 QQ空间分享
 * Created by BruceHurrican on 2016/3/27.
 */
public class QQLoginAndShare {
    private static volatile QQLoginAndShare instance;
    private Tencent tencent;
    private Activity context;

    public QQLoginAndShare() {
    }

    public static QQLoginAndShare getInstance() {
        if (null == instance) {
            synchronized (QQLoginAndShare.class) {
                instance = new QQLoginAndShare();
            }
        }
        return instance;
    }

    public QQLoginAndShare init(String QQAppID, Activity context) {
        if (null == tencent) {
            tencent = Tencent.createInstance(QQAppID, context.getApplicationContext());
        }
        this.context = context;
        return this;
    }

    public QQLoginAndShare login(QQListener loginListener) {
        this.login(null, null, null, loginListener);
        return this;
    }

    public QQLoginAndShare login(String token, String expires, String openId, QQListener loginListener) {
        if (null != tencent) {
            if (!tencent.isSessionValid()) {
                String scope = "all"; // 应用需要获得哪些API的权限，由“，”分隔。 例如：SCOPE =“get_user_info,add_t”；所有权限用“all”
                tencent.setAccessToken(token, expires);
                tencent.setOpenId(openId);
                tencent.login(context, scope, loginListener);
            } else {
                LogUtils.i("session 未过期");
            }
        } else {
            LogUtils.e("tencent未初始化");
            Toast.makeText(context, "tencent未初始化", Toast.LENGTH_SHORT).show();
        }
        return this;
    }

    public QQLoginAndShare qqShare(ShareData shareData,QQListener qqShareListener) {
        Bundle bundle = new Bundle();
        //这条分享消息被好友点击后的跳转URL。
        bundle.putString(QQShare.SHARE_TO_QQ_TARGET_URL, shareData.targetUrl);
        //分享的标题。注：PARAM_TITLE、PARAM_IMAGE_URL、PARAM_	SUMMARY不能全为空，最少必须有一个是有值的。
        bundle.putString(QQShare.SHARE_TO_QQ_TITLE, shareData.title);
        //分享的图片URL
        bundle.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, shareData.imgUrl);
        //分享的消息摘要，最长50个字
        bundle.putString(QQShare.SHARE_TO_QQ_SUMMARY, shareData.content);
        if (null != tencent) {
            tencent.shareToQQ(context, bundle, qqShareListener);
        } else {
            LogUtils.e("tencent未初始化");
            Toast.makeText(context, "tencent未初始化", Toast.LENGTH_SHORT).show();
        }
        return this;
    }

    public QQLoginAndShare qzoneShare(ShareData shareData, QQListener qzoneShareListener) {
        Bundle bundle = new Bundle();
        bundle.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        bundle.putString(QzoneShare.SHARE_TO_QQ_TITLE, shareData.title);
        bundle.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, shareData.content);
        bundle.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, shareData.targetUrl);
        bundle.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, shareData.imgList);
//        ArrayList<String> imgList = new ArrayList<String>(1);
//        imgList.add("http://p2.so.qhimg.com/t01dba9a5ac5641a797.jpg");
//        bundle.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, imgList);
        if (null != tencent) {
            tencent.shareToQzone(context, bundle, qzoneShareListener);
        } else {
            LogUtils.e("tencent未初始化");
            Toast.makeText(context, "tencent未初始化", Toast.LENGTH_SHORT).show();
        }
        return this;
    }

    public void loginOut() {
        if (null != tencent) {
            tencent.logout(context);
            releaseTencent();
            Toast.makeText(context, "注销成功", Toast.LENGTH_SHORT).show();
        } else {
            LogUtils.e("tencent未初始化");
            Toast.makeText(context, "tencent未初始化", Toast.LENGTH_SHORT).show();
        }
    }

    public void releaseTencent(){
        if (null!= tencent) {
            tencent.releaseResource();
        }
    }

    /**
     * 保证QQ 登录、分享，QQ空间分享成功回调，此方法应在 activity onActivityResult(int requestCode, int resultCode, Intent data)
     * 中调用，并且调用顺序先于super.onActivityResult(requestCode, resultCode, data);
     *
     * @param requestCode 对应于 activity 中的 requestCode
     * @param resultCode  对应于 activity 中的 resultCode
     * @param data        对应于 activity 中的 activity
     */
    public void QQCallBack(int requestCode, int resultCode, Intent data) {
        this.QQCallBack(requestCode, resultCode, data, null);
    }

    /**
     * 保证QQ 登录、分享，QQ空间分享成功回调，此方法应在 activity onActivityResult(int requestCode, int resultCode, Intent data)
     * 中调用，并且调用顺序先于super.onActivityResult(requestCode, resultCode, data);
     *
     * @param requestCode 对应于 activity 中的 requestCode
     * @param resultCode  对应于 activity 中的 resultCode
     * @param data        对应于 activity 中的 activity
     * @param listener    绑定的监听器,一般情况下是null,如果没有回调,则此处要传入相应的监听器
     */
    public void QQCallBack(int requestCode, int resultCode, Intent data, QQListener listener) {
        switch (requestCode) {
            case Constants.REQUEST_LOGIN:
            case Constants.REQUEST_APPBAR:
                Tencent.onActivityResultData(requestCode, resultCode, data, listener);
                break;
            case Constants.REQUEST_QQ_SHARE:
                Tencent.onActivityResultData(requestCode, resultCode, data, listener);
                break;
            case Constants.REQUEST_QZONE_SHARE:
                Tencent.onActivityResultData(requestCode, resultCode, data, listener);
                break;
        }
    }

    public static abstract class QQListener implements IUiListener {
        public abstract void onOperating(Object result);

        public abstract void onSucceed(Object result);

        public abstract void onFailed(Object result);

        public abstract void onCanceled();

        @Override
        public void onComplete(Object o) {
            onSucceed(o);
        }

        @Override
        public void onError(UiError uiError) {
            onFailed(uiError);
        }

        @Override
        public void onCancel() {
            onCanceled();
        }
    }
}
