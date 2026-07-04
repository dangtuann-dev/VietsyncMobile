package com.app.learning.data.local;

import android.content.Context;

import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.PreferenceDataStore;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public class AppSettingsManager {
    private static final String DATASTORE_NAME = "app_settings";

    // Keys
    private static final Preferences.Key<String> KEY_APP_LANGUAGE = PreferencesKeys.stringKey("app_language");
    private static final Preferences.Key<Boolean> KEY_DARK_MODE = PreferencesKeys.booleanKey("dark_mode");
    private static final Preferences.Key<String> KEY_FONT_SIZE = PreferencesKeys.stringKey("font_size");

    private static AppSettingsManager instance;
    private final RxDataStore<Preferences> dataStore;

    private AppSettingsManager(Context context) {
        dataStore = new RxPreferenceDataStoreBuilder(context.getApplicationContext(), DATASTORE_NAME).build();
    }

    public static synchronized AppSettingsManager getInstance(Context context) {
        if (instance == null) {
            instance = new AppSettingsManager(context);
        }
        return instance;
    }

    // Language
    public Single<Preferences> setAppLanguage(String language) {
        return dataStore.updateDataAsync(prefsIn -> {
            MutablePreferences mutablePreferences = prefsIn.toMutablePreferences();
            mutablePreferences.set(KEY_APP_LANGUAGE, language);
            return Single.just(mutablePreferences);
        });
    }

    public Flowable<String> getAppLanguage() {
        return dataStore.data().map(prefs -> {
            String language = prefs.get(KEY_APP_LANGUAGE);
            return language != null ? language : "vi"; // Default to Vietnamese
        });
    }

    // Dark Mode
    public Single<Preferences> setDarkModeEnabled(boolean isEnabled) {
        return dataStore.updateDataAsync(prefsIn -> {
            MutablePreferences mutablePreferences = prefsIn.toMutablePreferences();
            mutablePreferences.set(KEY_DARK_MODE, isEnabled);
            return Single.just(mutablePreferences);
        });
    }

    public Flowable<Boolean> isDarkModeEnabled() {
        return dataStore.data().map(prefs -> {
            Boolean isDarkMode = prefs.get(KEY_DARK_MODE);
            return isDarkMode != null ? isDarkMode : false; // Default to false
        });
    }

    // Font Size
    public Single<Preferences> setFontSize(String fontSize) {
        return dataStore.updateDataAsync(prefsIn -> {
            MutablePreferences mutablePreferences = prefsIn.toMutablePreferences();
            mutablePreferences.set(KEY_FONT_SIZE, fontSize);
            return Single.just(mutablePreferences);
        });
    }

    public Flowable<String> getFontSize() {
        return dataStore.data().map(prefs -> {
            String fontSize = prefs.get(KEY_FONT_SIZE);
            return fontSize != null ? fontSize : "medium"; // Default to medium
        });
    }
}
