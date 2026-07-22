package com.app.learning.data.model;

public class LessonProgressModel {
    private String lessonId;
    private String userId;
    private boolean completed;
    private long watchedDuration;
    private long lastWatchedAt;

    public LessonProgressModel() {
        // Default constructor required for Firebase/Supabase serialization
    }

    public LessonProgressModel(String lessonId, String userId, boolean completed, long watchedDuration, long lastWatchedAt) {
        this.lessonId = lessonId;
        this.userId = userId;
        this.completed = completed;
        this.watchedDuration = watchedDuration;
        this.lastWatchedAt = lastWatchedAt;
    }

    public String getLessonId() {
        return lessonId;
    }

    public void setLessonId(String lessonId) {
        this.lessonId = lessonId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public long getWatchedDuration() {
        return watchedDuration;
    }

    public void setWatchedDuration(long watchedDuration) {
        this.watchedDuration = watchedDuration;
    }

    public long getLastWatchedAt() {
        return lastWatchedAt;
    }

    public void setLastWatchedAt(long lastWatchedAt) {
        this.lastWatchedAt = lastWatchedAt;
    }
}
