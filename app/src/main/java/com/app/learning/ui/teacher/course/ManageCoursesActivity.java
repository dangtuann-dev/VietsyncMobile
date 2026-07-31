package com.app.learning.ui.teacher.course;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.learning.data.model.Course;
import com.app.learning.data.model.User;
import com.app.learning.data.repository.TeacherCourseRepository;
import com.app.learning.utils.UserPreference;
import com.example.vietsyncmobile.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ManageCoursesActivity extends AppCompatActivity implements ManageCourseAdapter.OnCourseActionListener {

    private RecyclerView recyclerViewCourses;
    private ManageCourseAdapter adapter;
    private ProgressBar progressBar;
    private LinearLayout layoutEmptyState;
    private TextView txtCourseCount;
    private ExtendedFloatingActionButton fabAddCourse;

    private TeacherCourseRepository repository;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_courses);

        repository = new TeacherCourseRepository(this);
        currentUser = UserPreference.getInstance(this).getUserProfile();

        initViews();
        loadCourses();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCourses();
    }

    private void initViews() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        recyclerViewCourses = findViewById(R.id.recyclerViewCourses);
        progressBar = findViewById(R.id.progressBar);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        txtCourseCount = findViewById(R.id.txtCourseCount);
        fabAddCourse = findViewById(R.id.fabAddCourse);

        recyclerViewCourses.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ManageCourseAdapter(this);
        recyclerViewCourses.setAdapter(adapter);

        fabAddCourse.setOnClickListener(v -> {
            Intent intent = new Intent(this, CreateCourseActivity.class);
            startActivity(intent);
        });
    }

    private void loadCourses() {
        if (currentUser == null) return;
        
        progressBar.setVisibility(View.VISIBLE);
        layoutEmptyState.setVisibility(View.GONE);
        recyclerViewCourses.setVisibility(View.GONE);

        repository.getCoursesForInstructor(currentUser.getId()).observe(this, resource -> {
            if (resource.isLoading()) return;
            
            progressBar.setVisibility(View.GONE);
            if (resource.isSuccess() && resource.data != null) {
                List<Course> courses = resource.data;
                txtCourseCount.setText(courses.size() + " khóa học");
                
                if (courses.isEmpty()) {
                    layoutEmptyState.setVisibility(View.VISIBLE);
                    recyclerViewCourses.setVisibility(View.GONE);
                } else {
                    layoutEmptyState.setVisibility(View.GONE);
                    recyclerViewCourses.setVisibility(View.VISIBLE);
                    adapter.setCourses(courses);
                }
            } else {
                Toast.makeText(this, "Lỗi khi tải danh sách khóa học", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onEdit(Course course) {
        Intent intent = new Intent(this, EditCourseActivity.class);
        intent.putExtra("COURSE", course);
        startActivity(intent);
    }

    @Override
    public void onDelete(Course course) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa khóa học")
                .setMessage("Bạn có chắc chắn muốn xóa khóa học '" + course.getTitle() + "' không? Hành động này không thể hoàn tác.")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    deleteCourse(course);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void deleteCourse(Course course) {
        progressBar.setVisibility(View.VISIBLE);
        repository.deleteCourse(course.getId()).observe(this, resource -> {
            if (resource.isLoading()) return;
            
            progressBar.setVisibility(View.GONE);
            if (resource.isSuccess()) {
                Toast.makeText(this, "Đã xóa khóa học", Toast.LENGTH_SHORT).show();
                loadCourses();
            } else {
                Toast.makeText(this, "Lỗi xóa: " + (resource.error != null ? resource.error.getMessage() : ""), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
