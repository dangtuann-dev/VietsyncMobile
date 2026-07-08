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
                if (courseList == null || courseList.isEmpty()) {
                    isLastPage = true;
                    if (currentPage == 1) {
                        courses.postValue(new ArrayList<>());
                    }
                    return;
                }

                if (courseList.size() < PAGE_SIZE) {
                    isLastPage = true;
                }

                accumulatedCourses.addAll(courseList);
                courses.postValue(new ArrayList<>(accumulatedCourses));
                currentPage++;
            }

            @Override
            public void onError(String errorMsg) {
                isLoading.postValue(false);
                errorMessage.postValue(errorMsg);
            }
        });
    }
}
