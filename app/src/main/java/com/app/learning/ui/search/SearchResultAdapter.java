package com.app.learning.ui.search;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.learning.data.model.Course;
import com.bumptech.glide.Glide;
import com.example.vietsyncmobile.R;

import java.util.ArrayList;
import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {

    private List<Course> courseList = new ArrayList<>();
    private final OnCourseClickListener clickListener;

    public interface OnCourseClickListener {
        void onCourseClick(Course course);
    }

    public SearchResultAdapter(OnCourseClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setCourseList(List<Course> list) {
        this.courseList = list != null ? list : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_result, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Course course = courseList.get(position);

        holder.tvTitle.setText(course.getTitle());

        // Level translation / styling
        String levelDisplay = course.getLevel();
        if ("beginner".equalsIgnoreCase(levelDisplay)) {
            levelDisplay = "Mới bắt đầu";
        } else if ("intermediate".equalsIgnoreCase(levelDisplay)) {
            levelDisplay = "Trung cấp";
        } else if ("advanced".equalsIgnoreCase(levelDisplay)) {
            levelDisplay = "Nâng cao";
        } else if (levelDisplay == null) {
            levelDisplay = "Mọi cấp độ";
        }
        holder.tvLevel.setText(levelDisplay);

        // Instructor name
        if (course.getInstructor() != null && course.getInstructor().getFullName() != null) {
            holder.tvInstructor.setText(course.getInstructor().getFullName());
        } else {
            holder.tvInstructor.setText("Giảng viên");
        }

        // Rating and duration
        holder.tvRating.setText(String.valueOf(course.getRating()));
        holder.tvDuration.setText(course.getDuration() + " giờ");

        // Price formatting
        if (course.getPrice() == 0) {
            holder.tvPrice.setText("Miễn phí");
        } else {
            holder.tvPrice.setText(String.format("%,.0fđ", course.getPrice()));
        }

        // Thumbnail loading
        Glide.with(holder.itemView.getContext())
                .load(course.getThumbnail())
                .placeholder(R.drawable.ic_logo_placeholder)
                .error(R.drawable.ic_logo_placeholder)
                .into(holder.ivThumbnail);

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onCourseClick(course);
            }
        });
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumbnail;
        TextView tvLevel;
        TextView tvInstructor;
        TextView tvTitle;
        TextView tvRating;
        TextView tvDuration;
        TextView tvPrice;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumbnail = itemView.findViewById(R.id.iv_thumbnail);
            tvLevel = itemView.findViewById(R.id.tv_level);
            tvInstructor = itemView.findViewById(R.id.tv_instructor);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvRating = itemView.findViewById(R.id.tv_rating);
            tvDuration = itemView.findViewById(R.id.tv_duration);
            tvPrice = itemView.findViewById(R.id.tv_price);
        }
    }
}
