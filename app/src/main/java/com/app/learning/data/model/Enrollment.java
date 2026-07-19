package com.app.learning.data.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Enrollment implements Serializable {

    @SerializedName("user_id")
    private String userId;

    @SerializedName("course_id")
    private String courseId;

    @SerializedName("enrolled_at")
    private String enrolledAt;

    @SerializedName("progress_percent")
    private int progressPercent;

    @SerializedName("completed_at")
    private String completedAt;

    @SerializedName("course")
    private Course course;

    public Enrollment() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getEnrolledAt() {
        return enrolledAt;
    }

    public void setEnrolledAt(String enrolledAt) {
        this.enrolledAt = enrolledAt;
    }

    public int getProgressPercent() {
        return progressPercent;
    }

    public void setProgressPercent(int progressPercent) {
        this.progressPercent = progressPercent;
    }

    public String getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(String completedAt) {
        this.completedAt = completedAt;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}
