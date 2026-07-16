package com.app.learning.data.repository;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.app.learning.data.api.ApiClient;
import com.app.learning.data.api.Resource;
import com.app.learning.data.api.WishlistApi;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;

public class EnrollmentRepository extends BaseRepository {

    private final WishlistApi wishlistApi;

    public EnrollmentRepository() {
        super();
        this.wishlistApi = ApiClient.getInstance().createService(WishlistApi.class);
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
}
