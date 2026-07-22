package com.app.learning.data.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import com.app.learning.data.repository.ProgressRepository;

public class ProgressSyncService extends IntentService {

    private static final String TAG = "ProgressSyncService";
    private static final long SYNC_INTERVAL_MS = 30000; // 30 seconds
    private Handler handler;
    private Runnable syncRunnable;
    private ProgressRepository repository;
    private boolean isLearning = false;

    public ProgressSyncService() {
        super("ProgressSyncService");
        repository = new ProgressRepository();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler(Looper.getMainLooper());
        syncRunnable = new Runnable() {
            @Override
            public void run() {
                if (isLearning) {
                    repository.updateProgressToSupabase();
                    handler.postDelayed(this, SYNC_INTERVAL_MS);
                }
            }
        };
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String action = intent.getAction();
            if ("ACTION_START_LEARNING".equals(action)) {
                Log.d(TAG, "Learning started. Beginning sync...");
                isLearning = true;
                handler.post(syncRunnable);
            } else if ("ACTION_STOP_LEARNING".equals(action)) {
                Log.d(TAG, "Learning stopped. Halting sync...");
                isLearning = false;
                handler.removeCallbacks(syncRunnable);
            }
        }
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        isLearning = false;
        if (handler != null) {
            handler.removeCallbacks(syncRunnable);
        }
    }
}
