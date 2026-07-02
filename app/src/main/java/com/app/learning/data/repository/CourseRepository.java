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
import java.util.HashMap;
import java.io.IOException;

import retrofit2.Call;

public class CourseRepository extends BaseRepository {

    private final CourseApi courseApi;

    public CourseRepository() {
        super();
        this.courseApi = ApiClient.getInstance().createService(CourseApi.class);
    }

    public CourseRepository(@NonNull Context context) {
        this();
    }

    public interface CourseListCallback {
        void onSuccess(List<Course> courseList);
        void onError(String errorMessage);
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

    public LiveData<Resource<List<Course>>> getCoursesByCategory(String categoryId, String sortBy, int page) {
        MutableLiveData<Resource<List<Course>>> resultLiveData = new MutableLiveData<>();
        
        Map<String, String> options = new HashMap<>();
        options.put("select", "*,instructor:users(full_name)");
        
        String resolvedId = resolveCategoryId(categoryId);
        if (resolvedId != null) {
            options.put("category_id", "eq." + resolvedId);
        }
        
        String order = "enrolled_count.desc";
        if (sortBy != null) {
            if (sortBy.equals("Phổ biến") || sortBy.equalsIgnoreCase("Popular")) {
                order = "enrolled_count.desc";
            } else if (sortBy.equals("Mới nhất") || sortBy.equalsIgnoreCase("Newest")) {
                order = "id.desc";
            } else if (sortBy.equals("Đánh giá") || sortBy.equalsIgnoreCase("Rating")) {
                order = "rating.desc";
            } else if (sortBy.equals("Giá thấp đến cao") || sortBy.equalsIgnoreCase("Price Low-High")) {
                order = "price.asc";
            }
        }
        options.put("order", order);
        
        int limit = 10;
        int offset = (page - 1) * limit;
        options.put("limit", String.valueOf(limit));
        options.put("offset", String.valueOf(offset));
        
        Call<List<Course>> call = courseApi.searchCourses(options);
        executeCall(call, resultLiveData);
        return resultLiveData;
    }

    public void getCoursesByCategory(String categoryId, String sortBy, int page, CourseListCallback callback) {
        Map<String, String> options = new HashMap<>();
        options.put("select", "*,instructor:users(full_name)");
        
        String resolvedId = resolveCategoryId(categoryId);
        if (resolvedId != null) {
            options.put("category_id", "eq." + resolvedId);
        }
        
        String order = "enrolled_count.desc";
        if (sortBy != null) {
            if (sortBy.equals("Phổ biến") || sortBy.equalsIgnoreCase("Popular")) {
                order = "enrolled_count.desc";
            } else if (sortBy.equals("Mới nhất") || sortBy.equalsIgnoreCase("Newest")) {
                order = "id.desc";
            } else if (sortBy.equals("Đánh giá") || sortBy.equalsIgnoreCase("Rating")) {
                order = "rating.desc";
            } else if (sortBy.equals("Giá thấp đến cao") || sortBy.equalsIgnoreCase("Price Low-High")) {
                order = "price.asc";
            }
        }
        options.put("order", order);
        
        int limit = 10;
        int offset = (page - 1) * limit;
        options.put("limit", String.valueOf(limit));
        options.put("offset", String.valueOf(offset));

        Call<List<Course>> call = courseApi.searchCourses(options);
        executors.networkIO().execute(() -> {
            try {
                retrofit2.Response<List<Course>> response = call.execute();
                if (response.isSuccessful()) {
                    executors.mainThread().execute(() -> callback.onSuccess(response.body()));
                } else {
                    executors.mainThread().execute(() -> callback.onError("Server response error: " + response.code()));
                }
            } catch (IOException e) {
                executors.mainThread().execute(() -> callback.onError(e.getLocalizedMessage()));
            }
        });
    }

    private String resolveCategoryId(String categoryIdOrName) {
        if (categoryIdOrName == null || categoryIdOrName.trim().isEmpty()) {
            return null;
        }
        try {
            Long.parseLong(categoryIdOrName);
            return categoryIdOrName;
        } catch (NumberFormatException e) {
            switch (categoryIdOrName.toLowerCase()) {
                case "technology":
                case "công nghệ thông tin":
                    return "1";
                case "business":
                case "kinh doanh & khởi nghiệp":
                    return "2";
                case "design":
                case "thiết kế đồ họa":
                    return "3";
                case "language":
                case "ngoại ngữ":
                    return "4";
                default:
                    return null;
            }
        }
    }
}
