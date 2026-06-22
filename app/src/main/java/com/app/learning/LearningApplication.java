package com.app.learning;

import android.app.Application;
import com.app.learning.data.api.ApiClient;
import com.example.vietsyncmobile.BuildConfig;

/**
 * LearningApplication serves as the entry point for initialization task configurations.
 */
public class LearningApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        
        // Initialize the ApiClient singleton with the application context and the Supabase key from BuildConfig
        ApiClient.initialize(this, BuildConfig.SUPABASE_ANON_KEY);

        // Apply dark mode theme if configured
        boolean isDarkMode = com.app.learning.utils.UserPreference.getInstance(this).isDarkModeEnabled();
        androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(
                isDarkMode ? androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES : androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
        );

        // Apply app language if configured
        String lang = com.app.learning.utils.UserPreference.getInstance(this).getAppLanguage();
        androidx.core.os.LocaleListCompat appLocale = androidx.core.os.LocaleListCompat.forLanguageTags(lang);
        androidx.appcompat.app.AppCompatDelegate.setApplicationLocales(appLocale);
    }
}
