package com.app.learning.data.model;

public class DownloadItem {
    private String id;
    private String lessonTitle;
    private String courseTitle;
    private String size;
    private String date;

    public DownloadItem(String id, String lessonTitle, String courseTitle, String size, String date) {
        this.id = id;
        this.lessonTitle = lessonTitle;
        this.courseTitle = courseTitle;
        this.size = size;
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public String getLessonTitle() {
        return lessonTitle;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public String getSize() {
        return size;
    }

    public String getDate() {
        return date;
    }
}
