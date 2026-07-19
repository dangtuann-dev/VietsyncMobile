package com.app.learning.ui.course;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.lifecycle.ViewModelProvider;
import com.app.learning.data.api.Resource;
import com.app.learning.data.model.Course;
import com.app.learning.data.model.User;
import com.app.learning.ui.base.BaseFragment;
import com.example.vietsyncmobile.R;
import com.bumptech.glide.Glide;

public class InstructorFragment extends BaseFragment {

    private ImageView ivAvatar;
    private TextView tvName, tvRole, tvBio;
    private CourseDetailViewModel viewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_instructor;
    }

    @Override
    protected void initViews(View view) {
        ivAvatar = view.findViewById(R.id.iv_avatar);
        tvName = view.findViewById(R.id.tv_instructor_name);
        tvRole = view.findViewById(R.id.tv_instructor_role);
        tvBio = view.findViewById(R.id.tv_instructor_bio);
    }

    @Override
    protected void initObservers() {
        viewModel = new ViewModelProvider(requireActivity()).get(CourseDetailViewModel.class);
        
        viewModel.getCourseDetail().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.status == Resource.Status.SUCCESS && resource.data != null) {
                displayInstructor(resource.data);
            }
        });
    }

    private void displayInstructor(Course course) {
        User instructor = course.getInstructor();
        if (instructor != null) {
            tvName.setText(instructor.getFullName() != null ? instructor.getFullName() : "Giảng viên");
            
            String roleStr = "Giảng viên EdX";
            if (instructor.getRole() != null) {
                if ("instructor".equalsIgnoreCase(instructor.getRole())) {
                    roleStr = "Giảng viên chuyên môn";
                } else if ("admin".equalsIgnoreCase(instructor.getRole())) {
                    roleStr = "Quản trị viên chuyên môn";
                }
            }
            tvRole.setText(roleStr);
            
            tvBio.setText((instructor.getBio() != null && !instructor.getBio().isEmpty())
                    ? instructor.getBio()
                    : "Giảng viên nhiều năm kinh nghiệm nghiên cứu và giảng dạy về lĩnh vực phát triển công nghệ di động và phần mềm hệ thống.");

            Glide.with(this)
                    .load(instructor.getAvatarUrl())
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .error(R.drawable.ic_profile_placeholder)
                    .into(ivAvatar);
        } else {
            tvName.setText("Giảng viên EdX");
            tvRole.setText("Giảng viên chuyên môn");
            tvBio.setText("Giảng viên nhiều năm kinh nghiệm nghiên cứu và giảng dạy về lĩnh vực phát triển công nghệ di động và phần mềm hệ thống.");
            ivAvatar.setImageResource(R.drawable.ic_profile_placeholder);
        }
    }
}
