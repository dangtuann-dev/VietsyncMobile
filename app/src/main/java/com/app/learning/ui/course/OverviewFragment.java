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
        if (getContext() == null || course == null) return;
        cgSkills.removeAllViews();
        llWhatYouLearn.removeAllViews();
        llRequirements.removeAllViews();

        String title = course.getTitle() != null ? course.getTitle().toLowerCase() : "";

        String[] skills;
        String[] goals;
        String[] requirements;

        if (title.contains("python")) {
            skills = new String[]{"Python 3", "OOP Python", "Data Structures", "Automation", "Django / FastAPI", "Git"};
            goals = new String[]{
                "Thành thạo cú pháp và tư duy lập trình Python hiện đại",
                "Xây dựng script tự động hóa các công việc lặp đi lặp lại",
                "Làm việc với File, JSON, API và CSDL với Python",
                "Phát triển ứng dụng Web hoàn chỉnh bằng Framework Python"
            };
            requirements = new String[]{
                "Không yêu cầu kiến thức lập trình trước đó",
                "Máy tính cá nhân cài sẵn phần mềm Python 3 (miễn phí)"
            };
        } else if (title.contains("react") || title.contains("web") || title.contains("frontend")) {
            skills = new String[]{"HTML5 / CSS3", "JavaScript ES6+", "ReactJS", "Redux Toolkit", "RESTful API", "Tailwind CSS"};
            goals = new String[]{
                "Xây dựng giao diện Web chuẩn Responsive tương thích mọi thiết bị",
                "Làm chủ React Hooks, Component Lifecycle và State Management",
                "Tích hợp RESTful API xử lý dữ liệu động linh hoạt",
                "Tối ưu tốc độ tải trang và trải nghiệm người dùng Web"
            };
            requirements = new String[]{
                "Kiến thức máy tính cơ bản",
                "Cài sẵn phần mềm VS Code và trình duyệt Chrome"
            };
        } else if (title.contains("thiết kế") || title.contains("ui") || title.contains("ux") || title.contains("figma")) {
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
        } else if (title.contains("tiếng anh") || title.contains("ielts") || title.contains("ngoại ngữ") || title.contains("ngôn ngữ")) {
            skills = new String[]{"Giao tiếp Tiếng Anh", "Từ vựng chuyên ngành", "Phát âm chuẩn IPA", "Phản xạ tự nhiên", "Grammar", "Listening"};
            goals = new String[]{
                "Tự tin giao tiếp Tiếng Anh trong công việc và đời sống",
                "Phát âm chuẩn theo bảng phiên âm quốc tế IPA",
                "Nâng cao từ vựng chuyên ngành và cấu trúc câu thông dụng",
                "Tăng khả năng phản xạ nghe nói tự nhiên không cần dịch nhẩm"
            };
            requirements = new String[]{
                "Tinh thần kiên trì và chủ động luyện tập mỗi ngày",
                "Tai nghe và thiết bị có ghi âm âm thanh"
            };
        } else if (title.contains("kinh doanh") || title.contains("marketing") || title.contains("startup")) {
            skills = new String[]{"Digital Marketing", "SEO / SEM", "Facebook Ads", "Tư duy Kinh doanh", "Content Strategy", "Kế hoạch Tài chính"};
            goals = new String[]{
                "Xây dựng chiến lược kinh doanh và tiếp thị đa kênh",
                "Tối ưu chi phí quảng cáo và gia tăng tỷ lệ chuyển đổi đơn hàng",
                "Lên kế hoạch Content Marketing thu hút khách hàng tiềm năng",
                "Phân tích chỉ số tài chính và đo lường hiệu quả chiến dịch"
            };
            requirements = new String[]{
                "Đam mê kinh doanh và marketing online",
                "Máy tính hoặc điện thoại thông minh kết nối Internet"
            };
        } else {
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
