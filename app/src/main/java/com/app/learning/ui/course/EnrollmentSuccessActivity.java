package com.app.learning.ui.course;

import android.content.Intent;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.app.learning.ui.base.BaseActivity;
import com.example.vietsyncmobile.R;
import com.google.android.material.button.MaterialButton;

public class EnrollmentSuccessActivity extends BaseActivity {

    private LottieAnimationView lavCelebration;
    private TextView tvCourseTitle;
    private MaterialButton btnStartLearning;

    private String courseTitle;
    private String courseId;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_enrollment_success;
    }

    @Override
    protected void initViews() {
        lavCelebration = findViewById(R.id.lav_celebration);
        tvCourseTitle = findViewById(R.id.tv_success_course_title);
        btnStartLearning = findViewById(R.id.btn_success_start_learning);

        courseTitle = getIntent().getStringExtra("course_title");
        courseId = getIntent().getStringExtra("course_id");

        if (courseTitle != null) {
            tvCourseTitle.setText(courseTitle);
        }

        lavCelebration.playAnimation();

        btnStartLearning.setOnClickListener(v -> {
            Intent intent = new Intent(this, CourseDetailActivity.class);
            intent.putExtra("course_id", courseId);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void initObservers() {
    }
}
