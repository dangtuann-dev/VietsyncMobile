package com.app.learning.ui.teacher.course;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vietsyncmobile.R;
import com.app.learning.data.model.Course;
import com.app.learning.data.model.Enrollment;
import com.app.learning.data.model.User;
import com.app.learning.data.repository.TeacherCourseRepository;
import com.app.learning.utils.UserPreference;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class ManageStudentsActivity extends AppCompatActivity {

    private Spinner spinnerCourses;
    private RecyclerView recyclerViewStudents;
    private LinearLayout layoutEmptyState;
    private TextView txtStudentCount;
    private ExtendedFloatingActionButton fabAddStudent;

    private TeacherCourseRepository repository;
    private UserPreference userPreference;
    
    private List<Course> instructorCourses = new ArrayList<>();
    private List<Enrollment> enrolledStudents = new ArrayList<>();
    private StudentAdapter adapter;
    private Course selectedCourse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_students);

        repository = new TeacherCourseRepository(this);
        userPreference = UserPreference.getInstance(this);

        initViews();
        setupRecyclerView();
        loadCourses();
    }

    private void initViews() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        spinnerCourses = findViewById(R.id.spinnerCourses);
        recyclerViewStudents = findViewById(R.id.recyclerViewStudents);
        layoutEmptyState = findViewById(R.id.layoutEmptyState);
        txtStudentCount = findViewById(R.id.txtStudentCount);
        fabAddStudent = findViewById(R.id.fabAddStudent);

        spinnerCourses.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < instructorCourses.size()) {
                    selectedCourse = instructorCourses.get(position);
                    loadStudents(selectedCourse.getId().toString());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedCourse = null;
            }
        });

        fabAddStudent.setOnClickListener(v -> showAddStudentDialog());
    }

    private void setupRecyclerView() {
        recyclerViewStudents.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StudentAdapter();
        recyclerViewStudents.setAdapter(adapter);
    }

    private void loadCourses() {
        User user = userPreference.getUserProfile();
        if (user == null || user.getId() == null) {
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        repository.getCoursesForInstructor(user.getId().toString()).observe(this, resource -> {
            if (resource.isSuccess() && resource.data != null) {
                instructorCourses = resource.data;
                updateCourseSpinner();
            } else if (resource.isError()) {
                Toast.makeText(this, "Lỗi tải danh sách khóa học: " + resource.error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCourseSpinner() {
        List<String> courseTitles = new ArrayList<>();
        for (Course course : instructorCourses) {
            courseTitles.add(course.getTitle());
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, courseTitles);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCourses.setAdapter(spinnerAdapter);

        if (instructorCourses.isEmpty()) {
            layoutEmptyState.setVisibility(View.VISIBLE);
            recyclerViewStudents.setVisibility(View.GONE);
            fabAddStudent.setEnabled(false);
        } else {
            fabAddStudent.setEnabled(true);
        }
    }

    private void loadStudents(String courseId) {
        repository.getEnrollmentsForCourse(courseId).observe(this, resource -> {
            if (resource.isSuccess() && resource.data != null) {
                enrolledStudents = resource.data;
                adapter.setEnrollments(enrolledStudents);
                updateUIState();
            } else if (resource.isError()) {
                Toast.makeText(this, "Lỗi tải học viên: " + resource.error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUIState() {
        txtStudentCount.setText(enrolledStudents.size() + " học viên");
        if (enrolledStudents.isEmpty()) {
            layoutEmptyState.setVisibility(View.VISIBLE);
            recyclerViewStudents.setVisibility(View.GONE);
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            recyclerViewStudents.setVisibility(View.VISIBLE);
        }
    }

    private void showAddStudentDialog() {
        if (selectedCourse == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_student, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.show();

        TextInputEditText etStudentEmail = dialogView.findViewById(R.id.etStudentEmail);
        dialogView.findViewById(R.id.btnCancel).setOnClickListener(v -> dialog.dismiss());
        dialogView.findViewById(R.id.btnConfirm).setOnClickListener(v -> {
            String email = etStudentEmail.getText().toString().trim();
            if (email.isEmpty()) {
                etStudentEmail.setError("Email không được trống");
                return;
            }

            dialog.dismiss();
            addStudent(email);
        });
    }

    private void addStudent(String email) {
        if (selectedCourse == null) return;
        Toast.makeText(this, "Đang ghi danh học viên...", Toast.LENGTH_SHORT).show();
        repository.addStudentToCourse(email, selectedCourse.getId().toString()).observe(this, resource -> {
            if (resource.isSuccess() && resource.data != null) {
                Toast.makeText(this, "Đã thêm học viên thành công", Toast.LENGTH_SHORT).show();
                enrolledStudents.add(resource.data);
                adapter.notifyItemInserted(enrolledStudents.size() - 1);
                updateUIState();
            } else if (resource.isError()) {
                Toast.makeText(this, "Lỗi: " + resource.error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showEditProgressDialog(Enrollment enrollment, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_edit_progress, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        dialog.show();

        TextInputEditText etProgress = dialogView.findViewById(R.id.etProgress);
        etProgress.setText(String.valueOf(enrollment.getProgressPercent()));

        dialogView.findViewById(R.id.btnCancel).setOnClickListener(v -> dialog.dismiss());
        dialogView.findViewById(R.id.btnConfirm).setOnClickListener(v -> {
            String progressStr = etProgress.getText().toString().trim();
            if (progressStr.isEmpty()) {
                etProgress.setError("Phần trăm không được trống");
                return;
            }

            int progress = Integer.parseInt(progressStr);
            if (progress < 0 || progress > 100) {
                etProgress.setError("Tiến độ phải từ 0 đến 100%");
                return;
            }

            dialog.dismiss();
            updateStudentProgress(enrollment, progress, position);
        });
    }

    private void updateStudentProgress(Enrollment enrollment, int progress, int position) {
        repository.updateStudentProgress(enrollment.getUserId(), enrollment.getCourseId(), progress).observe(this, resource -> {
            if (resource.isSuccess()) {
                Toast.makeText(this, "Đã cập nhật tiến độ", Toast.LENGTH_SHORT).show();
                enrollment.setProgressPercent(progress);
                adapter.notifyItemChanged(position);
            } else if (resource.isError()) {
                Toast.makeText(this, "Cập nhật thất bại: " + resource.error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDeleteConfirmDialog(Enrollment enrollment, int position) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa học viên")
                .setMessage("Bạn có chắc muốn xóa học viên khỏi khóa học này?")
                .setNegativeButton("Hủy bỏ", null)
                .setPositiveButton("Xóa", (dialog, which) -> {
                    removeStudent(enrollment, position);
                })
                .show();
    }

    private void removeStudent(Enrollment enrollment, int position) {
        repository.removeStudentFromCourse(enrollment.getUserId(), enrollment.getCourseId()).observe(this, resource -> {
            if (resource.isSuccess()) {
                Toast.makeText(this, "Đã xóa học viên thành công", Toast.LENGTH_SHORT).show();
                enrolledStudents.remove(position);
                adapter.notifyItemRemoved(position);
                adapter.notifyItemRangeChanged(position, enrolledStudents.size());
                updateUIState();
            } else if (resource.isError()) {
                Toast.makeText(this, "Xóa thất bại: " + resource.error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Inner Adapter Class for Students List
    private class StudentAdapter extends RecyclerView.Adapter<StudentViewHolder> {

        private List<Enrollment> enrollmentsList = new ArrayList<>();

        public void setEnrollments(List<Enrollment> list) {
            this.enrollmentsList = list;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_manage_student, parent, false);
            return new StudentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
            Enrollment enrollment = enrollmentsList.get(position);
            User student = enrollment.getUser();

            if (student != null) {
                holder.txtName.setText(student.getFullName());
                holder.txtEmail.setText(student.getEmail());
            } else {
                holder.txtName.setText("Học viên");
                holder.txtEmail.setText(enrollment.getUserId());
            }

            holder.txtProgress.setText("Tiến độ học: " + enrollment.getProgressPercent() + "%");
            holder.progressIndicator.setProgress(enrollment.getProgressPercent());

            holder.btnEdit.setOnClickListener(v -> showEditProgressDialog(enrollment, position));
            holder.btnDelete.setOnClickListener(v -> showDeleteConfirmDialog(enrollment, position));
        }

        @Override
        public int getItemCount() {
            return enrollmentsList.size();
        }
    }

    private static class StudentViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView txtName, txtEmail, txtProgress;
        LinearProgressIndicator progressIndicator;
        ImageButton btnEdit, btnDelete;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            txtName = itemView.findViewById(R.id.txtStudentName);
            txtEmail = itemView.findViewById(R.id.txtStudentEmail);
            txtProgress = itemView.findViewById(R.id.txtProgressPercent);
            progressIndicator = itemView.findViewById(R.id.progressIndicator);
            btnEdit = itemView.findViewById(R.id.btnEditProgress);
            btnDelete = itemView.findViewById(R.id.btnDeleteStudent);
        }
    }
}
