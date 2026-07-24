package com.app.learning.ui.download;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vietsyncmobile.R;
import com.app.learning.data.local.AppDatabase;
import com.app.learning.data.local.DownloadDao;
import com.app.learning.data.local.DownloadEntity;
import com.app.learning.utils.AppDownloadManager;

public class DownloadsFragment extends Fragment {

    private RecyclerView rvDownloads;
    private TextView tvEmpty;

    private DownloadAdapter adapter;
    private DownloadDao downloadDao;
    private AppDownloadManager downloadManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_downloads, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvDownloads = view.findViewById(R.id.rvDownloads);
        tvEmpty = view.findViewById(R.id.tvEmpty);

        downloadDao = AppDatabase.getInstance(requireContext()).downloadDao();
        downloadManager = new AppDownloadManager(requireContext());

        adapter = new DownloadAdapter(new DownloadAdapter.OnDownloadActionListener() {
            @Override
            public void onDelete(DownloadEntity download) {
                downloadManager.cancelDownload(download.getLessonId());
                Toast.makeText(requireContext(), "Đã xóa bản tải về", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onClick(DownloadEntity download) {
                Toast.makeText(requireContext(), "Đang mở file ngoại tuyến: " + download.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });

        rvDownloads.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvDownloads.setAdapter(adapter);

        downloadDao.getAllDownloads().observe(getViewLifecycleOwner(), downloads -> {
            if (downloads == null || downloads.isEmpty()) {
                tvEmpty.setVisibility(View.VISIBLE);
                adapter.setDownloads(downloads);
            } else {
                tvEmpty.setVisibility(View.GONE);
                adapter.setDownloads(downloads);
            }
        });
    }
}
