/*
 * Copyright (c) 2015.
 *   This document is Bruce's individual learning the android demo, wherein the use of the code from the Internet, only to use as a learning exchanges.
 *   And where any person can download and use, but not for commercial purposes.
 *   Author does not assume the resulting corresponding disputes.
 *   If you have good suggestions for the code, you can contact BurrceHurrican@foxmail.com
 *   本文件为Bruce's个人学习android的demo, 其中所用到的代码来源于互联网，仅作为学习交流使用。
 *   任和何人可以下载并使用, 但是不能用于商业用途。
 *   作者不承担由此带来的相应纠纷。
 *   如果对本代码有好的建议，可以联系BurrceHurrican@foxmail.com
 */

package com.study.bruce.demo.utils;

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
import android.util.DisplayMetrics;

import com.study.bruce.demo.log.Logs;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 工具类
 * Created by BruceHurrican on 2015/7/11.
 */
public final class PublicUtil {
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
                "\nSDK版本号:" + Build.VERSION.SDK_INT + "\n系统版本:" + Build.VERSION.RELEASE + "\n设备安卓版本:" + Build.VERSION
                .RELEASE + "\n设备驱动:" + Build.DEVICE + "\n显示:" + Build.DISPLAY + "\n品牌:" + Build.BRAND + "\n主板:" +
                Build.BOARD + "\n标识:" + Build.FINGERPRINT + "\nID:" + Build.ID + "\n制造商:" + Build
                .MANUFACTURER + "\n用户组:" + Build.USER + "\n序列号:" + Build.SERIAL + "\nandroid设备标识码:" + Settings.Secure
                .getString(context.getContentResolver(), Settings.Secure
                        .ANDROID_ID);
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

    private static final int kSystemRootStateUnknow = -1;
    private static final int kSystemRootStateDisable = 0;
    private static final int kSystemRootStateEnable = 1;
    private static int systemRootState = kSystemRootStateUnknow;

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
        final String kSuSearchPaths[] = {"/system/bin/", "/system/xbin/",
                "/system/sbin/", "/sbin/", "/vendor/bin/"};
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
            Logs.e("PublicUtil", "e:" + e);
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
    public static String isNetWorkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isWifiOK = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnectedOrConnecting();
        Logs.i("PublicUtil", "isWifiOK -->" + isWifiOK);
        boolean isInternetOK = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnectedOrConnecting();
        Logs.i("PublicUtil", "isInternetOK -->" + isInternetOK);
        return (isWifiOK || isInternetOK) ? "当前手机是否联网 ? 已经联网" : "当前手机是否联网 ? 未连接网络";
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
}
