package com.app.learning.data.model;

import java.io.Serializable;




public class RecentEnrollment implements Serializable {
    private final String studentName;
    private final String studentEmail;
    private final String courseName;
    private final String enrollDate;
    private final String avatarUrl;

    public RecentEnrollment(String studentName, String studentEmail, String courseName, String enrollDate, String avatarUrl) {
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.courseName = courseName;
        this.enrollDate = enrollDate;
        this.avatarUrl = avatarUrl;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getEnrollDate() {
        return enrollDate;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }
}
