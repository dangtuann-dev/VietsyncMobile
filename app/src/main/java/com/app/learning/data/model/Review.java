package com.app.learning.data.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Review implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("course_id")
    private String courseId;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("rating")
    private float rating;

    @SerializedName("comment")
    private String comment;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("helpful_count")
    private int helpfulCount;

    @SerializedName("user")
    private ReviewUser user;

    // Fallback fields for old mock data compatibility
    private String userName;
    private String userAvatar;
    private String date;

    public static class ReviewUser implements Serializable {
        @SerializedName("full_name")
        private String fullName;

        @SerializedName("avatar_url")
        private String avatarUrl;

        public String getFullName() {
            return fullName;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public String getAvatarUrl() {
            return avatarUrl;
        }

        public void setAvatarUrl(String avatarUrl) {
            this.avatarUrl = avatarUrl;
        }
    }

    public Review() {
    }

    public Review(String userName, String userAvatar, float rating, String comment, String date) {
        this.userName = userName;
        this.userAvatar = userAvatar;
        this.rating = rating;
        this.comment = comment;
        this.date = date;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        if (user != null && user.getFullName() != null) {
            return user.getFullName();
        }
        return userName != null ? userName : "Học viên Vietsync";
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserAvatar() {
        if (user != null && user.getAvatarUrl() != null) {
            return user.getAvatarUrl();
        }
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getDate() {
        if (createdAt != null) {
            try {
                if (createdAt.contains("T")) {
                    String[] parts = createdAt.split("T");
                    String datePart = parts[0];
                    String[] dateParts = datePart.split("-");
                    if (dateParts.length == 3) {
                        return dateParts[2] + "/" + dateParts[1] + "/" + dateParts[0];
                    }
                }
            } catch (Exception e) {
                // Ignore parsing errors and fallback
            }
            return createdAt;
        }
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getHelpfulCount() {
        return helpfulCount;
    }

    public void setHelpfulCount(int helpfulCount) {
        this.helpfulCount = helpfulCount;
    }

    public ReviewUser getUser() {
        return user;
    }

    public void setUser(ReviewUser user) {
        this.user = user;
    }
}
