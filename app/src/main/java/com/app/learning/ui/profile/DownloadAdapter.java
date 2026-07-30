package com.app.learning.ui.profile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.app.learning.data.model.DownloadItem;
import com.example.vietsyncmobile.R;

import java.util.List;

public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.DownloadViewHolder> {

    private final List<DownloadItem> items;
    private final OnDownloadDeleteListener deleteListener;

    public interface OnDownloadDeleteListener {
        void onDelete(DownloadItem item, int position);
    }

    public DownloadAdapter(List<DownloadItem> items, OnDownloadDeleteListener deleteListener) {
        this.items = items;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public DownloadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_download, parent, false);
        return new DownloadViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DownloadViewHolder holder, int position) {
        DownloadItem item = items.get(position);
        holder.tvTitle.setText(item.getLessonTitle());
        holder.tvSizeStatus.setText(item.getCourseTitle() + " • " + item.getSize() + " • " + item.getDate());

        holder.btnCancelDelete.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDelete(item, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class DownloadViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;
        TextView tvSizeStatus;
        View btnCancelDelete;

        public DownloadViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvSizeStatus = itemView.findViewById(R.id.tvSizeStatus);
            btnCancelDelete = itemView.findViewById(R.id.btnCancelDelete);
        }
    }
}
