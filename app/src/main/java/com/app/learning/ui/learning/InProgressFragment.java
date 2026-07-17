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

public class InProgressFragment extends BaseFragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rvCourses;
    private View layoutEmptyState;
    private ProgressBar progressBar;
    private InProgressAdapter adapter;
    private MyLearningViewModel viewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_in_progress;
    }

    @Override
    protected void initViews(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        rvCourses = view.findViewById(R.id.rv_courses);
        layoutEmptyState = view.findViewById(R.id.layout_empty_state);
        progressBar = view.findViewById(R.id.progress_bar);

        rvCourses.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new InProgressAdapter(getViewLifecycleOwner(), new InProgressAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Enrollment enrollment) {
                openCourseDetail(enrollment.getCourse());
            }

            @Override
            public void onContinueClick(Enrollment enrollment) {
                openCourseDetail(enrollment.getCourse());
            }
        });
        rvCourses.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(this::loadCourses);
    }

    @Override
    protected void initObservers() {
        // Share viewmodel with the parent fragment (MyLearningFragment)
        viewModel = new ViewModelProvider(requireParentFragment()).get(MyLearningViewModel.class);
        loadCourses();
    }

    private void loadCourses() {
        viewModel.loadEnrolledCourses("in_progress").observe(getViewLifecycleOwner(), resource -> {
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
                        showError(resource.error != null ? resource.error.getMessage() : "Lỗi tải khóa học đang học");
                        break;
                }
            }
        });
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
