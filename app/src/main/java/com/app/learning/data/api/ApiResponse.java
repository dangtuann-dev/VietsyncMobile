package com.app.learning.data.api;

/**
 * ApiResponse is a generic wrapper that captures the state of network responses.
 * It contains the response body data, HTTP status code, and deserialized error models.
 *
 * @param <T> The expected data type of the API response
 */
public class ApiResponse<T> {

    private final T data;
    private final ApiError error;
    private final int statusCode;

    public ApiResponse(T data, int statusCode) {
        this.data = data;
        this.error = null;
        this.statusCode = statusCode;
    }

    public ApiResponse(ApiError error, int statusCode) {
        this.data = null;
        this.error = error;
        this.statusCode = statusCode;
    }

    /**
     * Helper to verify if the request returned a successful HTTP code (2xx range).
     */
    public boolean isSuccessful() {
        return statusCode >= 200 && statusCode < 300;
    }

    /**
     * Get the successfully returned data payload.
     */
    public T getData() {
        return data;
    }

    /**
     * Get the deserialized API error details if the request failed.
     */
    public ApiError getError() {
        return error;
    }

    /**
     * Get the HTTP response code.
     */
    public int getStatusCode() {
        return statusCode;
    }
}
