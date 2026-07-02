package com.app.learning.ui.course;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.app.learning.data.model.Course;
import com.app.learning.data.repository.CourseRepository;

import java.util.List;

public class CourseListViewModel extends ViewModel {

    private final CourseRepository courseRepository;
    private final MutableLiveData<List<Course>> courses = new MutableLiveData<>();

    public CourseListViewModel() {
        this.courseRepository = new CourseRepository(); // Instantiate your repository
    }

    public LiveData<List<Course>> getCourses() {
        return courses;
    }

    public void loadCourses(String categoryId, String sortBy, int page) {
        // You would typically use LiveData or coroutines here for async operations
        // For simplicity, we'll simulate an async call
        courseRepository.getCoursesByCategory(categoryId, sortBy, page, new CourseRepository.CourseListCallback() {
            @Override
            public void onSuccess(List<Course> courseList) {
                courses.postValue(courseList);
            }

            @Override
            public void onError(String errorMessage) {
                // Handle error, e.g., show a Toast or log
            }
        });
    }
}
