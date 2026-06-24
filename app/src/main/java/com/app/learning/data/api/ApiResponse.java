package com.app.learning.data.api;







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




    public boolean isSuccessful() {
        return statusCode >= 200 && statusCode < 300;
    }




    public T getData() {
        return data;
    }




    public ApiError getError() {
        return error;
    }




    public int getStatusCode() {
        return statusCode;
    }
}
