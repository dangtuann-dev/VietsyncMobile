package com.app.learning.data.work;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.File;

public class CacheCleanupWorker extends Worker {

    public CacheCleanupWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            File cacheDir = getApplicationContext().getCacheDir();
            if (cacheDir != null && cacheDir.exists()) {
                deleteOldFiles(cacheDir, 30 * 24 * 60 * 60 * 1000L); // > 30 days
            }
            return Result.success();
        } catch (Exception e) {
            return Result.failure();
        }
    }

    private void deleteOldFiles(File dir, long maxAgeMs) {
        File[] files = dir.listFiles();
        if (files == null) return;
        long now = System.currentTimeMillis();
        for (File f : files) {
            if (f.isDirectory()) {
                deleteOldFiles(f, maxAgeMs);
            } else if (now - f.lastModified() > maxAgeMs) {
                f.delete();
            }
        }
    }
}
