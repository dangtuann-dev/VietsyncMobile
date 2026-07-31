package com.app.learning.ui.learning;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.app.learning.data.api.Resource;
import com.app.learning.data.model.Course;
import com.app.learning.data.model.Enrollment;
import com.app.learning.ui.base.BaseFragment;
import com.example.vietsyncmobile.R;
import java.util.List;

public class SavedFragment extends BaseFragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rvCourses;
    private View layoutEmptyState;
    private ProgressBar progressBar;
    private SavedAdapter adapter;
    private MyLearningViewModel viewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_saved;
    }

    @Override
    protected void initViews(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        rvCourses = view.findViewById(R.id.rv_courses);
        layoutEmptyState = view.findViewById(R.id.layout_empty_state);
        progressBar = view.findViewById(R.id.progress_bar);

        rvCourses.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new SavedAdapter(new SavedAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Enrollment enrollment) {
                openCourseDetail(enrollment.getCourse());
            }

            @Override
            public void onRemoveClick(Enrollment enrollment) {
                removeCourse(enrollment.getCourseId());
            }

            @Override
            public void onEnrollClick(Enrollment enrollment) {
                enrollCourse(enrollment.getCourseId(), enrollment.getCourse());
            }
        });
        rvCourses.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(this::loadCourses);
    }

    @Override
    protected void initObservers() {
        viewModel = new ViewModelProvider(requireParentFragment()).get(MyLearningViewModel.class);
        loadCourses();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCourses();
    }

    private void loadCourses() {
        viewModel.loadEnrolledCourses(requireContext(), "saved").observe(getViewLifecycleOwner(), resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case LOADING:
                        if (!swipeRefreshLayout.isRefreshing()) {
                            progressBar.setVisibility(View.VISIBLE);
                        }
                        layoutEmptyState.setVisibility(View.GONE);
                        break;
                    case SUCCESS:
                        progressBar.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                        List<Enrollment> data = resource.data;
                        if (data == null || data.isEmpty()) {
                            adapter.setItems(null);
                            layoutEmptyState.setVisibility(View.VISIBLE);
                            rvCourses.setVisibility(View.GONE);
                        } else {
                            adapter.setItems(data);
                            layoutEmptyState.setVisibility(View.GONE);
                            rvCourses.setVisibility(View.VISIBLE);
                        }
                        break;
                    case ERROR:
                        progressBar.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                        showError(resource.error != null ? resource.error.getMessage() : "Lỗi tải khóa học đã lưu");
                        break;
                }
            }
        });
    }

    private void removeCourse(String courseId) {
        showLoading("Đang gỡ bỏ...");
        com.app.learning.utils.UserPreference pref = com.app.learning.utils.UserPreference.getInstance(requireContext());
        pref.removeWishlistId(courseId);
        pref.addRemovedSavedId(courseId);

        try {
            viewModel.removeFromWishlist(courseId);
        } catch (Exception ignored) {}

        hideLoading();
        showToast("Đã gỡ khỏi danh sách lưu!");
        loadCourses();
    }

    private void enrollCourse(String courseId, Course course) {
        showLoading("Đang đăng ký học...");
        com.app.learning.utils.UserPreference pref = com.app.learning.utils.UserPreference.getInstance(requireContext());
        pref.addEnrolledCourseId(courseId);
        pref.removeWishlistId(courseId);
        pref.addRemovedSavedId(courseId);

        try {
            viewModel.enrollInCourse(courseId);
            viewModel.removeFromWishlist(courseId);
        } catch (Exception ignored) {}

        hideLoading();
        showToast("Đăng ký thành công! Bắt đầu học...");

        try {
            Intent intent = new Intent(requireContext(), com.app.learning.ui.learning.LessonPlayerActivity.class);
            intent.putExtra(com.app.learning.ui.learning.LessonPlayerActivity.EXTRA_COURSE_ID, courseId);
            intent.putExtra(com.app.learning.ui.learning.LessonPlayerActivity.EXTRA_LESSON_TITLE, course != null ? course.getTitle() : "Bài 1: Giới thiệu");
            startActivity(intent);
        } catch (Exception e) {
            loadCourses();
        }
    }

    private void openCourseDetail(Course course) {
        if (course == null) return;
        try {
            Intent intent = new Intent(requireActivity(), Class.forName("com.app.learning.ui.course.CourseDetailActivity"));
            intent.putExtra("course", course);
            intent.putExtra("course_id", course.getId());
            startActivity(intent);
        } catch (ClassNotFoundException e) {
            Toast.makeText(requireContext(), "Mở khóa học: " + course.getTitle(), Toast.LENGTH_SHORT).show();
        }
    }
}
