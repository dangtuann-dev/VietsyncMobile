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
            if (resource != null) {
                courseDetail.setValue(resource);
                if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                    loadLessons(courseId);
                }
            }
        });
    }

    private void loadLessons(String courseId) {
        courseRepository.getLessons(courseId).observeForever(resource -> {
            if (resource != null) {
                courseLessons.setValue(resource);
            }
        });
    }

    public void checkEnrollment() {
        if (userId == null || courseId == null) {
            enrollmentStatus.setValue(Resource.success(false));
            return;
        }
        enrollmentStatus.setValue(Resource.loading());
        courseRepository.checkEnrollment(userId, courseId).observeForever(resource -> {
            if (resource != null) {
                enrollmentStatus.setValue(resource);
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
