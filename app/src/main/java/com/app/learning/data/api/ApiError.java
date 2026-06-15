package com.app.learning.data.api;

import com.google.gson.annotations.SerializedName;

/**
 * ApiError represents structural error response messages thrown by the Supabase PostgREST server.
 * Typically returns details like Postgres error code, description, and suggestions (hint).
 */
public class ApiError {

    @SerializedName("code")
    private String errorCode;

    @SerializedName("message")
    private String message;

    @SerializedName("hint")
    private String hint;

    @SerializedName("details")
    private String details;

    public ApiError() {
    }

    public ApiError(String message) {
        this.message = message;
    }

    public ApiError(String errorCode, String message, String hint, String details) {
        this.errorCode = errorCode;
        this.message = message;
        this.hint = hint;
        this.details = details;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message != null ? message : "Unknown error occurred";
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getMessage());
        if (errorCode != null) {
            sb.append(" (Code: ").append(errorCode).append(")");
        }
        if (hint != null) {
            sb.append("\nHint: ").append(hint);
        }
        return sb.toString();
    }
}
