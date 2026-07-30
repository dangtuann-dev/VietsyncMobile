package com.app.learning.utils;

import android.content.Context;
import com.app.learning.data.work.WorkManagerConfig;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AppInitializer {

    private static final ExecutorService initExecutor = Executors.newSingleThreadExecutor();

    public static void initializeAsync(Context context) {
        initExecutor.execute(() -> {
            try {
                // Initialize background jobs & heavy components off main UI thread
                WorkManagerConfig.setupBackgroundJobs(context);
                StrictModeHelper.init();
            } catch (Exception ignored) {}
        });
    }
}
