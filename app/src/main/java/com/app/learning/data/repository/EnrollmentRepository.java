package com.app.learning.data.repository;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.app.learning.data.api.ApiClient;
import com.app.learning.data.api.ApiError;
import com.app.learning.data.api.EnrollmentApi;
import com.app.learning.data.api.Resource;
import com.app.learning.data.api.WishlistApi;
import com.app.learning.data.model.Enrollment;
import com.app.learning.data.model.WishlistModel;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Response;

public class EnrollmentRepository extends BaseRepository {

    private final WishlistApi wishlistApi;
    private final EnrollmentApi enrollmentApi;

    public EnrollmentRepository() {
        super();
        this.wishlistApi = ApiClient.getInstance().createService(WishlistApi.class);
        this.enrollmentApi = ApiClient.getInstance().createService(EnrollmentApi.class);
    }

    public EnrollmentRepository(@NonNull Context context) {
        this();
    }

    public LiveData<Resource<Void>> enrollInCourse(String userId, String courseId) {
        MutableLiveData<Resource<Void>> resultLiveData = new MutableLiveData<>();
        Map<String, Object> body = new HashMap<>();
        body.put("user_id", userId);
        body.put("course_id", courseId);
        body.put("progress_percent", 0);
        Call<Void> call = wishlistApi.enrollInCourse(body, "representation");
        executeCall(call, resultLiveData);
        return resultLiveData;
    }

    public LiveData<Resource<List<Enrollment>>> loadEnrolledCourses(String userId, String status) {
        MutableLiveData<Resource<List<Enrollment>>> resultLiveData = new MutableLiveData<>();
        resultLiveData.postValue(Resource.loading());

        if ("saved".equalsIgnoreCase(status)) {
            Call<List<WishlistModel>> call = wishlistApi.getWishlist("eq." + userId, "*,course:courses(*,instructor:users(full_name))");
            executors.networkIO().execute(() -> {
                try {
                    Response<List<WishlistModel>> response = call.execute();
                    if (response.isSuccessful()) {
                        List<Enrollment> list = new ArrayList<>();
                        if (response.body() != null) {
                            for (WishlistModel w : response.body()) {
                                if (w.getCourse() != null) {
                                    Enrollment e = new Enrollment();
                                    e.setUserId(w.getUserId());
                                    e.setCourseId(w.getCourseId());
                                    e.setCourse(w.getCourse());
                                    e.setProgressPercent(0);
                                    list.add(e);
                                }
                            }
                        }
                        resultLiveData.postValue(Resource.success(list));
                    } else {
                        resultLiveData.postValue(Resource.error(parseError(response)));
                    }
                } catch (IOException e) {
                    resultLiveData.postValue(Resource.error(new ApiError(
                            "503",
                            "Không có kết nối mạng. Vui lòng thử lại. Chi tiết: " + e.getMessage(),
                            e.getLocalizedMessage(),
                            "Network IO Failure"
                    )));
                } catch (Exception e) {
                    resultLiveData.postValue(Resource.error(new ApiError(
                            "500",
                            "Đã xảy ra lỗi hệ thống: " + e.getLocalizedMessage(),
                            null,
                            "Internal System Exception"
                    )));
                }
            });
        } else {
            Call<List<Enrollment>> call;
            String selectFields = "*,course:courses(*,instructor:users(full_name))";
            if ("completed".equalsIgnoreCase(status)) {
                call = enrollmentApi.getEnrollmentsFiltered("eq." + userId, "eq.100", selectFields);
            } else if ("in_progress".equalsIgnoreCase(status)) {
                call = enrollmentApi.getEnrollmentsFiltered("eq." + userId, "lt.100", selectFields);
            } else {
                call = enrollmentApi.getEnrollments("eq." + userId, selectFields);
            }
            executeCall(call, resultLiveData);
        }

        return resultLiveData;
    }
}

