package com.app.learning.ui.learning;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.app.learning.data.api.Resource;
import com.app.learning.data.model.Enrollment;
import com.app.learning.data.model.Lesson;
import com.app.learning.data.repository.CourseRepository;
import com.app.learning.data.repository.EnrollmentRepository;
import com.app.learning.data.repository.WishlistRepository;
import com.app.learning.ui.base.BaseViewModel;
import java.util.ArrayList;
import java.util.List;

public class MyLearningViewModel extends BaseViewModel {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final WishlistRepository wishlistRepository;
    private String userId;

    public MyLearningViewModel() {
        this.enrollmentRepository = new EnrollmentRepository();
        this.courseRepository = new CourseRepository();
        this.wishlistRepository = new WishlistRepository();
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public LiveData<Resource<List<Enrollment>>> loadEnrolledCourses(String status) {
        if (userId == null || userId.trim().isEmpty()) {
            MutableLiveData<Resource<List<Enrollment>>> emptyResult = new MutableLiveData<>();
            emptyResult.setValue(Resource.success(new ArrayList<>()));
            return emptyResult;
        }
        return enrollmentRepository.loadEnrolledCourses(userId, status);
    }

    public LiveData<Resource<List<Lesson>>> calculateProgress(String courseId) {
        return courseRepository.getLessons(courseId);
    }

    public LiveData<Resource<Void>> enrollInCourse(String courseId) {
        if (userId == null || userId.trim().isEmpty()) {
            MutableLiveData<Resource<Void>> errorResult = new MutableLiveData<>();
            errorResult.setValue(Resource.error(new com.app.learning.data.api.ApiError("401", "Chưa đăng nhập", null, null)));
            return errorResult;
        }
        return enrollmentRepository.enrollInCourse(userId, courseId);
    }

    public LiveData<Resource<Void>> removeFromWishlist(String courseId) {
        if (userId == null || userId.trim().isEmpty()) {
            MutableLiveData<Resource<Void>> errorResult = new MutableLiveData<>();
            errorResult.setValue(Resource.error(new com.app.learning.data.api.ApiError("401", "Chưa đăng nhập", null, null)));
            return errorResult;
        }
        return wishlistRepository.removeFromWishlist(userId, courseId);
    }
}

