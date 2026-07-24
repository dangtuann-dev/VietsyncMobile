package com.app.learning.ui.download;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vietsyncmobile.R;
import com.app.learning.data.local.DownloadEntity;

import java.util.ArrayList;
import java.util.List;

public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.DownloadViewHolder> {

    public interface OnDownloadActionListener {
        void onDelete(DownloadEntity download);
        void onClick(DownloadEntity download);
    }

    private final List<DownloadEntity> downloadList = new ArrayList<>();
    private final OnDownloadActionListener listener;

    public DownloadAdapter(OnDownloadActionListener listener) {
        this.listener = listener;
    }

    public void setDownloads(List<DownloadEntity> downloads) {
        this.downloadList.clear();
        if (downloads != null) {
            this.downloadList.addAll(downloads);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DownloadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_download, parent, false);
        return new DownloadViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DownloadViewHolder holder, int position) {
        DownloadEntity model = downloadList.get(position);
        holder.tvTitle.setText(model.getTitle());
        long sizeMb = model.getTotalBytes() > 0 ? model.getTotalBytes() / (1024 * 1024) : 15;
        holder.tvSizeStatus.setText(sizeMb + " MB • " + model.getStatus());
        holder.progressBar.setProgress(model.getProgress());

        holder.btnCancelDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(model);
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(model);
        });
    }

    @Override
    public int getItemCount() {
        return downloadList.size();
    }

    static class DownloadViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvSizeStatus;
        ProgressBar progressBar;
        ImageButton btnCancelDelete;

        public DownloadViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvSizeStatus = itemView.findViewById(R.id.tvSizeStatus);
            progressBar = itemView.findViewById(R.id.progressBar);
            btnCancelDelete = itemView.findViewById(R.id.btnCancelDelete);
        }
    }
}
