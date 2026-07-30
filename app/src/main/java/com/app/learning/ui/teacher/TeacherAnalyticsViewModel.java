package com.app.learning.ui.teacher;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.app.learning.data.repository.TeacherAnalyticsRepository;
import java.util.Map;

public class TeacherAnalyticsViewModel extends AndroidViewModel {

    private TeacherAnalyticsRepository repository;
    private MutableLiveData<Map<String, Object>> teacherStats = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public TeacherAnalyticsViewModel(@NonNull Application application) {
        super(application);
        repository = new TeacherAnalyticsRepository(application);
    }

    public LiveData<Map<String, Object>> getTeacherStats() { return teacherStats; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }

    public void loadAnalyticsData() {
        isLoading.setValue(true);
        repository.getTeacherStats(new TeacherAnalyticsRepository.RepositoryCallback<Map<String, Object>>() {
            @Override
            public void onSuccess(Map<String, Object> data) {
                isLoading.setValue(false);
                teacherStats.setValue(data);
            }

            @Override
            public void onError(String message) {
                isLoading.setValue(false);
            }
        });
    }
}
