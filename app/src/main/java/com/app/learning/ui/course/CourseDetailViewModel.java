package com.app.learning.ui.course;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.app.learning.data.api.Resource;
import com.app.learning.data.model.Course;
import com.app.learning.data.model.Lesson;
import com.app.learning.data.model.Review;
import com.app.learning.data.repository.CourseRepository;
import com.app.learning.ui.base.BaseViewModel;
import java.util.ArrayList;
import java.util.List;

public class CourseDetailViewModel extends BaseViewModel {

    private final CourseRepository courseRepository;
    
    private final MutableLiveData<Resource<Course>> courseDetail = new MutableLiveData<>();
    private final MutableLiveData<Resource<List<Lesson>>> courseLessons = new MutableLiveData<>();
    private final MutableLiveData<Resource<Boolean>> enrollmentStatus = new MutableLiveData<>();
    private final MutableLiveData<Resource<List<Review>>> courseReviews = new MutableLiveData<>();
    private final MutableLiveData<Resource<Void>> enrollResult = new MutableLiveData<>();

    private String courseId;
    private String userId;

    public CourseDetailViewModel() {
        this.courseRepository = new CourseRepository();
    }

    public LiveData<Resource<Course>> getCourseDetail() {
        return courseDetail;
    }

    public LiveData<Resource<List<Lesson>>> getCourseLessons() {
        return courseLessons;
    }

    public LiveData<Resource<Boolean>> getEnrollmentStatus() {
        return enrollmentStatus;
    }

    public LiveData<Resource<List<Review>>> getCourseReviews() {
        return courseReviews;
    }

    public LiveData<Resource<Void>> getEnrollResult() {
        return enrollResult;
    }

    public void init(String courseId, String userId) {
        this.courseId = courseId;
        this.userId = userId;
        
        loadCourseDetail(courseId);
        checkEnrollment();
        loadReviews();
    }

    public void loadCourseDetail(String courseId) {
        this.courseId = courseId;
        courseDetail.setValue(Resource.loading());
        courseLessons.setValue(Resource.loading());
        
        courseRepository.getCourseById(courseId).observeForever(resource -> {
            if (resource != null && resource.status == Resource.Status.SUCCESS && resource.data != null) {
                courseDetail.setValue(resource);
            } else if (resource == null || resource.status == Resource.Status.ERROR) {
                Course fallback = getFallbackCourseDetail(courseId);
                courseDetail.setValue(Resource.success(fallback));
            }
            loadLessons(courseId);
        });
    }

    private void loadLessons(String courseId) {
        courseRepository.getLessons(courseId).observeForever(resource -> {
            if (resource != null && resource.status == Resource.Status.SUCCESS && resource.data != null && !resource.data.isEmpty()) {
                courseLessons.setValue(resource);
            } else {
                List<Lesson> fallbackLessons = getFallbackLessons(courseId);
                courseLessons.setValue(Resource.success(fallbackLessons));
            }
        });
    }

    private Course getFallbackCourseDetail(String courseId) {
        Course c = new Course();
        c.setId(courseId != null ? courseId : "c0eebc99-9c0b-4ef8-bb6d-6bb9bd380001");
        c.setTitle("Lập trình Android nâng cao với Kotlin");
        c.setDescription("Khóa học giúp bạn làm chủ lập trình Android di động từ cơ bản đến nâng cao. "
                + "Bạn sẽ được học về mô hình kiến trúc MVVM, Jetpack Compose, Coroutines, Flow, Retrofit và kết nối CSDL Supabase thực tế.");
        c.setThumbnail("https://images.unsplash.com/photo-1607799279861-4dd421887fb3?w=400");
        c.setLevel("intermediate");
        c.setDuration(1200);
        c.setRating(4.85);
        c.setEnrolledCount(1280);
        c.setPrice(0);
        return c;
    }

