package com.app.learning.data.repository;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.app.learning.data.api.ApiClient;
import com.app.learning.data.api.CourseApi;
import com.app.learning.data.api.Resource;
import com.app.learning.data.model.Category;
import com.app.learning.data.model.Course;

import java.util.List;
import java.util.Map;

import retrofit2.Call;

public class CourseRepository extends BaseRepository {

    private final CourseApi courseApi;

    public CourseRepository(@NonNull Context context) {
        super();
        this.courseApi = ApiClient.getInstance().createService(CourseApi.class);
    }

    public LiveData<Resource<List<Course>>> searchCourses(Map<String, String> options) {
        MutableLiveData<Resource<List<Course>>> resultLiveData = new MutableLiveData<>();
        Call<List<Course>> call = courseApi.searchCourses(options);
        executeCall(call, resultLiveData);
        return resultLiveData;
    }

    public LiveData<Resource<List<Category>>> getCategories() {
        MutableLiveData<Resource<List<Category>>> resultLiveData = new MutableLiveData<>();
        Call<List<Category>> call = courseApi.getCategories("*");
        executeCall(call, resultLiveData);
        return resultLiveData;
    }
}
