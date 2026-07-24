package com.app.learning.ui.quiz;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.vietsyncmobile.R;
import com.app.learning.data.model.QuizQuestionModel;
import com.bumptech.glide.Glide;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.List;
import java.util.Locale;

public class QuizActivity extends AppCompatActivity {

    public static final String EXTRA_LESSON_ID = "extra_lesson_id";

    private MaterialToolbar toolbar;
    private TextView tvTimer, tvQuestionProgress, tvQuestionText;
    private ProgressBar progressBarQuiz;
    private MaterialCardView cardQuestion;
    private ImageView imgQuestion;
    private RadioGroup rgOptions;
    private TextInputLayout tilShortAnswer;
    private TextInputEditText etShortAnswer;
    private MaterialButton btnSubmitNext;

    private QuizViewModel viewModel;
    private CountDownTimer countDownTimer;
    private String lessonId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        toolbar = findViewById(R.id.toolbar);
        tvTimer = findViewById(R.id.tvTimer);
        tvQuestionProgress = findViewById(R.id.tvQuestionProgress);
        tvQuestionText = findViewById(R.id.tvQuestionText);
        progressBarQuiz = findViewById(R.id.progressBarQuiz);
        cardQuestion = findViewById(R.id.cardQuestion);
        imgQuestion = findViewById(R.id.imgQuestion);
        rgOptions = findViewById(R.id.rgOptions);
        tilShortAnswer = findViewById(R.id.tilShortAnswer);
        etShortAnswer = findViewById(R.id.etShortAnswer);
        btnSubmitNext = findViewById(R.id.btnSubmitNext);

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        lessonId = getIntent().getStringExtra(EXTRA_LESSON_ID);
        if (lessonId == null) lessonId = "d0eebc99-9c0b-4ef8-bb6d-6bb9bd380011";

        viewModel = new ViewModelProvider(this).get(QuizViewModel.class);

        observeViewModel();
        startTimer(300000); // 5 minutes countdown

        btnSubmitNext.setOnClickListener(v -> handleNext());
        viewModel.loadQuiz(lessonId);
    }

    private void observeViewModel() {
        viewModel.getQuestions().observe(this, questions -> {
            if (questions != null && !questions.isEmpty()) {
                displayQuestion(0);
            }
        });

        viewModel.getCurrentIndex().observe(this, index -> {
            List<QuizQuestionModel> list = viewModel.getQuestions().getValue();
            if (list != null && index < list.size()) {
                displayQuestion(index);
            }
        });

        viewModel.getIsFinished().observe(this, finished -> {
            if (Boolean.TRUE.equals(finished)) {
                if (countDownTimer != null) countDownTimer.cancel();
                Intent intent = new Intent(this, QuizResultActivity.class);
                intent.putExtra("score", viewModel.calculateScore());
                intent.putExtra("total", viewModel.getQuestions().getValue() != null ? viewModel.getQuestions().getValue().size() : 0);
                startActivity(intent);
                finish();
            }
        });
    }

    private void displayQuestion(int index) {
        List<QuizQuestionModel> list = viewModel.getQuestions().getValue();
        if (list == null || index >= list.size()) return;

        QuizQuestionModel q = list.get(index);

        tvQuestionProgress.setText("Câu hỏi " + (index + 1) + "/" + list.size());
        progressBarQuiz.setProgress((int) (((index + 1) / (float) list.size()) * 100));

        tvQuestionText.setText(q.getQuestion());
        cardQuestion.startAnimation(AnimationUtils.loadAnimation(this, android.R.anim.fade_in));

        if (q.getImageUrl() != null && !q.getImageUrl().isEmpty()) {
            imgQuestion.setVisibility(View.VISIBLE);
            Glide.with(this).load(q.getImageUrl()).into(imgQuestion);
        } else {
            imgQuestion.setVisibility(View.GONE);
        }

        rgOptions.removeAllViews();
        rgOptions.setVisibility(View.GONE);
        tilShortAnswer.setVisibility(View.GONE);

        if ("SHORT_ANSWER".equalsIgnoreCase(q.getQuestionType())) {
            tilShortAnswer.setVisibility(View.VISIBLE);
            etShortAnswer.setText("");
        } else {
            rgOptions.setVisibility(View.VISIBLE);
            if (q.getOptions() != null) {
                for (int i = 0; i < q.getOptions().size(); i++) {
                    RadioButton rb = new RadioButton(this);
                    rb.setId(View.generateViewId());
                    rb.setText(q.getOptions().get(i));
                    rb.setTextColor(getResources().getColor(android.R.color.white));
                    rb.setTextSize(16);
                    rb.setPadding(16, 24, 16, 24);
                    rgOptions.addView(rb);
                }
            }
        }

        btnSubmitNext.setText(index == list.size() - 1 ? "Nộp bài" : "Tiếp theo");
    }

    private void handleNext() {
        List<QuizQuestionModel> list = viewModel.getQuestions().getValue();
        Integer idx = viewModel.getCurrentIndex().getValue();
        if (list == null || idx == null) return;

        QuizQuestionModel q = list.get(idx);
        String selectedAnswer = "";

        if ("SHORT_ANSWER".equalsIgnoreCase(q.getQuestionType())) {
            selectedAnswer = etShortAnswer.getText() != null ? etShortAnswer.getText().toString().trim() : "";
        } else {
            int selectedId = rgOptions.getCheckedRadioButtonId();
            if (selectedId != -1) {
                RadioButton rb = findViewById(selectedId);
                if (rb != null) selectedAnswer = rb.getText().toString();
            }
        }

        if (selectedAnswer.isEmpty()) {
            Toast.makeText(this, "Vui lòng chọn hoặc nhập đáp án!", Toast.LENGTH_SHORT).show();
            return;
        }

        viewModel.submitAnswer(selectedAnswer);
    }

    private void startTimer(long durationMs) {
        countDownTimer = new CountDownTimer(durationMs, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = (millisUntilFinished / 1000) / 60;
                long seconds = (millisUntilFinished / 1000) % 60;
                tvTimer.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                tvTimer.setText("00:00");
                Toast.makeText(QuizActivity.this, "Hết giờ làm bài!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(QuizActivity.this, QuizResultActivity.class);
                intent.putExtra("score", viewModel.calculateScore());
                intent.putExtra("total", viewModel.getQuestions().getValue() != null ? viewModel.getQuestions().getValue().size() : 0);
                startActivity(intent);
                finish();
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) countDownTimer.cancel();
    }
}
