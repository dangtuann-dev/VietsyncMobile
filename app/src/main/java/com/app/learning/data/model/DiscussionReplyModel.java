package com.app.learning.data.model;

import com.google.gson.annotations.SerializedName;

public class DiscussionReplyModel {

    @SerializedName("id")
    private String id;

    @SerializedName("post_id")
    private String postId;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("author_name")
    private String authorName;

    @SerializedName("author_avatar")
    private String authorAvatar;

    @SerializedName("reply_text")
    private String replyText;

    @SerializedName("created_at")
    private String createdAt;

    public DiscussionReplyModel() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPostId() { return postId; }
    public void setPostId(String postId) { this.postId = postId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }

    public String getAuthorAvatar() { return authorAvatar; }
    public void setAuthorAvatar(String authorAvatar) { this.authorAvatar = authorAvatar; }

    public String getReplyText() { return replyText; }
    public void setReplyText(String replyText) { this.replyText = replyText; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
