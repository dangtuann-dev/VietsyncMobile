package com.app.learning.data.model;

import com.google.gson.annotations.SerializedName;

public class PeerReviewModel {
    @SerializedName("id")
    private String id;

    @SerializedName("submission_id")
    private String submissionId;

    @SerializedName("reviewer_id")
    private String reviewerId;

    @SerializedName("reviewer_alias")
    private String reviewerAlias; // e.g. "Reviewer #1"

    @SerializedName("clarity_rating")
    private float clarityRating; // 1-5

    @SerializedName("accuracy_rating")
    private float accuracyRating; // 1-5

    @SerializedName("completeness_rating")
    private float completenessRating; // 1-5

    @SerializedName("comment")
    private String comment;

    @SerializedName("reviewed_at")
    private String reviewedAt;

    public PeerReviewModel() {}

    public PeerReviewModel(String id, String submissionId, String reviewerAlias, float clarityRating, float accuracyRating, float completenessRating, String comment, String reviewedAt) {
        this.id = id;
        this.submissionId = submissionId;
        this.reviewerAlias = reviewerAlias;
        this.clarityRating = clarityRating;
        this.accuracyRating = accuracyRating;
        this.completenessRating = completenessRating;
        this.comment = comment;
        this.reviewedAt = reviewedAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getSubmissionId() { return submissionId; }
    public void setSubmissionId(String submissionId) { this.submissionId = submissionId; }

    public String getReviewerId() { return reviewerId; }
    public void setReviewerId(String reviewerId) { this.reviewerId = reviewerId; }

    public String getReviewerAlias() { return reviewerAlias; }
    public void setReviewerAlias(String reviewerAlias) { this.reviewerAlias = reviewerAlias; }

    public float getClarityRating() { return clarityRating; }
    public void setClarityRating(float clarityRating) { this.clarityRating = clarityRating; }

    public float getAccuracyRating() { return accuracyRating; }
    public void setAccuracyRating(float accuracyRating) { this.accuracyRating = accuracyRating; }

    public float getCompletenessRating() { return completenessRating; }
    public void setCompletenessRating(float completenessRating) { this.completenessRating = completenessRating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public String getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(String reviewedAt) { this.reviewedAt = reviewedAt; }

    public float getAverageRating() {
        return (clarityRating + accuracyRating + completenessRating) / 3f;
    }
}
