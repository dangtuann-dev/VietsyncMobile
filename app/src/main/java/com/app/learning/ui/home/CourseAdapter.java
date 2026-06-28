package com.app.learning.ui.home;

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
import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private final List<Course> courses;

    public CourseAdapter(List<Course> courses) {
        this.courses = courses;
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course_horizontal, parent, false);
        return new CourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courses.get(position);
        holder.tvTitle.setText(course.getTitle());
        holder.tvLevel.setText(course.getLevel());
        holder.tvDuration.setText(course.getDuration() + " giờ");
        holder.tvRating.setText(String.valueOf(course.getRating()));

        if (course.getPrice() == 0) {
            holder.tvPrice.setText("Miễn phí");
        } else {
            holder.tvPrice.setText(String.format("%,.0fđ", course.getPrice()));
        }

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

    static class CourseViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumbnail;
        TextView tvLevel;
        TextView tvTitle;
        TextView tvDuration;
        TextView tvRating;
        TextView tvPrice;

        CourseViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumbnail = itemView.findViewById(R.id.iv_thumbnail);
            tvLevel = itemView.findViewById(R.id.tv_level);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDuration = itemView.findViewById(R.id.tv_duration);
            tvRating = itemView.findViewById(R.id.tv_rating);
            tvPrice = itemView.findViewById(R.id.tv_price);
        }
    }
}
