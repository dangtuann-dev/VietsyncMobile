package com.app.learning.data.model;

import com.google.gson.annotations.SerializedName;

public class PeerSubmissionModel {
    @SerializedName("id")
    private String id;

    @SerializedName("assignment_id")
    private String assignmentId;

    @SerializedName("user_id")
    private String userId;

    @SerializedName("anonymous_alias")
    private String anonymousAlias; // e.g. "Học viên #1042"

    @SerializedName("content_text")
    private String contentText;

    @SerializedName("file_url")
    private String fileUrl;

    @SerializedName("submitted_at")
    private String submittedAt;

    public PeerSubmissionModel() {}

    public PeerSubmissionModel(String id, String assignmentId, String anonymousAlias, String contentText, String fileUrl, String submittedAt) {
        this.id = id;
        this.assignmentId = assignmentId;
        this.anonymousAlias = anonymousAlias;
        this.contentText = contentText;
        this.fileUrl = fileUrl;
        this.submittedAt = submittedAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getAssignmentId() { return assignmentId; }
    public void setAssignmentId(String assignmentId) { this.assignmentId = assignmentId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getAnonymousAlias() { return anonymousAlias; }
    public void setAnonymousAlias(String anonymousAlias) { this.anonymousAlias = anonymousAlias; }

    public String getContentText() { return contentText; }
    public void setContentText(String contentText) { this.contentText = contentText; }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public String getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(String submittedAt) { this.submittedAt = submittedAt; }
}
