package com.app.learning.ui.teacher.course;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.learning.data.model.Course;
import com.bumptech.glide.Glide;
import com.example.vietsyncmobile.R;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;
import java.util.List;

public class ManageCourseAdapter extends RecyclerView.Adapter<ManageCourseAdapter.CourseViewHolder> {

    private List<Course> courses = new ArrayList<>();
    private final OnCourseActionListener listener;

    public interface OnCourseActionListener {
        void onEdit(Course course);
        void onDelete(Course course);
    }

    public ManageCourseAdapter(OnCourseActionListener listener) {
        this.listener = listener;
    }

    public void setCourses(List<Course> courses) {
        this.courses = courses;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_manage_course, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courses.get(position);
        holder.bind(course);
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    class CourseViewHolder extends RecyclerView.ViewHolder {
        private final ShapeableImageView ivThumbnail;
        private final TextView tvTitle;
        private final TextView tvLevelAndCategory;
        private final TextView tvPrice;
        private final ImageButton btnEdit;
        private final ImageButton btnDelete;

        public CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumbnail = itemView.findViewById(R.id.ivThumbnail);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvLevelAndCategory = itemView.findViewById(R.id.tvLevelAndCategory);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);

            btnEdit.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onEdit(courses.get(position));
                }
            });

            btnDelete.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onDelete(courses.get(position));
                }
            });
        }

        public void bind(Course course) {
            tvTitle.setText(course.getTitle());
            
            String level = course.getLevel() != null ? course.getLevel() : "Beginner";
            tvLevelAndCategory.setText(level); // Can add category here if joined

            if (course.getPrice() == 0) {
                tvPrice.setText("Miễn phí");
            } else {
                tvPrice.setText(String.format("%,.0f đ", course.getPrice()));
            }

            if (course.getThumbnail() != null && !course.getThumbnail().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(course.getThumbnail())
                        .placeholder(R.drawable.ic_image_placeholder)
                        .into(ivThumbnail);
            } else {
                ivThumbnail.setImageResource(R.drawable.ic_image_placeholder);
            }
        }
    }
}
