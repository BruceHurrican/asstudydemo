package com.bruce.demo.studydata.fragments.intentservice;

import android.app.IntentService;
import android.content.Intent;

import com.bruceutils.utils.LogUtils;

/**
 * 可以处理耗时工作,耗时工作处理完后自销毁
 * Created by hrk on 16/5/30.
 */
public class MyIntentService extends IntentService {

    public static final String FLAG = "aa";

    public MyIntentService() {
        super("MyIntentService thread");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.i("onStartCommand start===="+Thread.currentThread());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        LogUtils.i(intent.getStringExtra(FLAG));
        LogUtils.i("current thread: "+Thread.currentThread());
    }

    @Override
    public void onDestroy() {
        LogUtils.i("intent service destroy");
        super.onDestroy();
    }
}
