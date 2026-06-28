package com.app.learning.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.app.learning.data.model.Course;
import com.bumptech.glide.Glide;
import com.example.vietsyncmobile.R;
import java.util.List;

public class ContinueLearningAdapter extends RecyclerView.Adapter<ContinueLearningAdapter.ContinueViewHolder> {

    private final List<Course> courses;

    public ContinueLearningAdapter(List<Course> courses) {
        this.courses = courses;
    }

    @NonNull
    @Override
    public ContinueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course_continue, parent, false);
        return new ContinueViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContinueViewHolder holder, int position) {
        Course course = courses.get(position);
        holder.tvTitle.setText(course.getTitle());
        holder.tvDuration.setText(course.getDuration() + " giờ");

        int progress = 25 + (position * 17) % 70;
        holder.progressBar.setProgress(progress);
        holder.tvProgressText.setText("Đã học " + progress + "%");

        Glide.with(holder.itemView.getContext())
                .load(course.getThumbnail())
                .placeholder(R.drawable.ic_logo_placeholder)
                .error(R.drawable.ic_logo_placeholder)
                .into(holder.ivThumbnail);
    }

    @Override
    public int getItemCount() {
        return courses != null ? courses.size() : 0;
    }

    static class ContinueViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumbnail;
        TextView tvTitle;
        TextView tvDuration;
        ProgressBar progressBar;
        TextView tvProgressText;
        ImageButton btnResume;

        ContinueViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumbnail = itemView.findViewById(R.id.iv_thumbnail);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDuration = itemView.findViewById(R.id.tv_duration);
            progressBar = itemView.findViewById(R.id.pb_progress);
            tvProgressText = itemView.findViewById(R.id.tv_progress_text);
            btnResume = itemView.findViewById(R.id.btn_resume);
        }
    }
}
