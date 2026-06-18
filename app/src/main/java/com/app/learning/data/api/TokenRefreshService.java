package com.app.learning.data.api;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.app.learning.data.model.AuthResponse;
import com.app.learning.data.repository.AuthRepository;
import com.app.learning.utils.SessionManager;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * TokenRefreshService runs in the background to periodically evaluate if the stored
 * Supabase JWT token is close to expiration, and triggers a refresh using the
 * refresh token if necessary.
 */
public class TokenRefreshService extends Service {

    private static final String TAG = "TokenRefreshService";
    
    private ScheduledExecutorService scheduler;
    private AuthRepository authRepository;
    private SessionManager sessionManager;
    private boolean isRunning = false;

    @Override
    public void onCreate() {
        super.onCreate();
        this.authRepository = new AuthRepository(this);
        this.sessionManager = SessionManager.getInstance(this);
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        Log.d(TAG, "TokenRefreshService created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!isRunning) {
            isRunning = true;
            startPeriodicCheck();
            Log.d(TAG, "TokenRefreshService started and running periodic checks");
        }
        return START_STICKY;
    }

    /**
     * Schedules periodic token evaluation every 60 seconds.
     */
    private void startPeriodicCheck() {
        scheduler.scheduleWithFixedDelay(
                this::checkAndRefreshToken,
                10, // Initial delay
                60, // Period
                TimeUnit.SECONDS
        );
    }

    /**
     * Core worker routine to evaluate session and refresh token synchronously.
     */
    private void checkAndRefreshToken() {
        try {
            if (!sessionManager.isLoggedIn()) {
                Log.v(TAG, "No active session to verify");
                return;
            }

            if (sessionManager.isTokenExpired()) {
                Log.i(TAG, "Access token is close to expiry or already expired. Initiating refresh...");
                AuthResponse response = authRepository.refreshTokenSync();
                if (response != null) {
                    Log.i(TAG, "Successfully refreshed JWT token in background");
                } else {
                    Log.w(TAG, "Refresh session returned null response");
                }
            } else {
                long secondsLeft = (sessionManager.getExpiresAt() - System.currentTimeMillis()) / 1000;
                Log.d(TAG, "Token is active. Seconds until expiry evaluation: " + (secondsLeft - 300));
            }
        } catch (Exception e) {
            Log.e(TAG, "Background exception occurred during token refresh check: " + e.getMessage(), e);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null; // Do not support binding
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (scheduler != null) {
            scheduler.shutdownNow();
            scheduler = null;
        }
        isRunning = false;
        Log.d(TAG, "TokenRefreshService destroyed");
    }
}
