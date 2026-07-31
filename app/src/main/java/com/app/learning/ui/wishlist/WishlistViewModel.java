package com.app.learning.ui.wishlist;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.app.learning.data.api.Resource;
import com.app.learning.data.model.WishlistModel;
import com.app.learning.data.repository.WishlistRepository;
import com.app.learning.utils.UserPreference;

import java.util.List;

public class WishlistViewModel extends AndroidViewModel {

    private final WishlistRepository repository;
    private final UserPreference userPreference;

    private final MediatorLiveData<Resource<List<WishlistModel>>> wishlistLiveData = new MediatorLiveData<>();
    private final MutableLiveData<Resource<Void>> actionResultLiveData = new MutableLiveData<>();
    private final MutableLiveData<Resource<Boolean>> isWishlistedLiveData = new MutableLiveData<>();

    public WishlistViewModel(@NonNull Application application) {
        super(application);
        this.repository = new WishlistRepository(application);
        this.userPreference = UserPreference.getInstance(application);
    }

    public LiveData<Resource<List<WishlistModel>>> getWishlistLiveData() {
        return wishlistLiveData;
    }

    public LiveData<Resource<Void>> getActionResultLiveData() {
        return actionResultLiveData;
    }

    public LiveData<Resource<Boolean>> getIsWishlistedLiveData() {
        return isWishlistedLiveData;
    }

    public void loadWishlists() {
        String userId = getCurrentUserId();
        if (userId != null) {
            LiveData<Resource<List<WishlistModel>>> source = repository.getWishlist(userId);
            wishlistLiveData.addSource(source, resource -> {
                wishlistLiveData.setValue(resource);
                if (resource.status != Resource.Status.LOADING) {
                    wishlistLiveData.removeSource(source);
                }
            });
        } else {
            wishlistLiveData.setValue(Resource.error(new com.app.learning.data.api.ApiError("401", "Chưa đăng nhập", null, null)));
        }
    }

    public void removeFromWishlist(String courseId) {
        if (courseId != null) {
            userPreference.removeWishlistId(courseId);
            isWishlistedLiveData.setValue(Resource.success(false));
            actionResultLiveData.setValue(Resource.success(null));
            String userId = getCurrentUserId();
            if (userId != null) {
                repository.removeFromWishlist(userId, courseId);
            }
        }
    }

    public void addToWishlist(String courseId) {
        if (courseId != null) {
            userPreference.addWishlistId(courseId);
            isWishlistedLiveData.setValue(Resource.success(true));
            actionResultLiveData.setValue(Resource.success(null));
            String userId = getCurrentUserId();
            if (userId != null) {
                repository.addToWishlist(userId, courseId);
            }
        }
    }

    public void checkWishlistStatus(String courseId) {
        if (courseId == null) {
            isWishlistedLiveData.setValue(Resource.success(false));
            return;
        }
        boolean isWishlistedLocal = userPreference.isWishlisted(courseId);
        isWishlistedLiveData.setValue(Resource.success(isWishlistedLocal));

        String userId = getCurrentUserId();
        if (userId != null) {
            repository.getWishlist(userId).observeForever(resource -> {
                if (resource != null && resource.status == Resource.Status.SUCCESS && resource.data != null) {
                    boolean found = false;
                    for (WishlistModel item : resource.data) {
                        if (item.getCourseId() != null && item.getCourseId().equals(courseId)) {
                            found = true;
                            userPreference.addWishlistId(courseId);
                            break;
                        }
                    }
                    isWishlistedLiveData.setValue(Resource.success(found || isWishlistedLocal));
                }
            });
        }
    }

    public void enrollFromWishlist(String courseId) {
        String userId = getCurrentUserId();
        if (userId != null) {
            actionResultLiveData.setValue(Resource.loading());
            repository.enrollInCourse(userId, courseId).observeForever(result -> {
                if (result.status == Resource.Status.SUCCESS) {
                    repository.removeFromWishlist(userId, courseId).observeForever(removeResult -> {
                        actionResultLiveData.setValue(Resource.success(null));
                        loadWishlists();
                    });
                } else if (result.status == Resource.Status.ERROR) {
                    actionResultLiveData.setValue(Resource.error(result.error));
                }
            });
        }
    }

    private String getCurrentUserId() {
        return userPreference.getUserProfile() != null ? userPreference.getUserProfile().getId() : null;
    }
}
