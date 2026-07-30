package com.app.learning.data.model;

import com.google.gson.annotations.SerializedName;

public class AchievementModel {
    @SerializedName("code")
    private String code;

    @SerializedName("title")
    private String title;

    @SerializedName("description")
    private String description;

    @SerializedName("icon_res")
    private String iconRes;

    @SerializedName("target_count")
    private int targetCount;

    @SerializedName("current_progress")
    private int currentProgress;

    @SerializedName("is_unlocked")
    private boolean isUnlocked;

    @SerializedName("unlocked_at")
    private String unlockedAt;

    public AchievementModel() {}

    public AchievementModel(String code, String title, String description, String iconRes, int targetCount, int currentProgress, boolean isUnlocked, String unlockedAt) {
        this.code = code;
        this.title = title;
        this.description = description;
        this.iconRes = iconRes;
        this.targetCount = targetCount;
        this.currentProgress = currentProgress;
        this.isUnlocked = isUnlocked;
        this.unlockedAt = unlockedAt;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIconRes() { return iconRes; }
    public void setIconRes(String iconRes) { this.iconRes = iconRes; }

    public int getTargetCount() { return targetCount; }
    public void setTargetCount(int targetCount) { this.targetCount = targetCount; }

    public int getCurrentProgress() { return currentProgress; }
    public void setCurrentProgress(int currentProgress) { this.currentProgress = currentProgress; }

    public boolean isUnlocked() { return isUnlocked; }
    public void setUnlocked(boolean unlocked) { isUnlocked = unlocked; }

    public String getUnlockedAt() { return unlockedAt; }
    public void setUnlockedAt(String unlockedAt) { this.unlockedAt = unlockedAt; }
}
