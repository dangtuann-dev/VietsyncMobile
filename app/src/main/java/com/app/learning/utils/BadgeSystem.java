package com.app.learning.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;

public class BadgeSystem {

    public static class BadgeItem {
        private String id;
        private String title;
        private String description;
        private String iconName;
        private boolean isUnlocked;

        public BadgeItem(String id, String title, String description, String iconName, boolean isUnlocked) {
            this.id = id;
            this.title = title;
            this.description = description;
            this.iconName = iconName;
            this.isUnlocked = isUnlocked;
        }

        public String getId() { return id; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getIconName() { return iconName; }
        public boolean isUnlocked() { return isUnlocked; }
    }

    private static final String PREF_NAME = "BadgePrefs";
    private final SharedPreferences prefs;

    public BadgeSystem(Context context) {
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public List<BadgeItem> getAllBadges() {
        List<BadgeItem> list = new ArrayList<>();
        list.add(new BadgeItem("b1", "Bài học đầu tiên", "Hoàn thành bài học đầu tiên", "ic_badge", isUnlocked("b1", true)));
        list.add(new BadgeItem("b2", "Chuỗi 7 ngày", "Học liên tục trong 7 ngày", "ic_flame", isUnlocked("b2", true)));
        list.add(new BadgeItem("b3", "Vua trắc nghiệm", "Đạt 100% điểm quiz 3 lần", "ic_quiz", isUnlocked("b3", false)));
        list.add(new BadgeItem("b4", "Chủ nhân chứng chỉ", "Hoàn thành 1 khóa học xuất sắc", "ic_certificate", isUnlocked("b4", true)));
        list.add(new BadgeItem("b5", "Chim sớm", "Học vào lúc 6:00 sáng", "ic_sun", isUnlocked("b5", false)));
        return list;
    }

    public boolean isUnlocked(String badgeId, boolean defaultValue) {
        return prefs.getBoolean("badge_" + badgeId, defaultValue);
    }

    public void unlockBadge(String badgeId) {
        prefs.edit().putBoolean("badge_" + badgeId, true).apply();
    }
}
