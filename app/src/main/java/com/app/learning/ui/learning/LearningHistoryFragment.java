package com.app.learning.ui.learning;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vietsyncmobile.R;
import com.app.learning.ui.customview.HeatmapCalendarView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class LearningHistoryFragment extends Fragment {

    private LearningHistoryViewModel viewModel;
    private HeatmapCalendarView heatmapView;
    private BarChart weeklyBarChart;
    private RecyclerView rvMilestones;
    private MilestoneAdapter milestoneAdapter;

    private TextView tvTotalHours, tvAvgPerDay, tvStreakRecord, tvTotalLessons;
    private MaterialButton btnShareStats;
    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_learning_history, container, false);
        initViews(rootView);
        setupViewModel();
        return rootView;
    }

    private void initViews(View view) {
        heatmapView = view.findViewById(R.id.heatmapView);
        weeklyBarChart = view.findViewById(R.id.weeklyBarChart);
        rvMilestones = view.findViewById(R.id.rvMilestones);
        tvTotalHours = view.findViewById(R.id.tvTotalHours);
        tvAvgPerDay = view.findViewById(R.id.tvAvgPerDay);
        tvStreakRecord = view.findViewById(R.id.tvStreakRecord);
        tvTotalLessons = view.findViewById(R.id.tvTotalLessons);
        btnShareStats = view.findViewById(R.id.btnShareStats);

        rvMilestones.setLayoutManager(new LinearLayoutManager(getContext()));
        milestoneAdapter = new MilestoneAdapter();
        rvMilestones.setAdapter(milestoneAdapter);

        btnShareStats.setOnClickListener(v -> shareStatsAsImage());
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(LearningHistoryViewModel.class);

        viewModel.getTotalHours().observe(getViewLifecycleOwner(), hours -> tvTotalHours.setText(hours + "h"));
        viewModel.getAvgHoursPerDay().observe(getViewLifecycleOwner(), avg -> tvAvgPerDay.setText(String.format("%.1fh", avg)));
        viewModel.getStreakDays().observe(getViewLifecycleOwner(), streak -> tvStreakRecord.setText(streak + " Ngày 🔥"));
        viewModel.getTotalLessonsCompleted().observe(getViewLifecycleOwner(), lessons -> tvTotalLessons.setText(String.valueOf(lessons)));

        viewModel.getDailySessions().observe(getViewLifecycleOwner(), sessions -> {
            heatmapView.setSessionData(sessions);
            setupWeeklyChart();
        });

        viewModel.getMilestones().observe(getViewLifecycleOwner(), milestones -> {
            milestoneAdapter.setMilestones(milestones);
        });

        viewModel.loadHistoryData("test_user_id");
    }

    private void setupWeeklyChart() {
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0, 3.5f));
        entries.add(new BarEntry(1, 4.0f));
        entries.add(new BarEntry(2, 2.8f));
        entries.add(new BarEntry(3, 5.2f));

        BarDataSet dataSet = new BarDataSet(entries, "Số giờ học");
        dataSet.setColor(Color.parseColor("#3B82F6"));
        dataSet.setValueTextColor(Color.DKGRAY);
        dataSet.setValueTextSize(10f);

        BarData data = new BarData(dataSet);
        weeklyBarChart.setData(data);
        weeklyBarChart.getDescription().setEnabled(false);
        weeklyBarChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(new String[]{"T1", "T2", "T3", "T4"}));
        weeklyBarChart.animateY(800);
        weeklyBarChart.invalidate();
    }

    private void shareStatsAsImage() {
        if (rootView == null) return;
        try {
            Bitmap bitmap = Bitmap.createBitmap(rootView.getWidth(), rootView.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            rootView.draw(canvas);

            String path = MediaStore.Images.Media.insertImage(requireContext().getContentResolver(), bitmap, "LearningStats", null);
            Uri uri = Uri.parse(path);

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Xem tiến độ học tập của tôi trên VietsyncMobile!");
            startActivity(Intent.createChooser(shareIntent, "Chia sẻ thành tích học tập"));
        } catch (Exception e) {
            Toast.makeText(getContext(), "Không thể chia sẻ ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
