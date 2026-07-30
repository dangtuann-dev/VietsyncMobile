package com.app.learning.data.work;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class SyncProgressWorker extends Worker {

    public SyncProgressWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            // Upload pending progress offline data to Supabase
            Thread.sleep(1000);
            return Result.success();
        } catch (Exception e) {
            return Result.retry();
        }
    }
}
