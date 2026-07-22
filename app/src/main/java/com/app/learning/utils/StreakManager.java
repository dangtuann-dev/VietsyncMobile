package com.app.learning.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class StreakManager {

    private static final String PREF_NAME = "StreakPrefs";
    private static final String KEY_STREAK = "current_streak";
    private static final String KEY_LAST_STUDY_DATE = "last_study_date";
    private static final String TAG = "StreakManager";
    
    private SharedPreferences prefs;

    public StreakManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void logStudySession() {
        long lastStudyDate = prefs.getLong(KEY_LAST_STUDY_DATE, 0);
        long currentDate = System.currentTimeMillis();
        
        int currentStreak = prefs.getInt(KEY_STREAK, 0);
        
        if (lastStudyDate == 0) {
            // First time studying
            currentStreak = 1;
        } else {
            long diffInMillis = currentDate - lastStudyDate;
            long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis);
            
            // Checking if same day using Calendar to be accurate with timezones
            Calendar lastCal = Calendar.getInstance();
            lastCal.setTimeInMillis(lastStudyDate);
            Calendar currentCal = Calendar.getInstance();
            currentCal.setTimeInMillis(currentDate);
            
            boolean sameDay = lastCal.get(Calendar.YEAR) == currentCal.get(Calendar.YEAR) &&
                              lastCal.get(Calendar.DAY_OF_YEAR) == currentCal.get(Calendar.DAY_OF_YEAR);
                              
            if (sameDay) {
                // Already studied today, keep streak
            } else if (diffInDays <= 1) {
                // Studied yesterday, increment streak
                currentStreak++;
            } else {
                // Missed a day or more, reset streak
                currentStreak = 1;
            }
        }
        
        // Save to local
        prefs.edit()
            .putInt(KEY_STREAK, currentStreak)
            .putLong(KEY_LAST_STUDY_DATE, currentDate)
            .apply();
            
        syncToSupabase(currentStreak);
    }
    
    public int getCurrentStreak() {
        return prefs.getInt(KEY_STREAK, 0);
    }
    
    private void syncToSupabase(int streak) {
        Log.d(TAG, "Syncing streak of " + streak + " days to Supabase...");
        // Network call to update streak on Supabase
    }
}
