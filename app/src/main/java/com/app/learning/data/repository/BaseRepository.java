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






public abstract class BaseRepository {

    protected final AppExecutors executors;
    private final Gson gson;

    protected BaseRepository() {
        this.executors = AppExecutors.getInstance();
        this.gson = new Gson();
    }









    protected <T> void executeCall(Call<T> call, MutableLiveData<Resource<T>> liveData) {

        liveData.postValue(Resource.loading());

        executors.networkIO().execute(() -> {
            try {

                Response<T> response = call.execute();

                if (response.isSuccessful()) {
                    liveData.postValue(Resource.success(response.body()));
                } else {
                    ApiError error = parseError(response);
                    liveData.postValue(Resource.error(error));
                }
            } catch (IOException e) {

                android.util.Log.e("BaseRepository", "Network IO Failure", e);
                liveData.postValue(Resource.error(new ApiError(
                        "503",
                        "Không có kết nối mạng. Vui lòng thử lại. Chi tiết: " + e.getMessage(),
                        e.getLocalizedMessage(),
                        "Network IO Failure"
                )));
            } catch (Exception e) {

                liveData.postValue(Resource.error(new ApiError(
                        "500",
                        "Đã xảy ra lỗi hệ thống: " + e.getLocalizedMessage(),
                        null,
                        "Internal System Exception"
                )));
            }
        });
    }







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
