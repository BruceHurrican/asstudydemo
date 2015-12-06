/*
 * Copyright (c) 2015.
 *   This document is just for Bruce's personal study.
 *   Some resources come from the Internet. Everyone can download and use it for study, but can
 *   not be used for commercial purpose. The author does not bear the
 *   corresponding disputes arising therefrom.
 *   Please delete within 24 hours after download.
 *   If you have good suggestions for this code, you can contact BurrceHurrican@foxmail.com.
 *   本文件为Bruce's个人学习android的demo, 其中所用到的代码来源于互联网，仅作为学习交流使用。
 *   任和何人可以下载并使用, 但是不能用于商业用途。
 *   作者不承担由此带来的相应纠纷。
 *   如果对本代码有好的建议，可以联系BurrceHurrican@foxmail.com
 */

package com.study.bruce.demo.utils;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.File;

/**
 * 获取手机内部存储空间和外部存储空间
 * Created by BruceHurrican on 2015/7/11.
 */
public final class StorageUtil {
    private static final int ERROR = -1;
    public static final String TAG = "StorageUtil";

    private StorageUtil() {
    }

    /**
     * SDCARD是否存在
     */
    public static boolean isSDcardExists() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取手机内部剩余存储空间
     *
     * @return
     */
    public static long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        Log.d(TAG, "getAvailableInternalMemorySize path:" + path);
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        //getAvailableBlocks()已经被getAvailableBlocksLong()代替
        return availableBlocks * blockSize;
    }

    /**
     * 获取手机内部总的存储空间
     *
     * @return
     */
    public static long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        Log.d(TAG, "getTotalInternalMemorySize path:" + path);
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long totalBlocks = stat.getBlockCount();
        return totalBlocks * blockSize;
    }

    /**
     * 获取SDCARD剩余存储空间
     *
     * @return
     */
    public static long getAvailableExternalMemorySize() {
        if (isSDcardExists()) {
            File path = Environment.getExternalStorageDirectory();
            Log.d(TAG, "getAvailableExternalMemorySize path:" + path);
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            return availableBlocks * blockSize;
        } else {
            Log.d(TAG, "getAvailableExternalMemorySize no permission");
            return ERROR;
        }
    }

    /**
     * 获取SDCARD总的存储空间
     *
     * @return
     */
    public static long getTotalExternalMemorySize() {
        if (isSDcardExists()) {
            File path = Environment.getExternalStorageDirectory();
            Log.d(TAG, "getExternalStorageDirectory path:" + path);
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            //getBlockSize()已经被getBlockSizeLong()代替
            long totalBlocks = stat.getBlockCount();
            //getBlockCount()已经被getBlockCountLong()代替
            return totalBlocks * blockSize;
        } else {
            Log.d(TAG, "getExternalStorageDirectory no permission");
            return ERROR;
        }
    }

}
