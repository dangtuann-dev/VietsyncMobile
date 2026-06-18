package com.app.learning.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.app.learning.data.model.AuthResponse;
import com.app.learning.data.model.UserModel;
import com.google.gson.Gson;

/**
 * SessionManager securely stores the Supabase user sessions (JWT access token,
 * refresh token, expiry details, and profile data) using EncryptedSharedPreferences.
 */
public class SessionManager {

    private static final String TAG = "SessionManager";
    private static final String PREF_NAME = "vietsync_secure_session";
    private static final String KEY_ACCESS_TOKEN = "access_token";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";
    private static final String KEY_EXPIRES_AT = "expires_at";
    private static final String KEY_USER_DATA = "user_data";

    private static volatile SessionManager instance;
    private SharedPreferences prefs;
    private final Gson gson;

    private SessionManager(@NonNull Context context) {
        this.gson = new Gson();
        try {
            // Generate or load the master key for encrypting SharedPreferences
            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);

            // Create the EncryptedSharedPreferences instance
            this.prefs = EncryptedSharedPreferences.create(
                    PREF_NAME,
                    masterKeyAlias,
                    context.getApplicationContext(),
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
            Log.d(TAG, "EncryptedSharedPreferences initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Failed to initialize EncryptedSharedPreferences. Falling back to standard SharedPreferences.", e);
            // Fallback for safety (e.g. on devices lacking keystore support)
            this.prefs = context.getApplicationContext().getSharedPreferences(
                    PREF_NAME + "_fallback",
                    Context.MODE_PRIVATE
            );
        }
    }

    /**
     * Singleton accessor for SessionManager.
     */
    public static SessionManager getInstance(@NonNull Context context) {
        if (instance == null) {
            synchronized (SessionManager.class) {
                if (instance == null) {
                    instance = new SessionManager(context);
                }
            }
        }
        return instance;
    }

    /**
     * Saves session payload securely.
     *
     * @param response The auth response containing tokens and user data.
     */
    public synchronized void saveSession(@NonNull AuthResponse response) {
        if (response.getAccessToken() == null) {
            return;
        }

        // Calculate absolute expiry epoch millisecond timestamp
        long expiresAt = System.currentTimeMillis() + (response.getExpiresIn() * 1000);

        SharedPreferences.Editor editor = prefs.edit()
                .putString(KEY_ACCESS_TOKEN, response.getAccessToken())
                .putLong(KEY_EXPIRES_AT, expiresAt);

        if (response.getRefreshToken() != null) {
            editor.putString(KEY_REFRESH_TOKEN, response.getRefreshToken());
        }

        if (response.getUser() != null) {
            String userJson = gson.toJson(response.getUser());
            editor.putString(KEY_USER_DATA, userJson);
        }

        editor.apply();
        Log.d(TAG, "Session saved successfully. Expiry in: " + response.getExpiresIn() + " seconds.");
    }

    /**
     * Retrieves the encrypted access token (JWT).
     */
    @Nullable
    public synchronized String getAccessToken() {
        return prefs.getString(KEY_ACCESS_TOKEN, null);
    }

    /**
     * Retrieves the encrypted refresh token.
     */
    @Nullable
    public synchronized String getRefreshToken() {
        return prefs.getString(KEY_REFRESH_TOKEN, null);
    }

    /**
     * Retrieves the epoch timestamp in milliseconds when the access token expires.
     */
    public synchronized long getExpiresAt() {
        return prefs.getLong(KEY_EXPIRES_AT, 0);
    }

    /**
     * Checks if the current access token is expired or close to expiry (e.g., within 5 minutes).
     */
    public synchronized boolean isTokenExpired() {
        long expiresAt = getExpiresAt();
        if (expiresAt == 0) {
            return true;
        }
        // Refresh 5 minutes before actual expiration to handle latency
        long bufferTimeMillis = 5 * 60 * 1000;
        return System.currentTimeMillis() >= (expiresAt - bufferTimeMillis);
    }

    /**
     * Retrieves the saved User profile object.
     */
    @Nullable
    public synchronized UserModel getUser() {
        String userJson = prefs.getString(KEY_USER_DATA, null);
        if (userJson == null) {
            return null;
        }
        try {
            return gson.fromJson(userJson, UserModel.class);
        } catch (Exception e) {
            Log.e(TAG, "Error deserializing user data", e);
            return null;
        }
    }

    /**
     * Updates the local User profile data.
     */
    public synchronized void updateUser(@NonNull UserModel user) {
        String userJson = gson.toJson(user);
        prefs.edit().putString(KEY_USER_DATA, userJson).apply();
    }

    /**
     * Checks if a valid user session is stored locally.
     */
    public synchronized boolean isLoggedIn() {
        return getAccessToken() != null;
    }

    /**
     * Wipes all keys from the encrypted SharedPreferences (Sign-out).
     */
    public synchronized void clearSession() {
        prefs.edit()
                .remove(KEY_ACCESS_TOKEN)
                .remove(KEY_REFRESH_TOKEN)
                .remove(KEY_EXPIRES_AT)
                .remove(KEY_USER_DATA)
                .apply();
        Log.d(TAG, "Session cleared successfully");
    }
}
