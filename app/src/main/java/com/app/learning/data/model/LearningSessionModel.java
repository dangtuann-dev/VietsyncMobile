package com.app.learning.data.model;

import com.google.gson.annotations.SerializedName;

public class LearningSessionModel {
    @SerializedName("id")
    private String id;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("date")
    private String date; // YYYY-MM-DD

    @SerializedName("duration_minutes")
    private int durationMinutes;

    @SerializedName("subject_name")
    private String subjectName;

    @SerializedName("lessons_completed")
    private int lessonsCompleted;

    public LearningSessionModel() {}

    public LearningSessionModel(String date, int durationMinutes, String subjectName, int lessonsCompleted) {
        this.date = date;
        this.durationMinutes = durationMinutes;
        this.subjectName = subjectName;
        this.lessonsCompleted = lessonsCompleted;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) { this.durationMinutes = durationMinutes; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public int getLessonsCompleted() { return lessonsCompleted; }
    public void setLessonsCompleted(int lessonsCompleted) { this.lessonsCompleted = lessonsCompleted; }
}
