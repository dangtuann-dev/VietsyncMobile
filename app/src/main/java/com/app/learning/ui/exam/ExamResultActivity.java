package com.app.learning.ui.exam;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vietsyncmobile.R;
import com.app.learning.data.model.ExamAttemptModel;
import com.app.learning.data.model.QuizQuestionModel;
import com.app.learning.ui.certificate.CertificateActivity;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExamResultActivity extends AppCompatActivity {

    private TextView tvScoreText, tvPercentage, tvBadgeText;
    private MaterialCardView cardScore, cardBadge;
    private RecyclerView rvSectionAnalysis;
    private MaterialButton btnGetCertificate, btnRetake, btnReviewAnswers, btnFinish;

    private ExamAttemptModel attempt;
    private List<QuizQuestionModel> questions;
    private Map<String, String> lessonMap;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_result);

        tvScoreText = findViewById(R.id.tvScoreText);
        tvPercentage = findViewById(R.id.tvPercentage);
        tvBadgeText = findViewById(R.id.tvBadgeText);
        cardScore = findViewById(R.id.cardScore);
        cardBadge = findViewById(R.id.cardBadge);
        rvSectionAnalysis = findViewById(R.id.rvSectionAnalysis);
        btnGetCertificate = findViewById(R.id.btnGetCertificate);
        btnRetake = findViewById(R.id.btnRetake);
        btnReviewAnswers = findViewById(R.id.btnReviewAnswers);
        btnFinish = findViewById(R.id.btnFinish);

        attempt = (ExamAttemptModel) getIntent().getSerializableExtra("exam_attempt");
        questions = (List<QuizQuestionModel>) getIntent().getSerializableExtra("questions");
        lessonMap = (Map<String, String>) getIntent().getSerializableExtra("lesson_map");

        if (attempt == null) {
            Toast.makeText(this, "Không có dữ liệu bài thi!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        displayResults();
        setupSectionAnalysis();

        btnFinish.setOnClickListener(v -> finish());

        btnRetake.setOnClickListener(v -> {
            Intent intent = new Intent(this, FinalExamActivity.class);
            intent.putExtra(FinalExamActivity.EXTRA_COURSE_ID, attempt.getCourseId());
            startActivity(intent);
            finish();
        });

        btnGetCertificate.setOnClickListener(v -> {
            Intent intent = new Intent(this, CertificateActivity.class);
            intent.putExtra(CertificateActivity.EXTRA_COURSE_ID, attempt.getCourseId());
            intent.putExtra(CertificateActivity.EXTRA_COURSE_TITLE, "Khóa Học Lập Trình");
            intent.putExtra(CertificateActivity.EXTRA_INSTRUCTOR_NAME, "Giảng Viên Vietsync");
            intent.putExtra(CertificateActivity.EXTRA_COURSE_HOURS, "40");
            startActivity(intent);
        });

        btnReviewAnswers.setOnClickListener(v -> {
            Toast.makeText(this, "Tính năng xem lại câu trả lời chi tiết đang được phát triển!", Toast.LENGTH_SHORT).show();
        });
    }

    private void displayResults() {
        int score = attempt.getScore();
        int total = questions != null ? questions.size() : 30;
        tvScoreText.setText(score + " / " + total);

        int percentage = total > 0 ? (score * 100) / total : 0;
        tvPercentage.setText(percentage + "%");

        if (attempt.isPassed()) {
            cardBadge.setCardBackgroundColor(0xFF10B981); // Emerald Green
            tvBadgeText.setText("ĐẠT (PASS)");
            btnGetCertificate.setVisibility(View.VISIBLE);
            btnRetake.setVisibility(View.GONE);
            cardScore.setStrokeColor(0xFF10B981);
        } else {
            cardBadge.setCardBackgroundColor(0xFFEF4444); // Red
            tvBadgeText.setText("CHƯA ĐẠT (FAIL)");
            btnGetCertificate.setVisibility(View.GONE);
            cardScore.setStrokeColor(0xFFEF4444);

            int attemptsMade = attempt.getAttemptNumber();
            int attemptsLeft = 3 - attemptsMade;
            if (attemptsLeft > 0) {
                btnRetake.setVisibility(View.VISIBLE);
                btnRetake.setText("Thi Lại (Còn " + attemptsLeft + " lượt)");
            } else {
                btnRetake.setVisibility(View.VISIBLE);
                btnRetake.setEnabled(false);
                btnRetake.setText("Thi Lại (Hết lượt làm bài)");
                btnRetake.setBackgroundColor(0xFF64748B); // Muted slate color
            }
        }
    }

    private void setupSectionAnalysis() {
        if (questions == null || questions.isEmpty()) return;

        // Group scores by lessonId
        Map<String, SectionScore> sectionScores = new HashMap<>();
        for (QuizQuestionModel q : questions) {
            String lessonId = q.getLessonId();
            if (lessonId == null) continue;

            SectionScore score = sectionScores.get(lessonId);
            if (score == null) {
                String title = lessonMap != null ? lessonMap.get(lessonId) : "Bài học";
                score = new SectionScore(title != null ? title : "Bài học");
                sectionScores.put(lessonId, score);
            }

            score.total++;
            if (q.isCorrect()) {
                score.correct++;
            }
        }

        List<SectionScore> list = new ArrayList<>(sectionScores.values());
        rvSectionAnalysis.setLayoutManager(new LinearLayoutManager(this));
        rvSectionAnalysis.setAdapter(new SectionAdapter(list));
    }

    private static class SectionScore {
        String title;
        int correct = 0;
        int total = 0;

        SectionScore(String title) {
            this.title = title;
        }
    }

    private static class SectionAdapter extends RecyclerView.Adapter<SectionAdapter.SectionViewHolder> {
        private final List<SectionScore> items;

        SectionAdapter(List<SectionScore> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public SectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_section_analysis, parent, false);
            return new SectionViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull SectionViewHolder holder, int position) {
            SectionScore item = items.get(position);
            holder.tvSectionTitle.setText(item.title);
            holder.tvSectionScore.setText(item.correct + " / " + item.total);

            int pct = item.total > 0 ? (item.correct * 100) / item.total : 0;
            holder.pbSectionProgress.setProgress(pct);
            
            // Color feedback for sections: green if >= 70%, else blue/grey
            if (pct >= 70) {
                holder.pbSectionProgress.setProgressTintList(android.content.res.ColorStateList.valueOf(0xFF10B981));
                holder.tvSectionScore.setTextColor(0xFF10B981);
            } else {
                holder.pbSectionProgress.setProgressTintList(android.content.res.ColorStateList.valueOf(0xFF3B82F6));
                holder.tvSectionScore.setTextColor(0xFF38BDF8);
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class SectionViewHolder extends RecyclerView.ViewHolder {
            TextView tvSectionTitle, tvSectionScore;
            ProgressBar pbSectionProgress;

            SectionViewHolder(@NonNull View itemView) {
                super(itemView);
                tvSectionTitle = itemView.findViewById(R.id.tvSectionTitle);
                tvSectionScore = itemView.findViewById(R.id.tvSectionScore);
                pbSectionProgress = itemView.findViewById(R.id.pbSectionProgress);
            }
        }
    }
}
