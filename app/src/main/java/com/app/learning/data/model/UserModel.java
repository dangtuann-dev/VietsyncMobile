package com.app.learning.data.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/**
 * UserModel represents the Supabase authenticated user object.
 */
public class UserModel implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("email")
    private String email;

    @SerializedName("role")
    private String role;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("user_metadata")
    private UserMetadata userMetadata;

    // Direct mappings in case of flat custom queries
    @SerializedName("full_name")
    private String fullName;

    @SerializedName("avatar_url")
    private String avatarUrl;

    public UserModel() {
    }

    public UserModel(String id, String email, String fullName, String avatarUrl, String role, String createdAt) {
        this.id = id;
        this.email = email;
        this.fullName = fullName;
        this.avatarUrl = avatarUrl;
        this.role = role;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getFullName() {
        if (fullName != null && !fullName.trim().isEmpty()) {
            return fullName;
        }
        return userMetadata != null ? userMetadata.getFullName() : null;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
        if (userMetadata == null) {
            userMetadata = new UserMetadata();
        }
        userMetadata.setFullName(fullName);
    }

    public String getAvatarUrl() {
        if (avatarUrl != null && !avatarUrl.trim().isEmpty()) {
            return avatarUrl;
        }
        return userMetadata != null ? userMetadata.getAvatarUrl() : null;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
        if (userMetadata == null) {
            userMetadata = new UserMetadata();
        }
        userMetadata.setAvatarUrl(avatarUrl);
    }

    public UserMetadata getUserMetadata() {
        return userMetadata;
    }

    public void setUserMetadata(UserMetadata userMetadata) {
        this.userMetadata = userMetadata;
    }

    /**
     * Nested class representing Supabase's user_metadata field.
     */
    public static class UserMetadata implements Serializable {
        @SerializedName("full_name")
        private String fullName;

        @SerializedName("avatar_url")
        private String avatarUrl;

        public UserMetadata() {
        }

        public UserMetadata(String fullName, String avatarUrl) {
            this.fullName = fullName;
            this.avatarUrl = avatarUrl;
        }

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
}
