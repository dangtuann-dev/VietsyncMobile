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
        holder.tvLessonTitle.setText(item.getLessonTitle());
        holder.tvCourseTitle.setText(item.getCourseTitle());
        holder.tvSize.setText(item.getSize());
        holder.tvDate.setText(item.getDate());

        holder.btnDelete.setOnClickListener(v -> {
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
        TextView tvLessonTitle;
        TextView tvCourseTitle;
        TextView tvSize;
        TextView tvDate;
        ImageView btnDelete;

        public DownloadViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLessonTitle = itemView.findViewById(R.id.tvLessonTitle);
            tvCourseTitle = itemView.findViewById(R.id.tvCourseTitle);
            tvSize = itemView.findViewById(R.id.tvSize);
            tvDate = itemView.findViewById(R.id.tvDate);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
