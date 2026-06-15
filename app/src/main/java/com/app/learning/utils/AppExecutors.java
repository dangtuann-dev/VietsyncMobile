package com.app.learning.utils;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * AppExecutors maintains thread pools globally to organize asynchronous background
 * executions (disk database storage, network requests) and handle callback transitions
 * back to the main UI thread.
 */
public class AppExecutors {

    private static volatile AppExecutors instance;

    private final Executor diskIO;
    private final Executor networkIO;
    private final Executor mainThread;

    private AppExecutors(Executor diskIO, Executor networkIO, Executor mainThread) {
        this.diskIO = diskIO;
        this.networkIO = networkIO;
        this.mainThread = mainThread;
    }

    public static AppExecutors getInstance() {
        if (instance == null) {
            synchronized (AppExecutors.class) {
                if (instance == null) {
                    instance = new AppExecutors(
                            Executors.newSingleThreadExecutor(),
                            Executors.newFixedThreadPool(4),
                            new MainThreadExecutor()
                    );
                }
            }
        }
        return instance;
    }

    /**
     * Single-thread executor for local database (Room/SQLite) read-write operations.
     */
    public Executor diskIO() {
        return diskIO;
    }

    /**
     * Fixed-size thread pool for remote REST API network queries.
     */
    public Executor networkIO() {
        return networkIO;
    }

    /**
     * Handler-delegated executor that posts Runnable items onto the Android Main Looper.
     */
    public Executor mainThread() {
        return mainThread;
    }

    private static class MainThreadExecutor implements Executor {
        private final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}
