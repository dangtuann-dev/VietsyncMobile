package com.app.learning.utils;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;

public class StorageManager {

    public static long getAvailableStorageBytes(Context context) {
        File path = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES);
        if (path == null) path = context.getFilesDir();
        StatFs stat = new StatFs(path.getPath());
        return stat.getAvailableBlocksLong() * stat.getBlockSizeLong();
    }

    public static boolean hasEnoughSpace(Context context, long requiredBytes) {
        return getAvailableStorageBytes(context) > requiredBytes;
    }

    public static File getDownloadDirectory(Context context) {
        File dir = new File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES), "offline_lessons");
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    public static boolean deleteLocalFile(String filePath) {
        if (filePath == null || filePath.isEmpty()) return false;
        File file = new File(filePath);
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }
}
