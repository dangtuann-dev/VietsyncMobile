package com.app.learning.ui.quiz;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.app.learning.data.model.QuizQuestionModel;
import com.app.learning.ui.exam.ExamReviewBottomSheet;
import com.example.vietsyncmobile.R;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

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

        // Retrieve questions from intent
        @SuppressWarnings("unchecked")
        ArrayList<QuizQuestionModel> questions = (ArrayList<QuizQuestionModel>) getIntent().getSerializableExtra("questions");
        if (questions == null) questions = buildDefaultQuestions();

        final List<QuizQuestionModel> finalQuestions = questions;

        btnFinish.setOnClickListener(v -> finish());
        btnReviewAnswers.setOnClickListener(v -> {
            ExamReviewBottomSheet sheet = ExamReviewBottomSheet.newInstance(finalQuestions);
            sheet.show(getSupportFragmentManager(), "QuizReview");
        });
    }

    private ArrayList<QuizQuestionModel> buildDefaultQuestions() {
        ArrayList<QuizQuestionModel> list = new ArrayList<>();

        QuizQuestionModel q1 = new QuizQuestionModel();
        q1.setQuestion("OOP là viết tắt của?");
        q1.setOptions(java.util.Arrays.asList("Object-Oriented Programming", "Open Operating Platform", "Output Optimization Protocol", "Object Output Procedure"));
        q1.setCorrectAnswer("Object-Oriented Programming");
        q1.setUserAnswer("Object-Oriented Programming");
        q1.setExplanation("OOP (Object-Oriented Programming) là lập trình hướng đối tượng, mô hình lập trình dựa trên các đối tượng chứa dữ liệu và phương thức.");
        list.add(q1);

        QuizQuestionModel q2 = new QuizQuestionModel();
        q2.setQuestion("MVVM là pattern gì?");
        q2.setOptions(java.util.Arrays.asList("Model-View-ViewModel", "Main-View-Vision-Model", "Module-Visual-ViewModel", "Model-Visual-Vector-Map"));
        q2.setCorrectAnswer("Model-View-ViewModel");
        q2.setUserAnswer("Model-View-ViewModel");
        q2.setExplanation("MVVM là Model-View-ViewModel, giúp tách biệt logic nghiệp vụ khỏi UI, dễ test và bảo trì.");
        list.add(q2);

        QuizQuestionModel q3 = new QuizQuestionModel();
        q3.setQuestion("LiveData dùng để làm gì?");
        q3.setOptions(java.util.Arrays.asList("Quan sát dữ liệu theo lifecycle", "Lưu dữ liệu xuống database", "Xử lý luồng network", "Render giao diện Android"));
        q3.setCorrectAnswer("Quan sát dữ liệu theo lifecycle");
        q3.setUserAnswer("Quan sát dữ liệu theo lifecycle");
        q3.setExplanation("LiveData là một observable data holder tuân theo lifecycle, tự động cập nhật UI khi dữ liệu thay đổi.");
        list.add(q3);

        return list;
    }
}
