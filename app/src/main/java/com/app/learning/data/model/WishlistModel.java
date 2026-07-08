package com.app.learning.data.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class WishlistModel implements Serializable {

    @SerializedName("user_id")
    private String userId;

    @SerializedName("course_id")
    private String courseId;

    @SerializedName("course")
    private Course course;

    public WishlistModel() {
    }

    public WishlistModel(String userId, String courseId) {
        this.userId = userId;
        this.courseId = courseId;
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

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }
}