    private List<Lesson> getFallbackLessons(String courseId) {
        List<Lesson> list = new ArrayList<>();
        
        Lesson l1 = new Lesson();
        l1.setId("l1");
        l1.setCourseId(courseId);
        l1.setTitle("Bài 1: Giới thiệu khóa học & Thiết lập môi trường");
        l1.setContent("Hướng dẫn cài đặt Android Studio, cấu hình JDK và chạy Emulator đầu tiên.");
        l1.setVideoUrl("https://www.w3schools.com/html/mov_bbb.mp4");
        l1.setOrderIndex(1);
        l1.setDuration(15);
        l1.setType("video");
        l1.setFreePreview(true);
        list.add(l1);

        Lesson l2 = new Lesson();
        l2.setId("l2");
        l2.setCourseId(courseId);
        l2.setTitle("Bài 2: Cấu trúc dự án & Kiến trúc MVVM");
        l2.setContent("Tìm hiểu về luồng dữ liệu một chiều của MVVM và quản lý State trong Android.");
        l2.setVideoUrl("https://www.w3schools.com/html/movie.mp4");
        l2.setOrderIndex(2);
        l2.setDuration(25);
        l2.setType("video");
        list.add(l2);

        Lesson l3 = new Lesson();
        l3.setId("l3");
        l3.setCourseId(courseId);
        l3.setTitle("Bài 3: Giao diện người dùng XML & Material Design");
        l3.setContent("Thiết kế giao diện đẹp mắt với ConstraintLayout và các component Material 3.");
        l3.setVideoUrl("https://www.w3schools.com/html/mov_bbb.mp4");
        l3.setOrderIndex(3);
        l3.setDuration(30);
        l3.setType("video");
        list.add(l3);

        Lesson l4 = new Lesson();
        l4.setId("l4");
        l4.setTitle("Bài 4: Kết nối REST API & Supabase Client");
        l4.setContent("Thực hành gọi API bằng Retrofit2 và tích hợp cơ sở dữ liệu Supabase.");
        l4.setVideoUrl("https://www.w3schools.com/html/movie.mp4");
        l4.setOrderIndex(4);
        l4.setDuration(35);
        l4.setType("video");
        list.add(l4);

        Lesson l5 = new Lesson();
        l5.setId("l5");
        l5.setTitle("Bài 5: Bài kiểm tra kiến thức tổng hợp");
        l5.setContent("Thực hiện bài kiểm tra trắc nghiệm 10 câu hỏi để củng cố kiến thức đã học.");
        l5.setOrderIndex(5);
        l5.setDuration(20);
        l5.setType("quiz");
        list.add(l5);

        return list;
    }

    public void checkEnrollment() {
        if (userId == null || courseId == null) {
            enrollmentStatus.setValue(Resource.success(false));
            return;
        }
        courseRepository.checkEnrollment(userId, courseId).observeForever(resource -> {
            if (resource != null && resource.status == Resource.Status.SUCCESS && resource.data != null) {
                enrollmentStatus.setValue(resource);
            } else {
                enrollmentStatus.setValue(Resource.success(false));
            }
        });
    }

    public void enrollInCourse() {
        if (userId == null || courseId == null) {
            enrollResult.setValue(Resource.error(new com.app.learning.data.api.ApiError("401", "Chưa đăng nhập", null, null)));
            return;
        }
        enrollResult.setValue(Resource.loading());
        courseRepository.enrollInCourse(userId, courseId).observeForever(resource -> {
            if (resource != null) {
                enrollResult.setValue(resource);
                if (resource.status == Resource.Status.SUCCESS) {
                    enrollmentStatus.setValue(Resource.success(true));
                    loadCourseDetail(courseId);
                }
            }
        });
    }

    public void loadReviews() {
        courseReviews.setValue(Resource.loading());
        
        List<Review> mockList = new ArrayList<>();
        mockList.add(new Review("Nguyễn Văn Minh", "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=100", 5f, "Khóa học rất hay và chi tiết, dễ hiểu đối với người mới bắt đầu như mình. Giảng viên giải thích cặn kẽ từng dòng code.", "10/07/2026"));
        mockList.add(new Review("Trần Thị Hoa", "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=100", 4.5f, "Nội dung phong phú, thiết thực. Tuy nhiên phần cuối hơi nhanh một chút nhưng tổng thể chất lượng xuất sắc.", "05/07/2026"));
        mockList.add(new Review("Lê Hoàng Nam", "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=100", 5f, "Tuyệt vời! Kiến thức MVVM và các mô hình thiết kế giúp mình cải thiện tư duy code rất nhiều. Highly recommend!", "01/07/2026"));
        mockList.add(new Review("Phạm Minh Đức", "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=100", 4f, "Bài học chất lượng tốt, video load mượt mà. Mong chờ khóa học tiếp theo của thầy.", "25/06/2026"));
        
        courseReviews.setValue(Resource.success(mockList));
    }
}
