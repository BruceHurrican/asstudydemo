package com.bruce.demo.studydata.activities.ipc;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.bruceutils.utils.LogUtils;

/**
 * Created by hrk on 16/5/27.
 */
public class MyIPCService extends Service {
    public static final String FLAG = "flag";
    private boolean flag;
    private Handler handler;
    MyServiceAIDL.Stub ms = new MyServiceAIDL.Stub() {
        @Override
        public void showTxt(final String txt) throws RemoteException {
            LogUtils.i(txt);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MyIPCService.this, txt, Toast.LENGTH_SHORT).show();
                }
            }, 1500);
        }

        @Override
        public int calculate(int a, int b) throws RemoteException {
            return flag ? (a + b) : (a - b);
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Bundle bundle = intent.getExtras();
        flag = bundle.getBoolean(FLAG);
        return ms;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler = new Handler(Looper.myLooper());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        flag = false;
    }
}
