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

            String masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC);


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

            this.prefs = context.getApplicationContext().getSharedPreferences(
                    PREF_NAME + "_fallback",
                    Context.MODE_PRIVATE
            );
        }
    }




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






    public synchronized void saveSession(@NonNull AuthResponse response) {
        if (response.getAccessToken() == null) {
            return;
        }


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




    @Nullable
    public synchronized String getAccessToken() {
        return prefs.getString(KEY_ACCESS_TOKEN, null);
    }




    @Nullable
    public synchronized String getRefreshToken() {
        return prefs.getString(KEY_REFRESH_TOKEN, null);
    }




    public synchronized long getExpiresAt() {
        return prefs.getLong(KEY_EXPIRES_AT, 0);
    }




    public synchronized boolean isTokenExpired() {
        long expiresAt = getExpiresAt();
        if (expiresAt == 0) {
            return true;
        }

        long bufferTimeMillis = 5 * 60 * 1000;
        return System.currentTimeMillis() >= (expiresAt - bufferTimeMillis);
    }




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




    public synchronized void updateUser(@NonNull UserModel user) {
        String userJson = gson.toJson(user);
        prefs.edit().putString(KEY_USER_DATA, userJson).apply();
    }




    public synchronized boolean isLoggedIn() {
        return getAccessToken() != null;
    }




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
