package com.app.learning.data.model;

import com.google.gson.annotations.SerializedName;

public class Certificate {
    @SerializedName("id")
    private String id;

    @SerializedName("course_id")
    private String courseId;

    @SerializedName("issued_at")
    private String issuedAt;

    @SerializedName("certificate_url")
    private String certificateUrl;

    @SerializedName("courses")
    private CourseInfo course;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCourseId() {
        return courseId;
    }

    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }

    public String getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(String issuedAt) {
        this.issuedAt = issuedAt;
    }

    public String getCertificateUrl() {
        return certificateUrl;
    }

    public void setCertificateUrl(String certificateUrl) {
        this.certificateUrl = certificateUrl;
    }

    public CourseInfo getCourse() {
        return course;
    }

    public void setCourse(CourseInfo course) {
        this.course = course;
    }

    public static class CourseInfo {
        @SerializedName("title")
        private String title;

        @SerializedName("thumbnail")
        private String thumbnail;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
        }
    }
}
