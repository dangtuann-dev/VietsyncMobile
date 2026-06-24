package com.app.learning.ui.profile;

import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.app.learning.data.model.DownloadItem;
import com.app.learning.ui.base.BaseActivity;
import com.example.vietsyncmobile.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class DownloadHistoryActivity extends BaseActivity {

    private RecyclerView rvDownloads;
    private View layoutEmpty;
    private View btnBack;
    private TextView btnClearAll;

    private List<DownloadItem> downloadList;
    private DownloadAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_download_history;
    }

    @Override
    protected void initViews() {
        rvDownloads = findViewById(R.id.rvDownloads);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        btnBack = findViewById(R.id.btnBack);
        btnClearAll = findViewById(R.id.btnClearAll);

        btnBack.setOnClickListener(v -> onBackPressed());

        btnClearAll.setOnClickListener(v -> showClearAllConfirmation());

        setupDownloadList();
    }

    @Override
    protected void initObservers() {

    }

    private void setupDownloadList() {
        downloadList = new ArrayList<>();

        downloadList.add(new DownloadItem("1", "Bài 1: Giới thiệu khóa học & Thiết lập môi trường", "Lập trình Android với Java (MVVM)", "45.2 MB", "22/06/2026"));
        downloadList.add(new DownloadItem("2", "Bài 2: Cấu trúc mô hình MVVM trong Android", "Lập trình Android với Java (MVVM)", "82.1 MB", "22/06/2026"));
        downloadList.add(new DownloadItem("3", "Bài 1: Khái niệm UI và UX cơ bản", "UI/UX Design chuyên nghiệp", "28.5 MB", "21/06/2026"));

        rvDownloads.setLayoutManager(new LinearLayoutManager(this));
        adapter = new DownloadAdapter(downloadList, (item, position) -> showDeleteConfirmation(item, position));
        rvDownloads.setAdapter(adapter);

        updateViews();
    }

    private void showDeleteConfirmation(DownloadItem item, int position) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Xóa tệp tải xuống")
                .setMessage("Bạn có chắc chắn muốn xóa bài học này khỏi bộ nhớ thiết bị?")
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Xóa", (dialog, which) -> {
                    downloadList.remove(position);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position, downloadList.size());
                    showToast("Đã xóa tệp: " + item.getLessonTitle());
                    updateViews();
                })
                .show();
    }

    private void showClearAllConfirmation() {
        if (downloadList.isEmpty()) return;

        new MaterialAlertDialogBuilder(this)
                .setTitle("Xóa tất cả")
                .setMessage("Bạn có chắc chắn muốn xóa toàn bộ lịch sử tải xuống?")
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Xóa tất cả", (dialog, which) -> {
                    int size = downloadList.size();
                    downloadList.clear();
                    adapter.notifyItemRangeRemoved(0, size);
                    showToast("Đã xóa toàn bộ tệp tải xuống");
                    updateViews();
                })
                .show();
    }

    private void updateViews() {
        if (downloadList.isEmpty()) {
            rvDownloads.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
            btnClearAll.setVisibility(View.GONE);
        } else {
            rvDownloads.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
            btnClearAll.setVisibility(View.VISIBLE);
        }
    }
}
