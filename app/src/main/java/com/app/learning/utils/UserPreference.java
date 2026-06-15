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
        // Role key can be accessed here
        user.setRole("student"); // Default fallback
        return user;
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
                .apply();
    }
}
