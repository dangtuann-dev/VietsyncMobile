package com.app.learning.data.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Lesson implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("course_id")
    private String courseId;

    @SerializedName("title")
    private String title;

    @SerializedName("video_url")
    private String videoUrl;

    @SerializedName("content")
    private String content;

    @SerializedName("order_index")
    private int orderIndex;

    @SerializedName("duration")
    private int duration;

    // Helper fields for UI, not persisted in the same format in lessons table
    private String type; // "video", "quiz", "pdf"
    private boolean isFreePreview;

    public Lesson() {
    }

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(int orderIndex) {
        this.orderIndex = orderIndex;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getType() {
        if (type == null) {
            // Determine type by content/fields
            if (videoUrl != null && !videoUrl.isEmpty()) {
                return "video";
            } else if (title != null && (title.toLowerCase().contains("trắc nghiệm") || title.toLowerCase().contains("quiz") || title.toLowerCase().contains("bài tập"))) {
                return "quiz";
            } else {
                return "pdf";
            }
        }
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isFreePreview() {
        return isFreePreview;
    }

    public void setFreePreview(boolean freePreview) {
        isFreePreview = freePreview;
    }
}
