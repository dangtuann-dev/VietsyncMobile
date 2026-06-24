package com.app.learning.data.api;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;







public class Resource<T> {

    public enum Status {
        SUCCESS,
        ERROR,
        LOADING
    }

    @NonNull
    public final Status status;

    @Nullable
    public final T data;

    @Nullable
    public final ApiError error;

    private Resource(@NonNull Status status, @Nullable T data, @Nullable ApiError error) {
        this.status = status;
        this.data = data;
        this.error = error;
    }

    public static <T> Resource<T> success(@Nullable T data) {
        return new Resource<>(Status.SUCCESS, data, null);
    }

    public static <T> Resource<T> error(@NonNull ApiError error) {
        return new Resource<>(Status.ERROR, null, error);
    }

    public static <T> Resource<T> loading() {
        return new Resource<>(Status.LOADING, null, null);
    }

    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }

    public boolean isError() {
        return status == Status.ERROR;
    }

    public boolean isLoading() {
        return status == Status.LOADING;
    }
}
