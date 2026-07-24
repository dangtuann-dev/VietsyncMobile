package com.app.learning.data.model;

import com.google.gson.annotations.SerializedName;

public class DiscussionPostModel {

    @SerializedName("id")
    private String id;

    @SerializedName("course_id")
    private String courseId;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("author_name")
    private String authorName;

    @SerializedName("author_avatar")
    private String authorAvatar;

    @SerializedName("title")
    private String title;

    @SerializedName("body")
    private String body;

    @SerializedName("tags")
    private String tags;

    @SerializedName("likes_count")
    private int likesCount;

    @SerializedName("replies_count")
    private int repliesCount;

    @SerializedName("is_solved")
    private boolean isSolved;

    @SerializedName("created_at")
    private String createdAt;

    public DiscussionPostModel() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public String getAuthorAvatar() { return authorAvatar; }
    public void setAuthorAvatar(String authorAvatar) { this.authorAvatar = authorAvatar; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public int getLikesCount() { return likesCount; }
    public void setLikesCount(int likesCount) { this.likesCount = likesCount; }

    public int getRepliesCount() { return repliesCount; }
    public void setRepliesCount(int repliesCount) { this.repliesCount = repliesCount; }

    public boolean isSolved() { return isSolved; }
    public void setSolved(boolean solved) { isSolved = solved; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
