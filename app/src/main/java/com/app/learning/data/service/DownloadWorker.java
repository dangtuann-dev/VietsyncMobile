package com.app.learning.data.service;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.app.learning.data.local.AppDatabase;
import com.app.learning.data.local.DownloadDao;
import com.app.learning.data.local.DownloadEntity;
import com.app.learning.utils.StorageManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadWorker extends Worker {

    public static final String KEY_LESSON_ID = "key_lesson_id";
    public static final String KEY_COURSE_ID = "key_course_id";
    public static final String KEY_TITLE = "key_title";
    public static final String KEY_URL = "key_url";

    private final DownloadDao downloadDao;

    public DownloadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.downloadDao = AppDatabase.getInstance(context).downloadDao();
    }

    @NonNull
    @Override
    public Result doWork() {
        String lessonId = getInputData().getString(KEY_LESSON_ID);
        String courseId = getInputData().getString(KEY_COURSE_ID);
        String title = getInputData().getString(KEY_TITLE);
        String downloadUrl = getInputData().getString(KEY_URL);

        if (lessonId == null || downloadUrl == null) return Result.failure();

        File targetDir = StorageManager.getDownloadDirectory(getApplicationContext());
        File localFile = new File(targetDir, "lesson_" + lessonId + ".mp4");

        DownloadEntity entity = new DownloadEntity(lessonId, courseId, title, downloadUrl, localFile.getAbsolutePath(), "DOWNLOADING", 0, 0, 0, System.currentTimeMillis());
        downloadDao.insertOrUpdate(entity);

        try {
            URL url = new URL(downloadUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                entity.setStatus("FAILED");
                downloadDao.update(entity);
                return Result.retry();
            }

            long totalBytes = connection.getContentLengthLong();
            entity.setTotalBytes(totalBytes);

            InputStream input = connection.getInputStream();
            FileOutputStream output = new FileOutputStream(localFile);

            byte[] buffer = new byte[8192];
            long downloadedBytes = 0;
            int count;
            long lastUpdate = System.currentTimeMillis();

            while ((count = input.read(buffer)) != -1) {
                if (isStopped()) {
                    entity.setStatus("PAUSED");
                    downloadDao.update(entity);
                    output.close();
                    input.close();
                    return Result.failure();
                }

                downloadedBytes += count;
                output.write(buffer, 0, count);

                long now = System.currentTimeMillis();
                if (now - lastUpdate > 500) {
                    lastUpdate = now;
                    int progress = totalBytes > 0 ? (int) (downloadedBytes * 100 / totalBytes) : 0;
                    entity.setProgress(progress);
                    entity.setDownloadedBytes(downloadedBytes);
                    downloadDao.update(entity);
                }
            }

            output.flush();
            output.close();
            input.close();

            entity.setStatus("COMPLETED");
            entity.setProgress(100);
            entity.setDownloadedBytes(downloadedBytes);
            downloadDao.update(entity);

            return Result.success();

        } catch (Exception e) {
            entity.setStatus("FAILED");
            downloadDao.update(entity);
            return Result.retry();
        }
    }
}
