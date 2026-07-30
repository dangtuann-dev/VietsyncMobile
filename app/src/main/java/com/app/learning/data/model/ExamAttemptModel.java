package com.app.learning.data.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class ExamAttemptModel implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("course_id")
    private String courseId;

    @SerializedName("score")
    private int score;

    @SerializedName("passed")
    private boolean passed;

    @SerializedName("attempt_number")
    private int attemptNumber;

    @SerializedName("submitted_at")
    private String submittedAt;

    public ExamAttemptModel() {}

    public ExamAttemptModel(String userId, String courseId, int score, boolean passed, int attemptNumber) {
        this.userId = userId;
        this.courseId = courseId;
        this.score = score;
        this.passed = passed;
        this.attemptNumber = attemptNumber;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public boolean isPassed() { return passed; }
    public void setPassed(boolean passed) { this.passed = passed; }

    public int getAttemptNumber() { return attemptNumber; }
    public void setAttemptNumber(int attemptNumber) { this.attemptNumber = attemptNumber; }

    public String getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(String submittedAt) { this.submittedAt = submittedAt; }
}
