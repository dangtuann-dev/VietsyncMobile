package com.app.learning.data.work;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.app.learning.data.notification.NotificationBuilder;

public class LessonDownloadWorker extends Worker {

    public LessonDownloadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String lessonTitle = getInputData().getString("lesson_title");
        if (lessonTitle == null) lessonTitle = "Bài học";

        try {
            // Simulate video/PDF downloading
            for (int progress = 20; progress <= 100; progress += 20) {
                Thread.sleep(500);
            }

            NotificationBuilder.showNotification(
                    getApplicationContext(),
                    NotificationBuilder.CHANNEL_GENERAL,
                    "Tải Bài Học Hoàn Tất",
                    "Đã tải thành công: " + lessonTitle,
                    null
            );

            return Result.success();
        } catch (Exception e) {
            return Result.failure();
        }
    }
}
