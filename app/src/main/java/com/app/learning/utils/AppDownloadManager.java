package com.app.learning.utils;

import android.content.Context;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.app.learning.data.local.AppDatabase;
import com.app.learning.data.local.DownloadDao;
import com.app.learning.data.local.DownloadEntity;
import com.app.learning.data.service.DownloadWorker;

public class AppDownloadManager {

    private final Context context;
    private final DownloadDao downloadDao;

    public AppDownloadManager(Context context) {
        this.context = context.getApplicationContext();
        this.downloadDao = AppDatabase.getInstance(context).downloadDao();
    }

    public void enqueueDownload(String lessonId, String courseId, String title, String url, boolean wifiOnly) {
        Data inputData = new Data.Builder()
                .putString(DownloadWorker.KEY_LESSON_ID, lessonId)
                .putString(DownloadWorker.KEY_COURSE_ID, courseId)
                .putString(DownloadWorker.KEY_TITLE, title)
                .putString(DownloadWorker.KEY_URL, url)
                .build();

        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(wifiOnly ? NetworkType.UNMETERED : NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest downloadWork = new OneTimeWorkRequest.Builder(DownloadWorker.class)
                .setConstraints(constraints)
                .setInputData(inputData)
                .addTag(lessonId)
                .build();

        WorkManager.getInstance(context).enqueue(downloadWork);
    }

    public void cancelDownload(String lessonId) {
        WorkManager.getInstance(context).cancelAllWorkByTag(lessonId);
        AppExecutors.getInstance().diskIO().execute(() -> {
            DownloadEntity entity = downloadDao.getDownloadByLessonIdSync(lessonId);
            if (entity != null) {
                StorageManager.deleteLocalFile(entity.getLocalPath());
                downloadDao.deleteByLessonId(lessonId);
            }
        });
    }
}
