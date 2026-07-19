package com.app.learning.ui.learning;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;
import com.app.learning.data.api.Resource;
import com.app.learning.data.model.Course;
import com.app.learning.data.model.Enrollment;
import com.app.learning.data.repository.CourseRepository;
import com.bumptech.glide.Glide;
import com.example.vietsyncmobile.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import java.util.ArrayList;
import java.util.List;

public class InProgressAdapter extends RecyclerView.Adapter<InProgressAdapter.ViewHolder> {

    private final List<Enrollment> items = new ArrayList<>();
    private final LifecycleOwner lifecycleOwner;
    private final CourseRepository courseRepository;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Enrollment enrollment);
        void onContinueClick(Enrollment enrollment);
    }

    public InProgressAdapter(LifecycleOwner lifecycleOwner, OnItemClickListener listener) {
        this.lifecycleOwner = lifecycleOwner;
        this.courseRepository = new CourseRepository();
        this.listener = listener;
    }

    public void setItems(List<Enrollment> newItems) {
        this.items.clear();
        if (newItems != null) {
            this.items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_learning_progress, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Enrollment item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumbnail;
        TextView tvTitle;
        TextView tvInstructor;
        LinearProgressIndicator progressIndicator;
        TextView tvProgressPercent;
        TextView tvLessonsCount;
        MaterialButton btnContinue;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumbnail = itemView.findViewById(R.id.iv_thumbnail);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvInstructor = itemView.findViewById(R.id.tv_instructor);
            progressIndicator = itemView.findViewById(R.id.progress_indicator);
            tvProgressPercent = itemView.findViewById(R.id.tv_progress_percent);
            tvLessonsCount = itemView.findViewById(R.id.tv_lessons_count);
            btnContinue = itemView.findViewById(R.id.btn_continue);

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(items.get(pos));
                }
            });

            btnContinue.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && listener != null) {
                    listener.onContinueClick(items.get(pos));
                }
            });
        }

        public void bind(Enrollment enrollment) {
            Course course = enrollment.getCourse();
            if (course == null) return;

            tvTitle.setText(course.getTitle());
            tvInstructor.setText(course.getInstructor() != null ? course.getInstructor().getFullName() : "Giảng viên");
            progressIndicator.setProgress(enrollment.getProgressPercent());
            tvProgressPercent.setText(enrollment.getProgressPercent() + "%");

            if (course.getThumbnail() != null && !course.getThumbnail().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(course.getThumbnail())
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_background)
                        .into(ivThumbnail);
            } else {
                ivThumbnail.setImageResource(R.drawable.ic_launcher_background);
            }

            // Bind lessons count reactively
            tvLessonsCount.setText("Đang tải bài học...");
            courseRepository.getLessons(course.getId()).observe(lifecycleOwner, resource -> {
                if (resource != null && resource.status == Resource.Status.SUCCESS && resource.data != null) {
                    int total = resource.data.size();
                    int progress = enrollment.getProgressPercent();
                    int completed = (progress * total) / 100;
                    tvLessonsCount.setText(completed + "/" + total + " bài học");
                } else if (resource != null && resource.status == Resource.Status.ERROR) {
                    tvLessonsCount.setText("Không tải được số bài");
                }
            });
        }
    }
}
