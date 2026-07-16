package com.app.learning.ui.course;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.lifecycle.ViewModelProvider;
import com.app.learning.data.api.Resource;
import com.app.learning.data.model.Course;
import com.app.learning.ui.base.BaseFragment;
import com.example.vietsyncmobile.R;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class OverviewFragment extends BaseFragment {

    private TextView tvDuration, tvLessons, tvStudents, tvLevel, tvDescription;
    private ChipGroup cgSkills;
    private LinearLayout llWhatYouLearn, llRequirements;
    private CourseDetailViewModel viewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_overview;
    }

    @Override
    protected void initViews(View view) {
        tvDuration = view.findViewById(R.id.tv_stat_duration);
        tvLessons = view.findViewById(R.id.tv_stat_lessons);
        tvStudents = view.findViewById(R.id.tv_stat_students);
        tvLevel = view.findViewById(R.id.tv_stat_level);
        tvDescription = view.findViewById(R.id.tv_description);
        cgSkills = view.findViewById(R.id.cg_skills);
        llWhatYouLearn = view.findViewById(R.id.ll_what_you_learn);
        llRequirements = view.findViewById(R.id.ll_requirements);
    }

    @Override
    protected void initObservers() {
        viewModel = new ViewModelProvider(requireActivity()).get(CourseDetailViewModel.class);
        
        viewModel.getCourseDetail().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.status == Resource.Status.SUCCESS && resource.data != null) {
                displayOverview(resource.data);
            }
        });

        viewModel.getCourseLessons().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.status == Resource.Status.SUCCESS && resource.data != null) {
                tvLessons.setText(resource.data.size() + " bài học");
            }
        });
    }

    private void displayOverview(Course course) {
        tvDuration.setText((course.getDuration() > 0 ? course.getDuration() : 20) + " giờ");
        tvStudents.setText(course.getEnrolledCount() + " học viên");
        
        String lvl = "Cơ bản";
        if ("intermediate".equalsIgnoreCase(course.getLevel())) lvl = "Trung cấp";
        else if ("advanced".equalsIgnoreCase(course.getLevel())) lvl = "Nâng cao";
        tvLevel.setText(lvl);
        
        tvDescription.setText(course.getDescription() != null ? course.getDescription() : "Không có mô tả chi tiết.");

        populateDynamicContent(course);
    }

    private void populateDynamicContent(Course course) {
        if (getContext() == null) return;
        cgSkills.removeAllViews();
        llWhatYouLearn.removeAllViews();
        llRequirements.removeAllViews();

        String[] skills;
        String[] goals;
        String[] requirements;

        if (course.getTitle().toLowerCase().contains("android") || course.getTitle().toLowerCase().contains("java")) {
            skills = new String[]{"Android SDK", "Java OOP", "MVVM Pattern", "Retrofit API", "Room Database", "Git"};
            goals = new String[]{
                "Hiểu rõ mô hình kiến trúc MVVM trong Android",
                "Cách gọi API bằng Retrofit và xử lý phản hồi bất đồng bộ",
                "Quản lý cơ sở dữ liệu cục bộ bằng Room DB",
                "Xây dựng giao diện Responsive với XML & Material Design"
            };
            requirements = new String[]{
                "Có kiến thức lập trình cơ bản (biến, vòng lặp, hàm)",
                "Đã học qua ngôn ngữ Java hoặc Kotlin cơ bản",
                "Máy tính cá nhân cài sẵn Android Studio"
            };
        } else {
            skills = new String[]{"Figma Pro", "Wireframing", "User Research", "Prototyping", "Color Theory", "Typography"};
            goals = new String[]{
                "Thiết kế giao diện đẹp và trực quan trên Figma",
                "Lên kế hoạch và thực hiện User Research hiệu quả",
                "Xây dựng Prototype tương tác cao cho Mobile & Web",
                "Hiểu nguyên lý phân bổ lưới, màu sắc và kiểu chữ chuyên nghiệp"
            };
            requirements = new String[]{
                "Không yêu cầu kiến thức lập trình trước đó",
                "Có tư duy sáng tạo và mong muốn học hỏi về trải nghiệm người dùng",
                "Tải và cài đặt phần mềm Figma (miễn phí)"
            };
        }

        // Add Chips
        for (String skill : skills) {
            Chip chip = new Chip(getContext());
            chip.setText(skill);
            chip.setChipBackgroundColorResource(R.color.surface);
            chip.setTextColor(getResources().getColor(R.color.text_primary));
            chip.setCloseIconVisible(false);
            chip.setClickable(false);
            cgSkills.addView(chip);
        }

        // Add What you'll learn bullets
        for (String goal : goals) {
            View bulletView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, llWhatYouLearn, false);
            TextView text = bulletView.findViewById(android.R.id.text1);
            text.setText("✓  " + goal);
            text.setTextSize(13);
            text.setTextColor(getResources().getColor(R.color.text_secondary));
            llWhatYouLearn.addView(bulletView);
        }

        // Add Requirements
        for (String req : requirements) {
            View reqView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, llRequirements, false);
            TextView text = reqView.findViewById(android.R.id.text1);
            text.setText("•  " + req);
            text.setTextSize(13);
            text.setTextColor(getResources().getColor(R.color.text_secondary));
            llRequirements.addView(reqView);
        }
    }
}
