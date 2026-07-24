package com.app.learning.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class StreakManager {

    private static final String PREF_NAME = "StreakPrefs";
    private static final String KEY_STREAK = "current_streak";
    private static final String KEY_LONGEST_STREAK = "longest_streak";
    private static final String KEY_LAST_STUDY_DATE = "last_study_date";
    private static final String TAG = "StreakManager";

    private final SharedPreferences prefs;

    public StreakManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void logStudySession() {
        long lastStudyDate = prefs.getLong(KEY_LAST_STUDY_DATE, 0);
        long currentDate = System.currentTimeMillis();

        int currentStreak = prefs.getInt(KEY_STREAK, 0);
        int longestStreak = prefs.getInt(KEY_LONGEST_STREAK, 0);

        if (lastStudyDate == 0) {
            currentStreak = 1;
        } else {
            Calendar lastCal = Calendar.getInstance();
            lastCal.setTimeInMillis(lastStudyDate);
            Calendar currentCal = Calendar.getInstance();
            currentCal.setTimeInMillis(currentDate);

            boolean sameDay = lastCal.get(Calendar.YEAR) == currentCal.get(Calendar.YEAR) &&
                              lastCal.get(Calendar.DAY_OF_YEAR) == currentCal.get(Calendar.DAY_OF_YEAR);

            if (!sameDay) {
                lastCal.add(Calendar.DAY_OF_YEAR, 1);
                boolean consecutiveDay = lastCal.get(Calendar.YEAR) == currentCal.get(Calendar.YEAR) &&
                                         lastCal.get(Calendar.DAY_OF_YEAR) == currentCal.get(Calendar.DAY_OF_YEAR);

                if (consecutiveDay) {
                    currentStreak++;
                } else {
                    currentStreak = 1;
                }
            }
        }

        if (currentStreak > longestStreak) {
            longestStreak = currentStreak;
        }

        prefs.edit()
                .putInt(KEY_STREAK, currentStreak)
                .putInt(KEY_LONGEST_STREAK, longestStreak)
                .putLong(KEY_LAST_STUDY_DATE, currentDate)
                .apply();

        syncToSupabase(currentStreak);
    }

    public int getCurrentStreak() {
        return prefs.getInt(KEY_STREAK, 1);
    }

    public int getLongestStreak() {
        return prefs.getInt(KEY_LONGEST_STREAK, 1);
    }

    private void syncToSupabase(int streak) {
        Log.d(TAG, "Syncing streak of " + streak + " days to Supabase...");
    }
}
