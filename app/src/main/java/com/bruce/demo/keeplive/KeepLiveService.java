/*
 *   BruceHurrican
 *   Copyright (c) 2016.
 *   Licensed under the Apache License, Version 2.0 (the "License");
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
 *   This document is Bruce's individual learning the android demo, wherein the use of the code from the Internet, only to use as a learning exchanges.
 *   And where any person can download and use, but not for commercial purposes.
 *   Author does not assume the resulting corresponding disputes.
 *   If you have good suggestions for the code, you can contact BurrceHurrican@foxmail.com
 *   本文件为Bruce's个人学习android的demo, 其中所用到的代码来源于互联网，仅作为学习交流使用。
 *   任和何人可以下载并使用, 但是不能用于商业用途。
 *   作者不承担由此带来的相应纠纷。
 *   如果对本代码有好的建议，可以联系BurrceHurrican@foxmail.com
 */

package com.bruce.demo.keeplive;

import android.app.Notification;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.bruceutils.utils.logdetails.LogDetails;

/**
 * 进程保活服务
 * Created by hrk on 16/6/24.
 */
public class KeepLiveService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (Build.VERSION.SDK_INT < 18) {
            // API < 18 ，此方法能有效隐藏Notification上的图标
            startForeground(1001, new Notification());
        } else {
            Intent innerIntent = new Intent(this, KeepLiveInnerService.class);
            startService(innerIntent);
            startForeground(1001, new Notification());
        }
        LogDetails.getLogConfig().configShowBorders(true);
        LogDetails.i("保活服务开启-所在进程-" + android.os.Process.myPid());
        bindService(new Intent(this, KeepLiveService.class), new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                // 待处理业务逻辑
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        }, BIND_AUTO_CREATE);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogDetails.i("保活服务关闭");
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * 给 API >= 18 的平台上用的灰色保活手段
     */
    private static class KeepLiveInnerService extends Service {

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {

            startForeground(1001, new Notification());
            stopForeground(true);
            stopSelf();
            return super.onStartCommand(intent, flags, startId);
        }

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }
}
