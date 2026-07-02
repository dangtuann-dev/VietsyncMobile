package com.app.learning.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.app.learning.data.model.Course;
import com.bumptech.glide.Glide;
import com.example.vietsyncmobile.R;
import java.util.ArrayList;
import java.util.List;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.CourseViewHolder> {

    private final List<Course> courses;
    private final boolean isHorizontal;

    /**
     * Backward-compatible constructor — defaults to horizontal layout (item_course_horizontal).
     * Used by HomeFragment.
     */
    public CourseAdapter(List<Course> courses) {
        this.courses = courses != null ? courses : new ArrayList<>();
        this.isHorizontal = true;
    }

    /**
     * Full constructor.
     * Pass isHorizontal=false to use the vertical card layout (item_course_card).
     * Used by CourseListFragment.
     */
    public CourseAdapter(List<Course> courses, boolean isHorizontal) {
        this.courses = courses != null ? courses : new ArrayList<>();
        this.isHorizontal = isHorizontal;
    }

    /** Replace the dataset and refresh. Used by CourseListFragment observer. */
    public void setCourses(List<Course> newCourses) {
        this.courses.clear();
        if (newCourses != null) {
            this.courses.addAll(newCourses);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutRes = isHorizontal
                ? R.layout.item_course_horizontal
                : R.layout.item_course_card;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false);
        return new CourseViewHolder(view, isHorizontal);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseViewHolder holder, int position) {
        Course course = courses.get(position);

        if (isHorizontal) {
            // ── item_course_horizontal ──────────────────────────────────────
            if (holder.tvTitle != null)    holder.tvTitle.setText(course.getTitle());
            if (holder.tvLevel != null)    holder.tvLevel.setText(course.getLevel());
            if (holder.tvDuration != null) holder.tvDuration.setText(course.getDuration() + " giờ");
            if (holder.tvRating != null)   holder.tvRating.setText(String.valueOf(course.getRating()));
            if (holder.tvPrice != null) {
                holder.tvPrice.setText(course.getPrice() == 0
                        ? "Miễn phí"
                        : String.format("%,.0fđ", course.getPrice()));
            }
            Glide.with(holder.itemView.getContext())
                    .load(course.getThumbnail())
                    .placeholder(R.drawable.ic_logo_placeholder)
                    .error(R.drawable.ic_logo_placeholder)
                    .into(holder.ivThumbnail);
        } else {
            // ── item_course_card ────────────────────────────────────────────
            if (holder.tvTitle != null) holder.tvTitle.setText(course.getTitle());

            if (holder.tvInstructor != null) {
                String name = (course.getInstructor() != null
                        && course.getInstructor().getFullName() != null)
                        ? course.getInstructor().getFullName()
                        : "";
                holder.tvInstructor.setText(name);
            }

            if (holder.ratingBar != null)     holder.ratingBar.setRating((float) course.getRating());
            if (holder.tvRatingCount != null) holder.tvRatingCount.setText(String.format("(%.1f)", course.getRating()));

            if (holder.tvPrice != null) {
                holder.tvPrice.setText(course.getPrice() == 0
                        ? "Miễn phí"
                        : String.format("%,.0fđ", course.getPrice()));
            }

            if (holder.tvStudentsCount != null) {
                holder.tvStudentsCount.setText(String.format("• %d học viên", course.getEnrolledCount()));
            }

            if (holder.tvBadgeLevel != null) holder.tvBadgeLevel.setText(course.getLevel());

            Glide.with(holder.itemView.getContext())
                    .load(course.getThumbnail())
                    .placeholder(R.drawable.ic_logo_placeholder)
                    .error(R.drawable.ic_logo_placeholder)
                    .into(holder.ivThumbnail);
        }
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    static class CourseViewHolder extends RecyclerView.ViewHolder {
        // Horizontal-layout fields (item_course_horizontal)
        ImageView ivThumbnail;
        TextView  tvTitle;
        TextView  tvLevel;
        TextView  tvDuration;
        TextView  tvRating;
        TextView  tvPrice;

        // Vertical card-layout fields (item_course_card)
        TextView  tvInstructor;
        RatingBar ratingBar;
        TextView  tvRatingCount;
        TextView  tvStudentsCount;
        TextView  tvBadgeLevel;

        CourseViewHolder(@NonNull View itemView, boolean isHorizontal) {
            super(itemView);
            if (isHorizontal) {
                ivThumbnail = itemView.findViewById(R.id.iv_thumbnail);
                tvTitle     = itemView.findViewById(R.id.tv_title);
                tvLevel     = itemView.findViewById(R.id.tv_level);
                tvDuration  = itemView.findViewById(R.id.tv_duration);
                tvRating    = itemView.findViewById(R.id.tv_rating);
                tvPrice     = itemView.findViewById(R.id.tv_price);
            } else {
                ivThumbnail    = itemView.findViewById(R.id.course_thumbnail);
                tvTitle        = itemView.findViewById(R.id.course_title);
                tvInstructor   = itemView.findViewById(R.id.course_instructor);
                ratingBar      = itemView.findViewById(R.id.course_rating);
                tvRatingCount  = itemView.findViewById(R.id.course_rating_count);
                tvPrice        = itemView.findViewById(R.id.course_price);
                tvStudentsCount = itemView.findViewById(R.id.course_students_count);
                tvBadgeLevel   = itemView.findViewById(R.id.course_badge_level);
            }
        }
    }
}
