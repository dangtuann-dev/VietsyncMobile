package com.app.learning.ui.gradebook;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vietsyncmobile.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class GradeBookActivity extends AppCompatActivity {

    public static final String EXTRA_COURSE_ID = "extra_course_id";

    private MaterialToolbar toolbar;
    private MaterialButton btnExportCsv;
    private TabLayout tabLayout;
    private LinearLayout layoutOverview, layoutQuizzes, layoutAssignments;
    private TextView tvGpa, tvCompletionRate, tvLearningHours;
    
    private LineChart lineChartGpa;
    private BarChart barChartScores;
    private RadarChart radarChartSkills;
    
    private RecyclerView rvQuizzes;
    private QuizAttemptsAdapter quizAdapter;

    private GradeBookViewModel viewModel;
    private String courseId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grade_book);

        toolbar = findViewById(R.id.toolbar);
        btnExportCsv = findViewById(R.id.btnExportCsv);
        tabLayout = findViewById(R.id.tabLayout);
        
        layoutOverview = findViewById(R.id.layoutOverview);
        layoutQuizzes = findViewById(R.id.layoutQuizzes);
        layoutAssignments = findViewById(R.id.layoutAssignments);
        
        tvGpa = findViewById(R.id.tvGpa);
        tvCompletionRate = findViewById(R.id.tvCompletionRate);
        tvLearningHours = findViewById(R.id.tvLearningHours);
        
        lineChartGpa = findViewById(R.id.lineChartGpa);
        barChartScores = findViewById(R.id.barChartScores);
        radarChartSkills = findViewById(R.id.radarChartSkills);
        
        rvQuizzes = findViewById(R.id.rvQuizzes);

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        courseId = getIntent().getStringExtra(EXTRA_COURSE_ID);
        if (courseId == null) courseId = "c0eebc99-9c0b-4ef8-bb6d-6bb9bd380001";

        viewModel = new ViewModelProvider(this).get(GradeBookViewModel.class);

        setupTabs();
        setupRecyclerView();
        observeViewModel();
        
        btnExportCsv.setOnClickListener(v -> exportGradesToCsv());

        viewModel.aggregateGrades(courseId);
    }

    private void setupTabs() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                layoutOverview.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
                layoutQuizzes.setVisibility(position == 1 ? View.VISIBLE : View.GONE);
                layoutAssignments.setVisibility(position == 2 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void setupRecyclerView() {
        rvQuizzes.setLayoutManager(new LinearLayoutManager(this));
        quizAdapter = new QuizAttemptsAdapter();
        rvQuizzes.setAdapter(quizAdapter);
    }

    private void observeViewModel() {
        viewModel.getGpa().observe(this, value -> tvGpa.setText(String.valueOf(value)));
        viewModel.getCompletionRate().observe(this, rate -> tvCompletionRate.setText(rate + "%"));
        viewModel.getLearningHours().observe(this, hours -> tvLearningHours.setText(hours + "h"));
        
        viewModel.getQuizAttempts().observe(this, attempts -> {
            if (attempts != null) {
                quizAdapter.setAttempts(attempts);
                setupCharts();
            }
        });
    }

    private void setupCharts() {
        // 1. Line Chart setup (Weekly GPA)
        List<Entry> lineEntries = new ArrayList<>();
        List<Float> weekly = viewModel.getWeeklyStats();
        for (int i = 0; i < weekly.size(); i++) {
            lineEntries.add(new Entry(i + 1, weekly.get(i)));
        }
        
        LineDataSet lineDataSet = new LineDataSet(lineEntries, "GPA cá nhân");
        lineDataSet.setColor(0xFF38BDF8); // Sky blue
        lineDataSet.setCircleColor(0xFF38BDF8);
        lineDataSet.setLineWidth(3f);
        lineDataSet.setCircleRadius(5f);
        lineDataSet.setValueTextColor(Color.WHITE);
        lineDataSet.setValueTextSize(10dp);

        LineData lineData = new LineData(lineDataSet);
        lineChartGpa.setData(lineData);
        lineChartGpa.getDescription().setEnabled(false);
        lineChartGpa.getXAxis().setTextColor(Color.WHITE);
        lineChartGpa.getAxisLeft().setTextColor(Color.WHITE);
        lineChartGpa.getAxisRight().setEnabled(false);
        lineChartGpa.getLegend().setTextColor(Color.WHITE);
        lineChartGpa.animateX(1000);
        lineChartGpa.invalidate();

        // 2. Bar Chart setup (Comparison)
        List<BarEntry> barEntries = new ArrayList<>();
        List<Float> comparison = viewModel.compareWithAverage();
        barEntries.add(new BarEntry(1f, comparison.get(0)));
        barEntries.add(new BarEntry(2f, comparison.get(1)));

        BarDataSet barDataSet = new BarDataSet(barEntries, "Tỷ lệ đúng (%)");
        barDataSet.setColors(new int[]{0xFF10B981, 0xFF3B82F6}); // Green, Blue
        barDataSet.setValueTextColor(Color.WHITE);
        barDataSet.setValueTextSize(11dp);

        BarData barData = new BarData(barDataSet);
        barChartScores.setData(barData);
        barChartScores.getDescription().setEnabled(false);
        
        String[] labels = new String[]{"", "Cá nhân", "Trung bình lớp"};
        barChartScores.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        barChartScores.getXAxis().setTextColor(Color.WHITE);
        barChartScores.getAxisLeft().setTextColor(Color.WHITE);
        barChartScores.getAxisRight().setEnabled(false);
        barChartScores.getLegend().setEnabled(false);
        barChartScores.animateY(1000);
        barChartScores.invalidate();

        // 3. Radar Chart setup (Skills Analysis)
        List<RadarEntry> radarEntries = new ArrayList<>();
        radarEntries.add(new RadarEntry(80f)); // Coding
        radarEntries.add(new RadarEntry(90f)); // Theory
        radarEntries.add(new RadarEntry(65f)); // Design/UI
        radarEntries.add(new RadarEntry(75f)); // Debugging
        radarEntries.add(new RadarEntry(85f)); // API Integration

        RadarDataSet radarDataSet = new RadarDataSet(radarEntries, "Kỹ năng học tập");
        radarDataSet.setColor(0xFFF59E0B); // Amber
        radarDataSet.setFillColor(0x55F59E0B);
        radarDataSet.setDrawFilled(true);
        radarDataSet.setValueTextColor(Color.WHITE);
        radarDataSet.setLineWidth(2f);

        RadarData radarData = new RadarData(radarDataSet);
        radarChartSkills.setData(radarData);
        radarChartSkills.getDescription().setEnabled(false);
        
        String[] skillLabels = new String[]{"Coding", "Lý thuyết", "Giao diện", "Sửa lỗi", "Kết nối API"};
        radarChartSkills.getXAxis().setValueFormatter(new IndexAxisValueFormatter(skillLabels));
        radarChartSkills.getXAxis().setTextColor(Color.WHITE);
        radarChartSkills.getYAxis().setTextColor(Color.TRANSPARENT); // Hide values
        radarChartSkills.getLegend().setTextColor(Color.WHITE);
        radarChartSkills.animateXY(1000, 1000);
        radarChartSkills.invalidate();
    }

    private void exportGradesToCsv() {
        List<JsonObject> attempts = viewModel.getQuizAttempts().getValue();
        if (attempts == null || attempts.isEmpty()) {
            Toast.makeText(this, "Không có dữ liệu để xuất!", Toast.LENGTH_SHORT).show();
            return;
        }

        StringBuilder csvBuilder = new StringBuilder();
        csvBuilder.append("ID,Câu hỏi,Đáp án chọn,Đánh giá,Ngày thực hiện\n");

        for (JsonObject item : attempts) {
            String id = item.has("id") ? item.get("id").getAsString() : "";
            
            String question = "";
            if (item.has("quizzes") && item.getAsJsonObject("quizzes").has("question")) {
                question = item.getAsJsonObject("quizzes").get("question").getAsString();
                // Escape commas for CSV safety
                question = "\"" + question.replace("\"", "\"\"") + "\"";
            }

            String selectedAnswer = item.has("selected_answer") ? item.get("selected_answer").getAsString() : "";
            selectedAnswer = "\"" + selectedAnswer.replace("\"", "\"\"") + "\"";

            boolean isCorrect = item.has("is_correct") && item.get("is_correct").getAsBoolean();
            String status = isCorrect ? "ĐẠT" : "CHƯA ĐẠT";

            String date = item.has("attempted_at") ? item.get("attempted_at").getAsString() : "";

            csvBuilder.append(id).append(",")
                    .append(question).append(",")
                    .append(selectedAnswer).append(",")
                    .append(status).append(",")
                    .append(date).append("\n");
        }

        try {
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs();
            }
            File file = new File(downloadsDir, "HocBa_Course_" + System.currentTimeMillis() + ".csv");
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(csvBuilder.toString().getBytes());
            fos.close();

            Toast.makeText(this, "Xuất CSV thành công: " + file.getAbsolutePath(), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi xuất CSV: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private static class QuizAttemptsAdapter extends RecyclerView.Adapter<QuizAttemptsAdapter.QuizViewHolder> {
        private final List<JsonObject> items = new ArrayList<>();

        public void setAttempts(List<JsonObject> items) {
            this.items.clear();
            if (items != null) {
                this.items.addAll(items);
            }
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_grade_quiz, parent, false);
            return new QuizViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
            JsonObject item = items.get(position);
            
            String question = "Câu hỏi trắc nghiệm";
            if (item.has("quizzes") && item.getAsJsonObject("quizzes").has("question")) {
                question = item.getAsJsonObject("quizzes").get("question").getAsString();
            }
            holder.tvQuizName.setText(question);

            boolean isCorrect = item.has("is_correct") && item.get("is_correct").getAsBoolean();
            holder.tvQuizScore.setText(isCorrect ? "10 / 10" : "0 / 10");

            String date = item.has("attempted_at") ? item.get("attempted_at").getAsString() : "";
            if (date.contains("T")) date = date.split("T")[0];
            holder.tvQuizDate.setText("Ngày làm: " + date);

            if (isCorrect) {
                holder.cardPassFail.setCardBackgroundColor(0xFF10B981); // Green
                holder.tvPassFailText.setText("ĐẠT");
            } else {
                holder.cardPassFail.setCardBackgroundColor(0xFFEF4444); // Red
                holder.tvPassFailText.setText("HỎNG");
            }

            holder.btnReview.setOnClickListener(v -> {
                Toast.makeText(v.getContext(), "Mở lại bài trắc nghiệm này để xem lại...", Toast.LENGTH_SHORT).show();
            });
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class QuizViewHolder extends RecyclerView.ViewHolder {
            TextView tvQuizName, tvQuizScore, tvQuizDate, tvPassFailText;
            MaterialCardView cardPassFail;
            MaterialButton btnReview;

            QuizViewHolder(@NonNull View itemView) {
                super(itemView);
                tvQuizName = itemView.findViewById(R.id.tvQuizName);
                tvQuizScore = itemView.findViewById(R.id.tvQuizScore);
                tvQuizDate = itemView.findViewById(R.id.tvQuizDate);
                tvPassFailText = itemView.findViewById(R.id.tvPassFailText);
                cardPassFail = itemView.findViewById(R.id.cardPassFail);
                btnReview = itemView.findViewById(R.id.btnReview);
            }
        }
    }
}
