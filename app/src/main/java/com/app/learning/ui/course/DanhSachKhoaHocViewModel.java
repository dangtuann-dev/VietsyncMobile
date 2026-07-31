package com.app.learning.ui.course;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.app.learning.data.model.Course;
import com.app.learning.data.repository.CourseRepository;
import java.util.ArrayList;
import java.util.List;

public class DanhSachKhoaHocViewModel extends ViewModel {

    private final CourseRepository courseRepository;
    private final MutableLiveData<List<Course>> courses = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final List<Course> accumulatedCourses = new ArrayList<>();

    private int currentPage = 1;
    private boolean isLastPage = false;
    private static final int PAGE_SIZE = 20;

    public DanhSachKhoaHocViewModel() {
        this.courseRepository = new CourseRepository();
    }

    public LiveData<List<Course>> layDanhSachKhoaHoc() {
        return courses;
    }

    public LiveData<Boolean> layTrangThaiDangTai() {
        return isLoading;
    }

    public LiveData<String> layThongBaoLoi() {
        return errorMessage;
    }

    public boolean laTrangCuoi() {
        return isLastPage;
    }

    public void taiKhoaHoc(String categoryId, String sortBy, boolean reset) {
        if (isLoading.getValue() != null && isLoading.getValue()) return;
        if (!reset && isLastPage) return;

        if (reset) {
            currentPage = 1;
            isLastPage = false;
            accumulatedCourses.clear();
            courses.setValue(new ArrayList<>());
        }

        isLoading.setValue(true);
        errorMessage.setValue(null);

        courseRepository.getCoursesByCategory(categoryId, sortBy, currentPage, new CourseRepository.CourseListCallback() {
            @Override
            public void onSuccess(List<Course> courseList) {
                isLoading.postValue(false);
                List<Course> result = courseList;
                if (result == null || result.isEmpty()) {
                    isLastPage = true;
                    if (currentPage == 1) {
                        result = getMockCoursesForCategory(categoryId);
                        accumulatedCourses.addAll(result);
                        courses.postValue(new ArrayList<>(accumulatedCourses));
                    }
                    return;
                }

                if (result.size() < PAGE_SIZE) {
                    isLastPage = true;
                }

                accumulatedCourses.addAll(result);
                courses.postValue(new ArrayList<>(accumulatedCourses));
                currentPage++;
            }

            @Override
            public void onError(String errorMsg) {
                isLoading.postValue(false);
                if (currentPage == 1) {
                    List<Course> result = getMockCoursesForCategory(categoryId);
                    accumulatedCourses.addAll(result);
                    courses.postValue(new ArrayList<>(accumulatedCourses));
                    isLastPage = true;
                } else {
                    errorMessage.postValue(errorMsg);
                }
            }
        });
    }

    private List<Course> getMockCoursesForCategory(String categoryId) {
        List<Course> list = new ArrayList<>();

        Course c1 = new Course();
        c1.setId("c0eebc99-9c0b-4ef8-bb6d-6bb9bd380001");
        c1.setTitle("Lập trình Android với Java (MVVM)");
        c1.setDescription("Khóa học từ cơ bản đến nâng cao về phát triển ứng dụng di động Android dùng Java, cấu trúc MVVM và tích hợp Supabase.");
        c1.setThumbnail("https://images.unsplash.com/photo-1607799279861-4dd421887fb3?w=400");
        c1.setCategoryId(1L);
        c1.setLevel("intermediate");
        c1.setDuration(1200);
        c1.setRating(4.85);
        c1.setPrice(0);

        Course c2 = new Course();
        c2.setId("c0eebc99-9c0b-4ef8-bb6d-6bb9bd380002");
        c2.setTitle("UI/UX Design chuyên nghiệp");
        c2.setDescription("Làm chủ thiết kế giao diện Figma, nghiên cứu người dùng và tối ưu trải nghiệm thiết kế cho Mobile & Web.");
        c2.setThumbnail("https://images.unsplash.com/photo-1561070791-26c113006238?w=400");
        c2.setCategoryId(3L);
        c2.setLevel("beginner");
        c2.setDuration(900);
        c2.setRating(4.90);
        c2.setPrice(0);

        Course c3 = new Course();
        c3.setId("c0eebc99-9c0b-4ef8-bb6d-6bb9bd380003");
        c3.setTitle("Lập trình Android nâng cao với Kotlin");
        c3.setDescription("Xây dựng ứng dụng Android hiện đại với Jetpack, Coroutines, Flow và Clean Architecture.");
        c3.setThumbnail("https://images.unsplash.com/photo-1555066931-4365d14bab8c?w=400");
        c3.setCategoryId(1L);
        c3.setLevel("advanced");
        c3.setDuration(1500);
        c3.setRating(4.90);
        c3.setPrice(0);

        Course c4 = new Course();
        c4.setId("c0eebc99-9c0b-4ef8-bb6d-6bb9bd380004");
        c4.setTitle("Tiếng Anh giao tiếp công sở & CNTT");
        c4.setDescription("Nâng cao khả năng giao tiếp tiếng Anh chuyên ngành công nghệ thông tin và môi trường doanh nghiệp.");
        c4.setThumbnail("https://images.unsplash.com/photo-1434030216411-0b793f4b4173?w=400");
        c4.setCategoryId(4L);
        c4.setLevel("beginner");
        c4.setDuration(800);
        c4.setRating(4.75);
        c4.setPrice(0);

        Course c5 = new Course();
        c5.setId("c0eebc99-9c0b-4ef8-bb6d-6bb9bd380005");
        c5.setTitle("Khởi nghiệp Thực chiến & Quản trị Kinh doanh");
        c5.setDescription("Kiến thức quản trị kinh doanh toàn diện, xây dựng mô hình kinh doanh và chiến lược phát triển.");
        c5.setThumbnail("https://images.unsplash.com/photo-1531403009284-440f080d1e12?w=400");
        c5.setCategoryId(2L);
        c5.setLevel("intermediate");
        c5.setDuration(1100);
        c5.setRating(4.80);
        c5.setPrice(0);

        if (categoryId == null || categoryId.trim().isEmpty() || "0".equals(categoryId)) {
            list.add(c1); list.add(c2); list.add(c3); list.add(c4); list.add(c5);
            return list;
        }

        long catIdLong;
        try {
            catIdLong = Long.parseLong(categoryId);
        } catch (NumberFormatException e) {
            String lower = categoryId.toLowerCase();
            if (lower.contains("công nghệ")) catIdLong = 1L;
            else if (lower.contains("kinh doanh")) catIdLong = 2L;
            else if (lower.contains("thiết kế")) catIdLong = 3L;
            else if (lower.contains("ngôn ngữ") || lower.contains("ngoại ngữ")) catIdLong = 4L;
            else catIdLong = 1L;
        }

        for (Course c : new Course[]{c1, c2, c3, c4, c5}) {
            if (c.getCategoryId() != null && c.getCategoryId() == catIdLong) {
                list.add(c);
            }
        }

        if (list.isEmpty()) {
            list.add(c1);
            list.add(c2);
        }
        return list;
    }
}
