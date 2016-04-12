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

package com.bruceutils.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 工具类
 * Created by BruceHurrican on 2015/7/11.
 */
public final class PublicUtil {
    private static final int kSystemRootStateUnknow = -1;
    private static final int kSystemRootStateDisable = 0;
    private static final int kSystemRootStateEnable = 1;
    private static int systemRootState = kSystemRootStateUnknow;

    private PublicUtil() {
    }

    /**
     * 获取手机设备相关信息
     *
     * @return String
     */
    public static String getPhoneInfo(Context context) {
        return "产品名称：" + Build.PRODUCT + "\nCPU型号:" + Build.HARDWARE + "\nCPU类型1:" + Build.CPU_ABI + "\nCPU类型2:" +
                Build.CPU_ABI2 + "\n标签:" + Build.TAGS + "\n手机型号:" + Build.MODEL + "\nSDK版本:" + Build.VERSION.SDK +
                "\nSDK版本号:" + Build.VERSION.SDK_INT + "\n系统版本:" + Build.VERSION.RELEASE + "\n设备安卓版本:" + Build.VERSION.RELEASE + "\n设备驱动:" + Build.DEVICE + "\n显示:" + Build.DISPLAY + "\n品牌:" + Build.BRAND + "\n主板:" +
                Build.BOARD + "\n标识:" + Build.FINGERPRINT + "\nID:" + Build.ID + "\n制造商:" + Build.MANUFACTURER + "\n用户组:" + Build.USER + "\n序列号:" + Build.SERIAL + "\nandroid设备标识码:" + Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    /**
     * 获取手机中所有应用的每个应用的包名和权限名称
     */
    public static String getAllAppPackageNameAndPermission(Context context) {
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> list = packageManager.getInstalledPackages(PackageManager.GET_PERMISSIONS);
        StringBuilder stringBuilder = new StringBuilder();
        for (PackageInfo packageInfo : list) {
            stringBuilder.append("package name:").append(packageInfo.packageName).append("\n");
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            stringBuilder.append("应用名称:").append(applicationInfo.loadLabel(packageManager)).append("\n");
            if (packageInfo.permissions != null) {
                for (PermissionInfo p : packageInfo.permissions) {
                    stringBuilder.append("权限包括:").append(p.name).append("\n");
                }
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    /**
     * 查询手机内所有支持分享的应用
     */
    public static String getShareApps(Context context) {
        List<ResolveInfo> mApps;
        Intent intent = new Intent(Intent.ACTION_SEND, null);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setType("text/plain");
        PackageManager pManager = context.getPackageManager();
        mApps = pManager.queryIntentActivities(intent, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
        StringBuilder stringBuilder = new StringBuilder();
        for (ResolveInfo resolve : mApps) {
            resolve.loadIcon(pManager);
            //set Application Name
            stringBuilder.append(resolve.loadLabel(pManager).toString()).append("\n");
            //set Package Name
            stringBuilder.append(resolve.activityInfo.packageName).append("\n");
        }
        return stringBuilder.toString();
    }

    /**
     * 查询手机内非系统应用
     */
    public static String getAllNonSystemApps(Context context) {
        List<PackageInfo> apps = new ArrayList<PackageInfo>();
        PackageManager pManager = context.getPackageManager();
        // 获取手机内所有应用
        List<PackageInfo> paklist = pManager.getInstalledPackages(0);
        for (PackageInfo aPaklist : paklist) {
            PackageInfo pak = aPaklist;
            // 判断是否为非系统预装的应用程序
            if ((pak.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) <= 0) {
                // customs applications
                apps.add(pak);
            }
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (PackageInfo packageInfo : apps) {
            stringBuilder.append("package name:").append(packageInfo.packageName).append("\n");
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            stringBuilder.append("应用名称:").append(applicationInfo.loadLabel(pManager)).append("\n");
            if (packageInfo.permissions != null) {
                for (PermissionInfo p : packageInfo.permissions) {
                    stringBuilder.append("权限包括:").append(p.name).append("\n");
                }
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    /**
     * 判断手机是否已经root
     *
     * @return boolean
     */
    public static String isRootSystem() {
        if (systemRootState == kSystemRootStateEnable) {
            return "当前手机是否已经root ? 已经root";
        } else if (systemRootState == kSystemRootStateDisable) {
            return "当前手机是否已经root ? 未root";
        }
        File f = null;
        final String kSuSearchPaths[] = {"/system/bin/", "/system/xbin/", "/system/sbin/", "/sbin/", "/vendor/bin/"};
        try {
            for (int i = 0; i < kSuSearchPaths.length; i++) {
                String kSuSearchPath = kSuSearchPaths[i];
                f = new File(kSuSearchPath + "su");
                if (f != null && f.exists()) {
                    systemRootState = kSystemRootStateEnable;
                    return "当前手机是否已经root ? 已经root";
                }
            }
        } catch (Exception e) {
            Log.e("PublicUtil", "e:" + e);
        }
        systemRootState = kSystemRootStateDisable;
        return "当前手机是否已经root ? 未root";
    }

    /**
     * 获取屏幕宽高(像素)密度
     *
     * @return String
     */
    public static String getScreenInPixels(Activity activity) {
        DisplayMetrics metric = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels;     // 屏幕宽度（像素）
        int height = metric.heightPixels;   // 屏幕高度（像素）
        float density = metric.density;      // 屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = metric.densityDpi;  // 屏幕密度DPI（120 / 160 / 240）
        return "屏幕宽度-" + width + "\n屏幕高度-" + height + "\n屏幕密度-" + density + "\n屏幕密度DPI-" + densityDpi;
    }

    /**
     * 判断当前手机是否联网
     *
     * @return boolean
     */
    public static boolean isNetWorkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isWifiOK = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
        LogUtils.i("isWifiOK -->" + isWifiOK);
        boolean isInternetOK = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
        LogUtils.i("isInternetOK -->" + isInternetOK);
        return isWifiOK || isInternetOK;
    }

    /**
     * 调用系统浏览器打开网址
     *
     * @param context
     * @param uri     全路径网址
     */
    public static void openSystemBrowser(Context context, String uri) {
        Intent it = new Intent("android.intent.action.VIEW", Uri.parse(uri));
        context.startActivity(it);
    }

    /**
     * 递归删除目录及目录下的文件
     *
     * @param file 待删除的 file
     */
    public static void recursionDelFile(File file) {
        if (file == null) {
            return;
        }
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFile = file.listFiles();
            if (null == childFile || childFile.length == 0) {
                file.delete();
                return;
            }
            for (File f : childFile) {
                recursionDelFile(f);
            }
            file.delete();
        }
    }

    /**
     * 获取文件夹大小
     *
     * @param file
     * @return
     */
    public static long getFolderSize(File file) {
        if (null == file) {
            LogUtils.e("file is null");
            return 0;
        }
        long size = 0;
        File[] files = file.listFiles();
        if (null == files || files.length == 0) {
            return 0;
        }
        for (int i = 0; i < files.length; i++) {
            if (files[i].isDirectory()) {
                size += getFolderSize(files[i]);
            } else {
                size += files[i].length();
            }
        }
        return size;
    }

    /**
     * 保存信息到本地文件
     *
     * @param context
     * @param fileName
     * @param content  文件内容
     * @param saveMode 文件读写模式 {@link Context#MODE_PRIVATE},{@link Context#MODE_APPEND}
     */
    public static void saveInfo2File(Context context, String fileName, String content, int saveMode) {
        FileOutputStream fileOutputStream;
        BufferedWriter bufferedWriter = null;
        try {
            fileOutputStream = context.openFileOutput(fileName, saveMode);
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(fileOutputStream));
            bufferedWriter.write(content);
        } catch (IOException e) {
            LogUtils.e(e.toString());
        } finally {
            if (null != bufferedWriter) {
                try {
                    bufferedWriter.close();
                    LogUtils.i("保存信息成功");
                } catch (IOException e) {
                    LogUtils.e(e.toString());
                }
            }
        }
    }

    /**
     * 从文件中读取信息
     *
     * @param context
     * @param fileName 文件名称
     * @return 文件内容
     */
    public static String readInfoFromFile(Context context, String fileName) {
        FileInputStream fileInputStream;
        BufferedReader bufferedReader = null;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            fileInputStream = context.openFileInput(fileName);
            bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            String line;
            while (null != (line = bufferedReader.readLine())) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            LogUtils.e(e.toString());
            return "读取信息失败";
        } finally {
            if (null != bufferedReader) {
                try {
                    bufferedReader.close();
                    LogUtils.i("读取信息成功");
                } catch (IOException e) {
                    LogUtils.e(e.toString());
                    return "读取信息失败";
                }
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 判断 url 是否合法
     *
     * @param url
     * @return
     */
    public static boolean isUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        String regEx = "^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(url);
        return matcher.matches();
    }

    /**
     * 按照 yyyy-MM-dd HH:mm:ss 格式 获取系统当前时间
     *
     * @return
     */
    public static String getCurrentTime() {
        long time = System.currentTimeMillis();
        Date date = new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    /**
     * 获取渠道名
     *
     * @param context 此处习惯性的设置为activity，实际上context就可以
     * @return 如果没有获取成功，那么返回值为空
     */
    public static String getChannelName(Context context) {
        if (context == null) {
            return null;
        }
        String channelName = null;
        try {
            PackageManager packageManager = context.getPackageManager();
            if (packageManager != null) {
                //注意此处为ApplicationInfo 而不是 ActivityInfo,因为友盟设置的meta-data是在application标签中，而不是某activity标签中，所以用ApplicationInfo
                ApplicationInfo applicationInfo = packageManager.
                        getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        channelName = String.valueOf(applicationInfo.metaData.get("Demo_CHANNEL"));
                    }
                }

            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return channelName;
    }
}
