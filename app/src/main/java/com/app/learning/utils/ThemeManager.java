package com.app.learning.utils;

import android.content.Context;
import androidx.appcompat.app.AppCompatDelegate;

public class ThemeManager {

    public static final int THEME_LIGHT = AppCompatDelegate.MODE_NIGHT_NO;
    public static final int THEME_DARK = AppCompatDelegate.MODE_NIGHT_YES;
    public static final int THEME_SYSTEM = AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;

    private static ThemeManager instance;
    private final Context context;

    private ThemeManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public static synchronized ThemeManager getInstance(Context context) {
        if (instance == null) {
            instance = new ThemeManager(context);
        }
        return instance;
    }

    public void setThemeMode(int mode) {
        UserPreference userPref = UserPreference.getInstance(context);
        boolean isDark = (mode == THEME_DARK);
        userPref.setDarkModeEnabled(isDark);
        AppCompatDelegate.setDefaultNightMode(mode);
    }

    public int getThemeMode() {
        boolean isDark = UserPreference.getInstance(context).isDarkModeEnabled();
        return isDark ? THEME_DARK : THEME_LIGHT;
    }

    public void applyTheme() {
        int mode = getThemeMode();
        AppCompatDelegate.setDefaultNightMode(mode);
    }
}
