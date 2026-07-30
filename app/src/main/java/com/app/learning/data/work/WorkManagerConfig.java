package com.app.learning.data.work;

import android.content.Context;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

public class WorkManagerConfig {

    public static void setupBackgroundJobs(Context context) {
        WorkManager wm = WorkManager.getInstance(context);

        // 1. Sync progress periodic work every 15 minutes when connected
        Constraints syncConstraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest syncWork = new PeriodicWorkRequest.Builder(SyncProgressWorker.class, 15, TimeUnit.MINUTES)
                .setConstraints(syncConstraints)
                .build();

        wm.enqueue(syncWork);

        // 2. Cache cleanup weekly periodic work
        PeriodicWorkRequest cleanupWork = new PeriodicWorkRequest.Builder(CacheCleanupWorker.class, 7, TimeUnit.DAYS)
                .build();

        wm.enqueue(cleanupWork);
    }

    public static void scheduleLessonDownload(Context context, String lessonTitle, String downloadUrl) {
        WorkManager wm = WorkManager.getInstance(context);

        Data data = new Data.Builder()
                .putString("lesson_title", lessonTitle)
                .putString("download_url", downloadUrl)
                .build();

        Constraints downloadConstraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest downloadWork = new OneTimeWorkRequest.Builder(LessonDownloadWorker.class)
                .setInputData(data)
                .setConstraints(downloadConstraints)
                .build();

        wm.enqueue(downloadWork);
    }
}
