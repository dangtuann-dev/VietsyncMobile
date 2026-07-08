package com.app.learning.data.repository;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.app.learning.data.api.ApiClient;
import com.app.learning.data.api.Resource;
import com.app.learning.data.api.WishlistApi;
import com.app.learning.data.model.WishlistModel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;

public class WishlistRepository extends BaseRepository {

    private final WishlistApi wishlistApi;

    public WishlistRepository() {
        super();
        this.wishlistApi = ApiClient.getInstance().createService(WishlistApi.class);
    }

    public WishlistRepository(@NonNull Context context) {
        this();
    }

    public LiveData<Resource<List<WishlistModel>>> getWishlist(String userId) {
        MutableLiveData<Resource<List<WishlistModel>>> resultLiveData = new MutableLiveData<>();
        Call<List<WishlistModel>> call = wishlistApi.getWishlist("eq." + userId, "*,course:courses(*,instructor:users(full_name))");
        executeCall(call, resultLiveData);
        return resultLiveData;
    }

    public LiveData<Resource<Void>> addToWishlist(String userId, String courseId) {
        MutableLiveData<Resource<Void>> resultLiveData = new MutableLiveData<>();
        WishlistModel wishlist = new WishlistModel(userId, courseId);
        Call<Void> call = wishlistApi.addToWishlist(wishlist);
        executeCall(call, resultLiveData);
        return resultLiveData;
    }

    public LiveData<Resource<Void>> removeFromWishlist(String userId, String courseId) {
        MutableLiveData<Resource<Void>> resultLiveData = new MutableLiveData<>();
        Call<Void> call = wishlistApi.removeFromWishlist("eq." + userId, "eq." + courseId);
        executeCall(call, resultLiveData);
        return resultLiveData;
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
