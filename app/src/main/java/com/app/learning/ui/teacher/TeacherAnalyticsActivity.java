package com.app.learning.ui.teacher;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;

import com.example.vietsyncmobile.R;
import com.app.learning.ui.base.BaseActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class TeacherAnalyticsActivity extends BaseActivity {

    private TextView tvTotalRevenue, tvTotalStudents, tvAvgRating, tvCompletionRate;
    private LineChart chartEnrollments;
    private BarChart chartRevenue;
    private PieChart chartRatings;
    private MaterialButton btnExportPdf;
    private View scrollView;

    private TeacherAnalyticsViewModel viewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_teacher_analytics;
    }

    @Override
    protected void initViews() {
        tvTotalRevenue = findViewById(R.id.tvTotalRevenue);
        tvTotalStudents = findViewById(R.id.tvTotalStudents);
        tvAvgRating = findViewById(R.id.tvAvgRating);
        tvCompletionRate = findViewById(R.id.tvCompletionRate);

        chartEnrollments = findViewById(R.id.chartEnrollments);
        chartRevenue = findViewById(R.id.chartRevenue);
        chartRatings = findViewById(R.id.chartRatings);

        btnExportPdf = findViewById(R.id.btnExportPdf);
        scrollView = findViewById(R.id.scrollViewTeacherAnalytics);

        btnExportPdf.setOnClickListener(v -> exportPdfReport());

        setupCharts();
    }

    @Override
    protected void initObservers() {
        viewModel = new androidx.lifecycle.ViewModelProvider(this).get(TeacherAnalyticsViewModel.class);
        viewModel.getTeacherStats().observe(this, map -> {
            if (map != null) {
                if (map.containsKey("total_revenue")) tvTotalRevenue.setText(String.format("%,d ₫", (Long) map.get("total_revenue")));
                if (map.containsKey("total_students")) tvTotalStudents.setText(String.valueOf(map.get("total_students")));
                if (map.containsKey("avg_rating")) tvAvgRating.setText(map.get("avg_rating") + " ★");
                if (map.containsKey("completion_rate")) tvCompletionRate.setText(map.get("completion_rate") + "%");
            }
        });
        viewModel.loadAnalyticsData();
    }

    private void setupCharts() {
        // Line Chart: Enrollments
        List<Entry> lineEntries = new ArrayList<>();
        lineEntries.add(new Entry(1, 120));
        lineEntries.add(new Entry(2, 210));
        lineEntries.add(new Entry(3, 340));
        lineEntries.add(new Entry(4, 480));

        LineDataSet lineDataSet = new LineDataSet(lineEntries, "Ghi danh");
        lineDataSet.setColor(Color.parseColor("#3B82F6"));
        lineDataSet.setCircleColor(Color.parseColor("#3B82F6"));
        lineDataSet.setLineWidth(2.5f);
        chartEnrollments.setData(new LineData(lineDataSet));
        chartEnrollments.getDescription().setEnabled(false);
        chartEnrollments.animateX(600);
        chartEnrollments.invalidate();

        // Bar Chart: Revenue
        List<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(1, 45f));
        barEntries.add(new BarEntry(2, 32f));
        barEntries.add(new BarEntry(3, 48f));

        BarDataSet barDataSet = new BarDataSet(barEntries, "Doanh thu (Tr ₫)");
        barDataSet.setColor(Color.parseColor("#10B981"));
        chartRevenue.setData(new BarData(barDataSet));
        chartRevenue.getDescription().setEnabled(false);
        chartRevenue.animateY(600);
        chartRevenue.invalidate();

        // Pie Chart: Ratings
        List<PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry(70f, "5 Star"));
        pieEntries.add(new PieEntry(20f, "4 Star"));
        pieEntries.add(new PieEntry(10f, "3 Star"));

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
        pieDataSet.setColors(new int[]{
                Color.parseColor("#10B981"),
                Color.parseColor("#3B82F6"),
                Color.parseColor("#F59E0B")
        });
        chartRatings.setData(new PieData(pieDataSet));
        chartRatings.getDescription().setEnabled(false);
        chartRatings.animateXY(600, 600);
        chartRatings.invalidate();
    }

    private void exportPdfReport() {
        if (scrollView == null) return;
        try {
            Bitmap bitmap = Bitmap.createBitmap(scrollView.getWidth(), scrollView.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            scrollView.draw(canvas);

            PdfDocument pdfDocument = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(bitmap.getWidth(), bitmap.getHeight(), 1).create();
            PdfDocument.Page page = pdfDocument.startPage(pageInfo);

            Canvas pdfCanvas = page.getCanvas();
            pdfCanvas.drawBitmap(bitmap, 0, 0, null);
            pdfDocument.finishPage(page);

            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            File pdfFile = new File(downloadsDir, "Teacher_Analytics_Report_" + System.currentTimeMillis() + ".pdf");

            FileOutputStream fos = new FileOutputStream(pdfFile);
            pdfDocument.writeTo(fos);
            pdfDocument.close();
            fos.close();

            showToast("Đã xuất báo cáo PDF: " + pdfFile.getName());
        } catch (Exception e) {
            showError("Lỗi xuất PDF: " + e.getMessage());
        }
    }
}
