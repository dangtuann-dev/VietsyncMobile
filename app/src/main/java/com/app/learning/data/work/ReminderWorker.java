package com.app.learning.data.work;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.app.learning.data.notification.NotificationBuilder;

public class ReminderWorker extends Worker {

    public ReminderWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        NotificationBuilder.showNotification(
                getApplicationContext(),
                NotificationBuilder.CHANNEL_COURSE,
                "Nhắc Nhở Học Tập 📚",
                "Đã tới giờ học bài hôm nay rồi! Hãy duy trì chuỗi học tập nhé.",
                null
        );
        return Result.success();
    }
}
