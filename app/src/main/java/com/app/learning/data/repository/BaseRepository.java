package com.app.learning.data.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.app.learning.data.api.ApiError;
import com.app.learning.data.api.Resource;
import com.app.learning.utils.AppExecutors;
import com.google.gson.Gson;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

/**
 * BaseRepository acts as a foundational base class for all repositories.
 * It encapsulates common logic for executing network calls, handling errors,
 * and dispatching results using the {@link Resource} wrapper.
 */
public abstract class BaseRepository {

    protected final AppExecutors executors;
    private final Gson gson;

    protected BaseRepository() {
        this.executors = AppExecutors.getInstance();
        this.gson = new Gson();
    }

    /**
     * Executes a Retrofit Call synchronously on the background network thread,
     * posting loading, success, or error states to the provided MutableLiveData.
     *
     * @param call     The Retrofit Call object
     * @param liveData The LiveData container to post updates into
     * @param <T>      The model type expected from the API
     */
    protected <T> void executeCall(Call<T> call, MutableLiveData<Resource<T>> liveData) {
        // Dispatch LOADING state instantly
        liveData.postValue(Resource.loading());

        executors.networkIO().execute(() -> {
            try {
                // Perform synchronous execution on background thread
                Response<T> response = call.execute();

                if (response.isSuccessful() && response.body() != null) {
                    liveData.postValue(Resource.success(response.body()));
                } else {
                    ApiError error = parseError(response);
                    liveData.postValue(Resource.error(error));
                }
            } catch (IOException e) {
                // Handles socket, timeout or offline connectivity exceptions
                android.util.Log.e("BaseRepository", "Network IO Failure", e);
                liveData.postValue(Resource.error(new ApiError(
                        "503",
                        "Không có kết nối mạng. Vui lòng thử lại. Chi tiết: " + e.getMessage(),
                        e.getLocalizedMessage(),
                        "Network IO Failure"
                )));
            } catch (Exception e) {
                // Generic safety block
                liveData.postValue(Resource.error(new ApiError(
                        "500",
                        "Đã xảy ra lỗi hệ thống: " + e.getLocalizedMessage(),
                        null,
                        "Internal System Exception"
                )));
            }
        });
    }

    /**
     * Parses the HTTP error response body using GSON.
     *
     * @param response The Retrofit Response container representing a failure
     * @return Deserialized ApiError model
     */
    @NonNull
    protected ApiError parseError(Response<?> response) {
        if (response.errorBody() == null) {
            return new ApiError("Unknown error occurred");
        }
        try {
            String errorJson = response.errorBody().string();
            ApiError parsed = gson.fromJson(errorJson, ApiError.class);
            if (parsed == null || parsed.getMessage() == null) {
                return new ApiError(String.valueOf(response.code()), "Lỗi hệ thống (" + response.code() + ")", null, null);
            }
            return parsed;
        } catch (Exception e) {
            return new ApiError(
                    String.valueOf(response.code()),
                    "Lỗi máy chủ (" + response.code() + ")",
                    e.getLocalizedMessage(),
                    null
            );
        }
    }
}
