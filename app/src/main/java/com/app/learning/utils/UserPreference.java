package com.app.learning.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.learning.data.model.User;

/**
 * UserPreference is a wrapper around SharedPreferences to manage the local storage
 * of user sessions, access tokens, and user profiles.
 */
public class UserPreference {

    private static volatile UserPreference instance;
    private final SharedPreferences preferences;

    private UserPreference(@NonNull Context context) {
        this.preferences = context.getApplicationContext().getSharedPreferences(
                AppConstants.PREF_NAME,
                Context.MODE_PRIVATE
        );
    }

    public static UserPreference getInstance(@NonNull Context context) {
        if (instance == null) {
            synchronized (UserPreference.class) {
                if (instance == null) {
                    instance = new UserPreference(context);
                }
            }
        }
        return instance;
    }

    /**
     * Saves user session data including JWT and user profile.
     *
     * @param token Access token (JWT)
     * @param user  User object containing profile information
     */
    public void saveSession(@NonNull String token, @NonNull User user) {
        preferences.edit()
                .putString(AppConstants.PREF_KEY_ACCESS_TOKEN, token)
                .putBoolean(AppConstants.PREF_KEY_IS_LOGGED_IN, true)
                .putString(AppConstants.PREF_KEY_USER_ID, user.getId())
                .putString(AppConstants.PREF_KEY_USER_EMAIL, user.getEmail())
                .putString(AppConstants.PREF_KEY_USER_NAME, user.getFullName())
                .putString(AppConstants.PREF_KEY_USER_ROLE, user.getRole())
                .putString("key_user_avatar", user.getAvatarUrl())
                .putString("key_user_bio", user.getBio())
                .apply();
    }

    /**
     * Retrieves the stored access token.
     *
     * @return Stored token, or null if not available
     */
    @Nullable
    public String getAccessToken() {
        return preferences.getString(AppConstants.PREF_KEY_ACCESS_TOKEN, null);
    }

    /**
     * Checks if a user is currently logged in.
     *
     * @return True if a session exists, false otherwise
     */
    public boolean isLoggedIn() {
        return preferences.getBoolean(AppConstants.PREF_KEY_IS_LOGGED_IN, false);
    }

    /**
     * Reconstructs the User object from stored SharedPreferences keys.
     *
     * @return User object with profile metadata, or null if session is empty
     */
    @Nullable
    public User getUserProfile() {
        if (!isLoggedIn()) {
            return null;
        }
        User user = new User();
        user.setId(preferences.getString(AppConstants.PREF_KEY_USER_ID, ""));
        user.setEmail(preferences.getString(AppConstants.PREF_KEY_USER_EMAIL, ""));
        user.setFullName(preferences.getString(AppConstants.PREF_KEY_USER_NAME, ""));
        user.setAvatarUrl(preferences.getString("key_user_avatar", ""));
        user.setBio(preferences.getString("key_user_bio", ""));
        user.setRole(preferences.getString(AppConstants.PREF_KEY_USER_ROLE, "student"));
        return user;
    }

    /**
     * Updates locally cached user profile fields.
     */
    public void updateUserProfile(@NonNull User user) {
        preferences.edit()
                .putString(AppConstants.PREF_KEY_USER_NAME, user.getFullName())
                .putString(AppConstants.PREF_KEY_USER_ROLE, user.getRole())
                .putString("key_user_avatar", user.getAvatarUrl())
                .putString("key_user_bio", user.getBio())
                .apply();
    }

    /**
     * Clears all session keys (used for sign-out).
     */
    public void clearSession() {
        preferences.edit()
                .remove(AppConstants.PREF_KEY_ACCESS_TOKEN)
                .remove(AppConstants.PREF_KEY_IS_LOGGED_IN)
                .remove(AppConstants.PREF_KEY_USER_ID)
                .remove(AppConstants.PREF_KEY_USER_EMAIL)
                .remove(AppConstants.PREF_KEY_USER_NAME)
                .remove(AppConstants.PREF_KEY_USER_ROLE)
                .remove("key_user_avatar")
                .remove("key_user_bio")
                .apply();
    }

    /**
     * Settings configurations.
     */
    public boolean isNotificationsEnabled() {
        return preferences.getBoolean("key_notifications_enabled", true);
    }

    public void setNotificationsEnabled(boolean enabled) {
        preferences.edit().putBoolean("key_notifications_enabled", enabled).apply();
    }

    public boolean isDarkModeEnabled() {
        return preferences.getBoolean("key_dark_mode_enabled", false);
    }

    public void setDarkModeEnabled(boolean enabled) {
        preferences.edit().putBoolean("key_dark_mode_enabled", enabled).apply();
    }

    public String getAppLanguage() {
        return preferences.getString("key_app_language", "vi");
    }

    public void setAppLanguage(String language) {
        preferences.edit().putString("key_app_language", language).apply();
    }
}
