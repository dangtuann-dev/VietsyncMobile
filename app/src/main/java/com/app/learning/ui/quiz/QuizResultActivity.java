package com.app.learning.ui.quiz;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vietsyncmobile.R;
import com.google.android.material.button.MaterialButton;

public class QuizResultActivity extends AppCompatActivity {

    private TextView tvScoreText, tvPercentage, tvFeedback;
    private MaterialButton btnReviewAnswers, btnFinish;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_result);

        tvScoreText = findViewById(R.id.tvScoreText);
        tvPercentage = findViewById(R.id.tvPercentage);
        tvFeedback = findViewById(R.id.tvFeedback);
        btnReviewAnswers = findViewById(R.id.btnReviewAnswers);
        btnFinish = findViewById(R.id.btnFinish);

        int score = getIntent().getIntExtra("score", 0);
        int total = getIntent().getIntExtra("total", 3);

        tvScoreText.setText(score + " / " + total);
        int pct = total > 0 ? (int) (((float) score / total) * 100) : 0;
        tvPercentage.setText(pct + "%");

        if (pct >= 80) {
            tvFeedback.setText("Xuất sắc! Bạn đã nắm vững kiến thức bài học này.");
        } else if (pct >= 50) {
            tvFeedback.setText("Khá tốt! Bạn có thể xem lại bài học để nâng cao điểm số.");
        } else {
            tvFeedback.setText("Cố lên! Bạn nên ôn tập lại lý thuyết trước khi làm lại quiz.");
        }

        btnFinish.setOnClickListener(v -> finish());
        btnReviewAnswers.setOnClickListener(v -> finish());
    }
}
