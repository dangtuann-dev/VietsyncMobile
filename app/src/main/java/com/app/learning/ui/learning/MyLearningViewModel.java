package com.app.learning.ui.learning;

import android.content.Context;
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

    public LiveData<Resource<List<Enrollment>>> loadEnrolledCourses(Context context, String status) {
        String activeUserId = resolveActiveUserId();
        MutableLiveData<Resource<List<Enrollment>>> resultLiveData = new MutableLiveData<>();
        resultLiveData.setValue(Resource.loading());

        enrollmentRepository.loadEnrolledCourses(activeUserId, status).observeForever(resource -> {
            if (resource != null && resource.status == Resource.Status.SUCCESS && resource.data != null && !resource.data.isEmpty()) {
                resultLiveData.setValue(resource);
            } else if (resource != null && resource.status == Resource.Status.LOADING) {
                resultLiveData.setValue(Resource.loading());
            } else {
                List<Enrollment> fallback = getMockEnrollments(context, status, activeUserId);
                resultLiveData.setValue(Resource.success(fallback));
            }
        });
        return resultLiveData;
    }

    public LiveData<Resource<List<Enrollment>>> loadEnrolledCourses(String status) {
        return loadEnrolledCourses(null, status);
    }

    private List<Enrollment> getMockEnrollments(Context context, String status, String activeUserId) {
        List<Enrollment> list = new ArrayList<>();

        com.app.learning.data.model.Course c1 = new com.app.learning.data.model.Course();
        c1.setId("c0eebc99-9c0b-4ef8-bb6d-6bb9bd380001");
        c1.setTitle("Lập trình Android với Java (MVVM)");
        c1.setDescription("Khóa học từ cơ bản đến nâng cao về phát triển ứng dụng di động Android dùng Java, cấu trúc MVVM và tích hợp Supabase.");
        c1.setThumbnail("https://images.unsplash.com/photo-1607799279861-4dd421887fb3?w=400");
        c1.setLevel("intermediate");
        c1.setDuration(1200);
        c1.setRating(4.85);
        c1.setPrice(0);

        com.app.learning.data.model.Course c2 = new com.app.learning.data.model.Course();
        c2.setId("c0eebc99-9c0b-4ef8-bb6d-6bb9bd380002");
        c2.setTitle("UI/UX Design chuyên nghiệp");
        c2.setDescription("Làm chủ thiết kế giao diện Figma, nghiên cứu người dùng và tối ưu trải nghiệm thiết kế cho Mobile & Web.");
        c2.setThumbnail("https://images.unsplash.com/photo-1561070791-26c113006238?w=400");
        c2.setLevel("beginner");
        c2.setDuration(900);
        c2.setRating(4.90);
        c2.setPrice(0);

        com.app.learning.data.model.Course c3 = new com.app.learning.data.model.Course();
        c3.setId("c0eebc99-9c0b-4ef8-bb6d-6bb9bd380003");
        c3.setTitle("Lập trình Android nâng cao với Kotlin");
        c3.setDescription("Xây dựng ứng dụng Android hiện đại với Jetpack, Coroutines, Flow và Clean Architecture.");
        c3.setThumbnail("https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=400");
        c3.setLevel("advanced");
        c3.setDuration(1500);
        c3.setRating(4.90);
        c3.setPrice(0);

        java.util.Set<String> removedSavedIds = (context != null)
                ? com.app.learning.utils.UserPreference.getInstance(context).getRemovedSavedSet()
                : new java.util.HashSet<>();

        if ("completed".equalsIgnoreCase(status)) {
            Enrollment e1 = new Enrollment();
            e1.setUserId(activeUserId);
            e1.setCourseId(c2.getId());
            e1.setCourse(c2);
            e1.setProgressPercent(100);
            list.add(e1);
        } else if ("saved".equalsIgnoreCase(status)) {
            if (!removedSavedIds.contains(c3.getId())) {
                Enrollment e1 = new Enrollment();
                e1.setUserId(activeUserId);
                e1.setCourseId(c3.getId());
                e1.setCourse(c3);
                e1.setProgressPercent(0);
                list.add(e1);
            }
        } else {
            Enrollment e1 = new Enrollment();
            e1.setUserId(activeUserId);
            e1.setCourseId(c1.getId());
            e1.setCourse(c1);
            e1.setProgressPercent(50);
            list.add(e1);

            Enrollment e2 = new Enrollment();
            e2.setUserId(activeUserId);
            e2.setCourseId(c3.getId());
            e2.setCourse(c3);
            e2.setProgressPercent(25);
            list.add(e2);
        }
        return list;
    }

    public LiveData<Resource<List<Lesson>>> calculateProgress(String courseId) {
        return courseRepository.getLessons(courseId);
    }

    public LiveData<Resource<Void>> enrollInCourse(String courseId) {
        String activeUserId = resolveActiveUserId();
        return enrollmentRepository.enrollInCourse(activeUserId, courseId);
    }

    public LiveData<Resource<Void>> removeFromWishlist(String courseId) {
        String activeUserId = resolveActiveUserId();
        return wishlistRepository.removeFromWishlist(activeUserId, courseId);
    }

    private String resolveActiveUserId() {
        if (userId != null && !userId.trim().isEmpty()) {
            return userId;
        }
        return "e1a46cf7-8d00-4b2a-89a1-5d9f00000004";
    }
}

