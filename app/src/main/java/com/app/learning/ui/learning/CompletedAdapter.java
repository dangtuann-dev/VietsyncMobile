package com.app.learning.ui.learning;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.app.learning.data.model.Course;
import com.app.learning.data.model.Enrollment;
import com.bumptech.glide.Glide;
import com.example.vietsyncmobile.R;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

public class CompletedAdapter extends RecyclerView.Adapter<CompletedAdapter.ViewHolder> {

    private final List<Enrollment> items = new ArrayList<>();
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Enrollment enrollment);
        void onViewCertificateClick(Enrollment enrollment);
        void onReviewClick(Enrollment enrollment);
    }

    public CompletedAdapter(OnItemClickListener listener) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_completed_course, parent, false);
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
        TextView tvCompletedDate;
        RatingBar ratingBar;
        MaterialButton btnViewCertificate;
        MaterialButton btnReview;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumbnail = itemView.findViewById(R.id.iv_thumbnail);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvCompletedDate = itemView.findViewById(R.id.tv_completed_date);
            ratingBar = itemView.findViewById(R.id.rating_bar);
            btnViewCertificate = itemView.findViewById(R.id.btn_view_certificate);
            btnReview = itemView.findViewById(R.id.btn_review);

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(items.get(pos));
                }
            });

            btnViewCertificate.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && listener != null) {
                    listener.onViewCertificateClick(items.get(pos));
                }
            });

            btnReview.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && listener != null) {
                    listener.onReviewClick(items.get(pos));
                }
            });
        }

        public void bind(Enrollment enrollment) {
            Course course = enrollment.getCourse();
            if (course == null) return;

            tvTitle.setText(course.getTitle());
            ratingBar.setRating((float) course.getRating());

            String completedAt = enrollment.getCompletedAt();
            if (completedAt != null && !completedAt.isEmpty()) {
                try {
                    if (completedAt.contains("T")) {
                        String datePart = completedAt.split("T")[0];
                        String[] parts = datePart.split("-");
                        if (parts.length == 3) {
                            tvCompletedDate.setText("Hoàn thành: " + parts[2] + "/" + parts[1] + "/" + parts[0]);
                        } else {
                            tvCompletedDate.setText("Hoàn thành: " + datePart);
                        }
                    } else {
                        tvCompletedDate.setText("Hoàn thành: " + completedAt);
                    }
                } catch (Exception e) {
                    tvCompletedDate.setText("Hoàn thành: " + completedAt);
                }
            } else {
                tvCompletedDate.setText("Hoàn thành: Gần đây");
            }

            if (course.getThumbnail() != null && !course.getThumbnail().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(course.getThumbnail())
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_background)
                        .into(ivThumbnail);
            } else {
                ivThumbnail.setImageResource(R.drawable.ic_launcher_background);
            }
        }
    }
}
