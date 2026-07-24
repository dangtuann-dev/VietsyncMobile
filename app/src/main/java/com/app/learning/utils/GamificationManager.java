package com.app.learning.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class GamificationManager {

    private static final String PREF_NAME = "GamificationPrefs";
    private static final String KEY_XP = "total_xp";
    private static final String KEY_LEVEL = "current_level";

    public interface OnLevelUpListener {
        void onLevelUp(int newLevel, String levelTitle);
    }

    private final SharedPreferences prefs;

    public GamificationManager(Context context) {
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public int getTotalXp() {
        return prefs.getInt(KEY_XP, 0);
    }

    public int getCurrentLevel() {
        return calculateLevel(getTotalXp());
    }

    public String getLevelTitle(int level) {
        if (level <= 1) return "Tân thủ (Beginner)";
        if (level <= 3) return "Tập sự (Novice)";
        if (level <= 5) return "Thành thạo (Intermediate)";
        if (level <= 8) return "Chuyên gia (Advanced)";
        return "Bậc thầy (Expert)";
    }

    public void addXp(int xpAmount, OnLevelUpListener levelUpListener) {
        int currentXp = getTotalXp();
        int oldLevel = calculateLevel(currentXp);

        int newXp = currentXp + xpAmount;
        int newLevel = calculateLevel(newXp);

        prefs.edit().putInt(KEY_XP, newXp).putInt(KEY_LEVEL, newLevel).apply();

        if (newLevel > oldLevel && levelUpListener != null) {
            levelUpListener.onLevelUp(newLevel, getLevelTitle(newLevel));
        }
    }

    public void rewardLessonComplete(OnLevelUpListener listener) {
        addXp(10, listener);
    }

    public void rewardCourseComplete(OnLevelUpListener listener) {
        addXp(50, listener);
    }

    public void rewardQuizCorrect(OnLevelUpListener listener) {
        addXp(5, listener);
    }

    public static int calculateLevel(int xp) {
        if (xp < 50) return 1;
        if (xp < 150) return 2;
        if (xp < 300) return 3;
        if (xp < 500) return 4;
        if (xp < 800) return 5;
        if (xp < 1200) return 6;
        return 7 + (xp - 1200) / 500;
    }
}
