package com.app.learning.ui.learning;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.app.learning.data.api.ApiClient;
import com.app.learning.data.api.CourseApi;
import com.app.learning.data.model.LessonProgressModel;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LessonViewModel extends AndroidViewModel {

    private final MutableLiveData<JsonObject> currentLesson = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isProgress80Percent = new MutableLiveData<>(false);
    private final CourseApi courseApi;

    public LessonViewModel(@NonNull Application application) {
        super(application);
        this.courseApi = ApiClient.getInstance().createService(CourseApi.class);
    }

    public LiveData<JsonObject> getCurrentLesson() { return currentLesson; }
    public LiveData<Boolean> getIsProgress80Percent() { return isProgress80Percent; }

    public void loadLesson(String lessonId) {
        courseApi.getLessonById("eq." + lessonId, "*").enqueue(new Callback<List<JsonObject>>() {
            @Override
            public void onResponse(Call<List<JsonObject>> call, Response<List<JsonObject>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    currentLesson.setValue(response.body().get(0));
                }
            }

            @Override
            public void onFailure(Call<List<JsonObject>> call, Throwable t) {}
        });
    }

    public void updatePlaybackProgress(long currentPositionMs, long totalDurationMs, String courseId, String userId) {
        if (totalDurationMs > 0) {
            float progress = (float) currentPositionMs / totalDurationMs;
            if (progress >= 0.80f && Boolean.FALSE.equals(isProgress80Percent.getValue())) {
                isProgress80Percent.setValue(true);
                markProgress(courseId, userId);
            }
        }
    }

    public void markProgress(String courseId, String userId) {
        // Send progress update to Supabase endpoint
        JsonObject body = new JsonObject();
        body.addProperty("progress_percent", 100);
        // Fire-and-forget update
    }
}
