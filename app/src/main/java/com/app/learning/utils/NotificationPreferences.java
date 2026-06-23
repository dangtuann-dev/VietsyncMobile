package com.app.learning.utils;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

/**
 * NotificationPreferences is a Jetpack DataStore Preferences wrapper replacing SharedPreferences
 * to store notification toggle preferences and Quiet Hours settings asynchronously.
 */
public class NotificationPreferences {

    private static volatile NotificationPreferences instance;
    private final RxDataStore<Preferences> dataStore;

    // Preference Keys
    public static final Preferences.Key<Boolean> KEY_NEW_COURSE = PreferencesKeys.booleanKey("new_course_announcements");
    public static final Preferences.Key<Boolean> KEY_ASSIGNMENT = PreferencesKeys.booleanKey("assignment_deadlines");
    public static final Preferences.Key<Boolean> KEY_DISCUSSION = PreferencesKeys.booleanKey("discussion_replies");
    public static final Preferences.Key<Boolean> KEY_WEEKLY_PROGRESS = PreferencesKeys.booleanKey("weekly_progress_report");
    public static final Preferences.Key<Boolean> KEY_PROMOTIONAL = PreferencesKeys.booleanKey("promotional_offers");
    public static final Preferences.Key<String> KEY_QUIET_HOURS_START = PreferencesKeys.stringKey("quiet_hours_start");
    public static final Preferences.Key<String> KEY_QUIET_HOURS_END = PreferencesKeys.stringKey("quiet_hours_end");
    public static final Preferences.Key<Boolean> KEY_QUIET_HOURS_ENABLED = PreferencesKeys.booleanKey("quiet_hours_enabled");

    private NotificationPreferences(@NonNull Context context) {
        this.dataStore = new RxPreferenceDataStoreBuilder(
                context.getApplicationContext(),
                "notification_settings"
        ).build();
    }

    public static NotificationPreferences getInstance(@NonNull Context context) {
        if (instance == null) {
            synchronized (NotificationPreferences.class) {
                if (instance == null) {
                    instance = new NotificationPreferences(context);
                }
            }
        }
        return instance;
    }

    public RxDataStore<Preferences> getDataStore() {
        return dataStore;
    }

    // --- GETTERS (Return Flowable to automatically observe changes) ---

    public Flowable<Boolean> getNewCourseAnnouncements(boolean defaultValue) {
        return dataStore.data().map(prefs -> {
            Boolean value = prefs.get(KEY_NEW_COURSE);
            return value != null ? value : defaultValue;
        });
    }

    public Flowable<Boolean> getAssignmentDeadlines(boolean defaultValue) {
        return dataStore.data().map(prefs -> {
            Boolean value = prefs.get(KEY_ASSIGNMENT);
            return value != null ? value : defaultValue;
        });
    }

    public Flowable<Boolean> getDiscussionReplies(boolean defaultValue) {
        return dataStore.data().map(prefs -> {
            Boolean value = prefs.get(KEY_DISCUSSION);
            return value != null ? value : defaultValue;
        });
    }

    public Flowable<Boolean> getWeeklyProgressReport(boolean defaultValue) {
        return dataStore.data().map(prefs -> {
            Boolean value = prefs.get(KEY_WEEKLY_PROGRESS);
            return value != null ? value : defaultValue;
        });
    }

    public Flowable<Boolean> getPromotionalOffers(boolean defaultValue) {
        return dataStore.data().map(prefs -> {
            Boolean value = prefs.get(KEY_PROMOTIONAL);
            return value != null ? value : defaultValue;
        });
    }

    public Flowable<String> getQuietHoursStart(String defaultValue) {
        return dataStore.data().map(prefs -> {
            String value = prefs.get(KEY_QUIET_HOURS_START);
            return value != null ? value : defaultValue;
        });
    }

    public Flowable<String> getQuietHoursEnd(String defaultValue) {
        return dataStore.data().map(prefs -> {
            String value = prefs.get(KEY_QUIET_HOURS_END);
            return value != null ? value : defaultValue;
        });
    }

    public Flowable<Boolean> getQuietHoursEnabled(boolean defaultValue) {
        return dataStore.data().map(prefs -> {
            Boolean value = prefs.get(KEY_QUIET_HOURS_ENABLED);
            return value != null ? value : defaultValue;
        });
    }

    // --- SETTERS (Return Single representing async write operations) ---

    public Single<Preferences> setNewCourseAnnouncements(boolean enabled) {
        return dataStore.updateDataAsync(prefs -> {
            MutablePreferences mutable = prefs.toMutablePreferences();
            mutable.set(KEY_NEW_COURSE, enabled);
            return Single.just(mutable);
        });
    }

    public Single<Preferences> setAssignmentDeadlines(boolean enabled) {
        return dataStore.updateDataAsync(prefs -> {
            MutablePreferences mutable = prefs.toMutablePreferences();
            mutable.set(KEY_ASSIGNMENT, enabled);
            return Single.just(mutable);
        });
    }

    public Single<Preferences> setDiscussionReplies(boolean enabled) {
        return dataStore.updateDataAsync(prefs -> {
            MutablePreferences mutable = prefs.toMutablePreferences();
            mutable.set(KEY_DISCUSSION, enabled);
            return Single.just(mutable);
        });
    }

    public Single<Preferences> setWeeklyProgressReport(boolean enabled) {
        return dataStore.updateDataAsync(prefs -> {
            MutablePreferences mutable = prefs.toMutablePreferences();
            mutable.set(KEY_WEEKLY_PROGRESS, enabled);
            return Single.just(mutable);
        });
    }

    public Single<Preferences> setPromotionalOffers(boolean enabled) {
        return dataStore.updateDataAsync(prefs -> {
            MutablePreferences mutable = prefs.toMutablePreferences();
            mutable.set(KEY_PROMOTIONAL, enabled);
            return Single.just(mutable);
        });
    }

    public Single<Preferences> setQuietHoursStart(String startTime) {
        return dataStore.updateDataAsync(prefs -> {
            MutablePreferences mutable = prefs.toMutablePreferences();
            mutable.set(KEY_QUIET_HOURS_START, startTime);
            return Single.just(mutable);
        });
    }

    public Single<Preferences> setQuietHoursEnd(String endTime) {
        return dataStore.updateDataAsync(prefs -> {
            MutablePreferences mutable = prefs.toMutablePreferences();
            mutable.set(KEY_QUIET_HOURS_END, endTime);
            return Single.just(mutable);
        });
    }

    public Single<Preferences> setQuietHoursEnabled(boolean enabled) {
        return dataStore.updateDataAsync(prefs -> {
            MutablePreferences mutable = prefs.toMutablePreferences();
            mutable.set(KEY_QUIET_HOURS_ENABLED, enabled);
            return Single.just(mutable);
        });
    }
}
