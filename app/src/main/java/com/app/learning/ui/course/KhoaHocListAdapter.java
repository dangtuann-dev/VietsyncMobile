package com.app.learning.ui.course;

import android.content.Context;
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

public class KhoaHocListAdapter extends RecyclerView.Adapter<KhoaHocListAdapter.KhoaHocListViewHolder> {

    private final List<Course> danhSachKhoaHoc = new ArrayList<>();
    private final SuKienClickKhoaHoc boLangNghe;

    public interface SuKienClickKhoaHoc {
        void khiClickKhoaHoc(Course course);
    }

    public KhoaHocListAdapter(SuKienClickKhoaHoc boLangNghe) {
        this.boLangNghe = boLangNghe;
    }

    public void capNhatDanhSach(List<Course> newCourses) {
        danhSachKhoaHoc.clear();
        if (newCourses != null) {
            danhSachKhoaHoc.addAll(newCourses);
        }
        notifyDataSetChanged();
    }

    public void themDanhSach(List<Course> moreCourses) {
        if (moreCourses != null && !moreCourses.isEmpty()) {
            int startSize = danhSachKhoaHoc.size();
            danhSachKhoaHoc.addAll(moreCourses);
            notifyItemRangeInserted(startSize, moreCourses.size());
        }
    }

    public List<Course> layDanhSach() {
        return danhSachKhoaHoc;
    }

    @NonNull
    @Override
    public KhoaHocListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_course_list, parent, false);
        return new KhoaHocListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KhoaHocListViewHolder holder, int position) {
        Course course = danhSachKhoaHoc.get(position);
        holder.lienKet(course, boLangNghe);
    }

    @Override
    public int getItemCount() {
        return danhSachKhoaHoc.size();
    }

    public static class KhoaHocListViewHolder extends RecyclerView.ViewHolder {

        private final ImageView ivThumbnail;
        private final TextView tvCategory;
        private final TextView tvTitle;
        private final ImageView ivInstructorAvatar;
        private final TextView tvInstructor;
        private final TextView tvRating;
        private final TextView tvEnrolledCount;
        private final TextView tvPrice;

        public KhoaHocListViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumbnail = itemView.findViewById(R.id.iv_thumbnail);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvTitle = itemView.findViewById(R.id.tv_title);
            ivInstructorAvatar = itemView.findViewById(R.id.iv_instructor_avatar);
            tvInstructor = itemView.findViewById(R.id.tv_instructor);
            tvRating = itemView.findViewById(R.id.tv_rating);
            tvEnrolledCount = itemView.findViewById(R.id.tv_enrolled_count);
            tvPrice = itemView.findViewById(R.id.tv_price);
        }

        public void lienKet(Course course, SuKienClickKhoaHoc boLangNghe) {
            tvTitle.setText(course.getTitle());
            tvCategory.setText(layTenDanhMuc(course.getCategoryId()));

            String instructorName = "Giảng viên";
            String avatarUrl = null;
            if (course.getInstructor() != null) {
                if (course.getInstructor().getFullName() != null) {
                    instructorName = course.getInstructor().getFullName();
                }
                avatarUrl = course.getInstructor().getAvatarUrl();
            }
            tvInstructor.setText(instructorName);

            tvRating.setText(String.valueOf(course.getRating()));
            tvEnrolledCount.setText(String.format("(%d học viên)", course.getEnrolledCount()));

            if (course.getPrice() == 0) {
                tvPrice.setText("Miễn phí");
            } else {
                tvPrice.setText(String.format("%,.0fđ", course.getPrice()));
            }

            Context context = itemView.getContext();
            Glide.with(context)
                    .load(course.getThumbnail())
                    .placeholder(R.drawable.ic_logo_placeholder)
                    .error(R.drawable.ic_logo_placeholder)
                    .into(ivThumbnail);

            Glide.with(context)
                    .load(avatarUrl)
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .error(R.drawable.ic_profile_placeholder)
                    .into(ivInstructorAvatar);

            itemView.setOnClickListener(v -> {
                if (boLangNghe != null) {
                    boLangNghe.khiClickKhoaHoc(course);
                }
            });
        }

        private String layTenDanhMuc(Long categoryId) {
            if (categoryId == null) return "Khóa học";
            switch (categoryId.intValue()) {
                case 1: return "Công nghệ thông tin";
                case 2: return "Kinh doanh";
                case 3: return "Thiết kế đồ họa";
                case 4: return "Ngoại ngữ";
                default: return "Khóa học";
            }
        }
    }
}
