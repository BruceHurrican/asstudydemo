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

package com.bruce.demo;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.CalendarContract;

import com.bruce.demo.utils.Constants;
import com.bruce.demo.utils.LogUtils;
import com.facebook.stetho.InspectorModulesProvider;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.inspector.database.ContentProviderDatabaseDriver;
import com.facebook.stetho.inspector.database.ContentProviderSchema;
import com.facebook.stetho.inspector.protocol.ChromeDevtoolsDomain;
import com.facebook.stetho.rhino.JsRuntimeReplFactoryBuilder;
import com.github.moduth.blockcanary.BlockCanary;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import java.util.ArrayList;
import java.util.List;

/**
 * 全局 application
 * Created by BruceHurrican on 2015/6/7.
 */
public class DemoApplication extends Application {
    // ===============game 2048==============================
    public static SharedPreferences mSp;
    public static int mGameGoal;
    public static int mGameLines;
    public static int mItemSize;
    public static int SCORE = 0;
    public static String SP_HIGH_SCORE = "SP_HIGH_SCORE";
    public static String KEY_HIGH_SCORE = "KEY_HIGH_SCORE";
    public static String KEY_GAME_LINES = "KEY_GAME_LINES";
    public static String KEY_GAME_GOAL = "KEY_GAME_GOAL";
    private static Context sContext;
    public Context demoAppContext;
    // used in volley_demo
//    private static RequestQueue queues;
    private List<Activity> container;
    // memory leak tools
    private RefWatcher refWatcher;
    // =====================================================

    public static RefWatcher getRefWatcher(Context context) {
        DemoApplication application = (DemoApplication) context.getApplicationContext();
        return application.refWatcher;
    }

//    public static RequestQueue getHttpQueues() {
//        return queues;
//    }

    public static Context getAppContext() {
        return sContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (null == demoAppContext) {
            demoAppContext = getApplicationContext();
        }
//        queues = Volley.newRequestQueue(demoAppContext);
//        CrashHandler crashHandler = CrashHandler.getInstance();
//        crashHandler.init(getApplicationContext());
//        crashHandler.initActivityContainer(container);
        container = new ArrayList<>(5);

        if (Constants.IS_OPEN_UI_BLOCK_CANARY) {
            sContext = this;
            BlockCanary.install(this, new DemoBlockCanaryContext()).start();
        }
        if (Constants.IS_OPEN_LEAK_CANARY) {
            refWatcher = initLeakCanary();
        }

        if (Constants.IS_OPEN_STETHO){
            // 查看 dumpapp 信息
//            Stetho.initialize(Stetho.newInitializerBuilder(this).enableDumpapp(new DumperPluginsProvider() {
//                @Override
//                public Iterable<DumperPlugin> get() {
//                    return new Stetho.DefaultDumperPluginsBuilder(DemoApplication.this).provide(new HelloWorldDumperPlugin()).provide(new APODDumperPlugin(DemoApplication.this.getContentResolver())).finish();
//                }
//            }).enableWebKitInspector(new ExtInspectorModulesProvider(DemoApplication.this)).build());
            // 调用 JS
            Stetho.initialize(Stetho.newInitializerBuilder(DemoApplication.this)
                    .enableWebKitInspector(new InspectorModulesProvider() {
                        @Override
                        public Iterable<ChromeDevtoolsDomain> get() {
                            return new Stetho.DefaultInspectorModulesBuilder(DemoApplication.this).runtimeRepl(
                                    new JsRuntimeReplFactoryBuilder(DemoApplication.this)
                                            // Pass to JavaScript: var foo = "bar";
                                            .addVariable("foo", "bar")
                                            .build()
                            ).finish();
                        }
                    })
                    .build());
        }

        // game 2048
        mSp = getSharedPreferences(SP_HIGH_SCORE, 0);
        mGameLines = mSp.getInt(KEY_GAME_LINES, 4);
        mGameGoal = mSp.getInt(KEY_GAME_GOAL, 2048);
        mItemSize = 0;
    }

    private RefWatcher initLeakCanary() {
        return Constants.ISDEBUG ? LeakCanary.install(this) : RefWatcher.DISABLED;
    }

    public void addActivity(Activity activity) {
        if (null != activity) {
            container.add(activity);
            LogUtils.i("加入的activity：" + activity.getLocalClassName());
        } else {
            LogUtils.e("加入的activity为空");
        }
    }

    public void delActivity(Activity activity) {
        if (null != activity) {
            LogUtils.i("销毁的activity：" + activity.getLocalClassName());
            container.remove(activity);
        } else {
            LogUtils.e("待删除的activity为空");
        }
        if (null != container && container.size() == 0) {
            exitApp();
        }
    }

    public void exitApp() {
        if (container == null || container.size() == 0) {
            LogUtils.i("activity容器已经清空");
            android.os.Process.killProcess(android.os.Process.myPid());
            return;
        }
        for (Activity activity : container) {
            LogUtils.i("程序有序退出中，当前activity：" + activity.getLocalClassName());
            activity.finish();
        }
//        queues.stop();
//        queues = null;
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    // for stetho
    private static class ExtInspectorModulesProvider implements InspectorModulesProvider {

        private Context mContext;

        ExtInspectorModulesProvider(Context context) {
            mContext = context;
        }

        @Override
        public Iterable<ChromeDevtoolsDomain> get() {
            return new Stetho.DefaultInspectorModulesBuilder(mContext)
                    .provideDatabaseDriver(createContentProviderDatabaseDriver(mContext))
                    .finish();
        }

        @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
        private ContentProviderDatabaseDriver createContentProviderDatabaseDriver(Context context) {
            ContentProviderSchema calendarsSchema = new ContentProviderSchema.Builder()
                    .table(new ContentProviderSchema.Table.Builder()
                            .uri(CalendarContract.Calendars.CONTENT_URI)
                            .projection(new String[] {
                                    CalendarContract.Calendars._ID,
                                    CalendarContract.Calendars.NAME,
                                    CalendarContract.Calendars.ACCOUNT_NAME,
                                    CalendarContract.Calendars.IS_PRIMARY,
                            })
                            .build())
                    .build();

            // sample events content provider we want to support
            ContentProviderSchema eventsSchema = new ContentProviderSchema.Builder()
                    .table(new ContentProviderSchema.Table.Builder()
                            .uri(CalendarContract.Events.CONTENT_URI)
                            .projection(new String[]{
                                    CalendarContract.Events._ID,
                                    CalendarContract.Events.TITLE,
                                    CalendarContract.Events.DESCRIPTION,
                                    CalendarContract.Events.ACCOUNT_NAME,
                                    CalendarContract.Events.DTSTART,
                                    CalendarContract.Events.DTEND,
                                    CalendarContract.Events.CALENDAR_ID,
                            })
                            .build())
                    .build();
            return new ContentProviderDatabaseDriver(context, calendarsSchema, eventsSchema);
        }
    }
}
