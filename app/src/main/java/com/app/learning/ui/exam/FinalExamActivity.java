package com.app.learning.ui.exam;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.vietsyncmobile.R;
import com.app.learning.data.model.ExamAttemptModel;
import com.app.learning.data.model.QuizQuestionModel;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FinalExamActivity extends AppCompatActivity {

    public static final String EXTRA_COURSE_ID = "extra_course_id";

    private MaterialToolbar toolbar;
    private TextView tvTimer, tvQuestionProgress;
    private ViewPager2 viewPagerQuestions;
    private MaterialButton btnPrevious, btnNext, btnSubmit, btnNavGrid;

    private ExamViewModel viewModel;
    private QuestionPagerAdapter pagerAdapter;
    private String courseId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_exam);

        toolbar = findViewById(R.id.toolbar);
        tvTimer = findViewById(R.id.tvTimer);
        tvQuestionProgress = findViewById(R.id.tvQuestionProgress);
        viewPagerQuestions = findViewById(R.id.viewPagerQuestions);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnNavGrid = findViewById(R.id.btnNavGrid);

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> handleExitConfirmation());

        courseId = getIntent().getStringExtra(EXTRA_COURSE_ID);
        if (courseId == null) {
            Toast.makeText(this, "Không tìm thấy thông tin khóa học!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this).get(ExamViewModel.class);
        pagerAdapter = new QuestionPagerAdapter();
        viewPagerQuestions.setAdapter(pagerAdapter);

        // Disables user swiping to enforce button-only or grid-only navigation if desired, 
        // but keeping swiping enabled is friendly. Let's keep swiping enabled.
        viewPagerQuestions.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                viewModel.setCurrentIndex(position);
                updateNavigationButtons(position);
            }
        });

        btnPrevious.setOnClickListener(v -> {
            int current = viewPagerQuestions.getCurrentItem();
            if (current > 0) {
                viewPagerQuestions.setCurrentItem(current - 1, true);
            }
        });

        btnNext.setOnClickListener(v -> {
            int current = viewPagerQuestions.getCurrentItem();
            List<QuizQuestionModel> list = viewModel.getQuestions().getValue();
            if (list != null && current < list.size() - 1) {
                viewPagerQuestions.setCurrentItem(current + 1, true);
            }
        });

        btnSubmit.setOnClickListener(v -> showSubmitConfirmationDialog());

        btnNavGrid.setOnClickListener(v -> showNavigationGridBottomSheet());

        // Handle physical back button
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                handleExitConfirmation();
            }
        });

        observeViewModel();

        // Load questions and retroactively check attempts
        viewModel.checkRetakeEligibility(courseId);
        viewModel.loadExamQuestions(courseId, true);
        viewModel.startTimer(1800000L); // 30 minutes
    }

    private void observeViewModel() {
        viewModel.getQuestions().observe(this, quizQuestions -> {
            if (quizQuestions != null && !quizQuestions.isEmpty()) {
                pagerAdapter.setQuestions(quizQuestions);
                updateNavigationButtons(viewPagerQuestions.getCurrentItem());
            }
        });

        viewModel.getCurrentIndex().observe(this, index -> {
            List<QuizQuestionModel> list = viewModel.getQuestions().getValue();
            int total = list != null ? list.size() : 0;
            tvQuestionProgress.setText(String.format(Locale.getDefault(), "Câu hỏi: %d / %d", (index + 1), total));
        });

        viewModel.getTimeLeftInMillis().observe(this, timeInMillis -> {
            int minutes = (int) (timeInMillis / 1000) / 60;
            int seconds = (int) (timeInMillis / 1000) % 60;
            tvTimer.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
        });

        viewModel.getIsTimerFinished().observe(this, isFinished -> {
            if (Boolean.TRUE.equals(isFinished)) {
                Toast.makeText(this, "Hết giờ làm bài! Hệ thống tự động nộp bài.", Toast.LENGTH_LONG).show();
                performSubmit();
            }
        });

        viewModel.getSubmitSuccess().observe(this, attempt -> {
            if (attempt != null) {
                viewModel.stopTimer();
                Intent intent = new Intent(this, ExamResultActivity.class);
                intent.putExtra("exam_attempt", attempt);
                intent.putExtra("questions", (Serializable) viewModel.getQuestions().getValue());
                intent.putExtra("lesson_map", (Serializable) viewModel.getLessonMap().getValue());
                startActivity(intent);
                finish();
            }
        });

        viewModel.getErrorMessage().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void updateNavigationButtons(int position) {
        List<QuizQuestionModel> list = viewModel.getQuestions().getValue();
        int total = list != null ? list.size() : 0;

        btnPrevious.setEnabled(position > 0);

        if (position == total - 1) {
            btnNext.setVisibility(View.GONE);
            btnSubmit.setVisibility(View.VISIBLE);
        } else {
            btnNext.setVisibility(View.VISIBLE);
            btnSubmit.setVisibility(View.GONE);
        }
    }

    private void showSubmitConfirmationDialog() {
        // Count unanswered questions
        List<QuizQuestionModel> list = viewModel.getQuestions().getValue();
        int unanswered = 0;
        if (list != null) {
            for (QuizQuestionModel q : list) {
                if (q.getUserAnswer() == null || q.getUserAnswer().trim().isEmpty()) {
                    unanswered++;
                }
            }
        }

        String message = "Bạn có chắc chắn muốn nộp bài?";
        if (unanswered > 0) {
            message = "Bạn vẫn còn " + unanswered + " câu hỏi chưa trả lời. Bạn có chắc chắn muốn nộp bài?";
        }

        new AlertDialog.Builder(this)
                .setTitle("Nộp bài thi")
                .setMessage(message)
                .setPositiveButton("Nộp bài", (dialog, which) -> performSubmit())
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void performSubmit() {
        viewModel.submitExam(courseId);
    }

    private void handleExitConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Thoát bài thi")
                .setMessage("Bài thi của bạn đang làm dở và sẽ không được lưu. Bạn có chắc muốn thoát?")
                .setPositiveButton("Thoát", (dialog, which) -> {
                    viewModel.stopTimer();
                    finish();
                })
                .setNegativeButton("Tiếp tục làm bài", null)
                .show();
    }

    private void showNavigationGridBottomSheet() {
        List<QuizQuestionModel> list = viewModel.getQuestions().getValue();
        if (list == null || list.isEmpty()) return;

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.dialog_question_nav, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        RecyclerView rvGrid = bottomSheetView.findViewById(R.id.rvGrid);
        rvGrid.setLayoutManager(new GridLayoutManager(this, 5));

        QuestionNavAdapter navAdapter = new QuestionNavAdapter(index -> {
            viewPagerQuestions.setCurrentItem(index, true);
            bottomSheetDialog.dismiss();
        });
        rvGrid.setAdapter(navAdapter);
        navAdapter.setQuestions(list, viewPagerQuestions.getCurrentItem());

        bottomSheetDialog.show();
    }
}
