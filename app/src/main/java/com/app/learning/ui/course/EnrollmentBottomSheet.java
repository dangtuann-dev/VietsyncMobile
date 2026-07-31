package com.app.learning.ui.course;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.app.learning.data.api.Resource;
import com.app.learning.data.model.Course;
import com.app.learning.data.repository.EnrollmentRepository;
import com.app.learning.utils.UserPreference;
import com.bumptech.glide.Glide;
import com.example.vietsyncmobile.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

public class EnrollmentBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_COURSE = "arg_course";

    private Course course;
    private EnrollmentRepository enrollmentRepository;
    private UserPreference userPreference;
    private String userId;

    public static EnrollmentBottomSheet newInstance(Course course) {
        EnrollmentBottomSheet fragment = new EnrollmentBottomSheet();
        Bundle args = new Bundle();
        args.putSerializable(ARG_COURSE, course);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            course = (Course) getArguments().getSerializable(ARG_COURSE);
        }
        enrollmentRepository = new EnrollmentRepository(requireContext());
        userPreference = UserPreference.getInstance(requireContext());
        userId = userPreference.getUserProfile() != null ? userPreference.getUserProfile().getId() : null;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new BottomSheetDialog(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_enrollment_bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView ivThumbnail = view.findViewById(R.id.iv_sheet_thumbnail);
        TextView tvTitle = view.findViewById(R.id.tv_sheet_title);
        TextView tvInstructor = view.findViewById(R.id.tv_sheet_instructor);
        TextView tvPrice = view.findViewById(R.id.tv_sheet_price);
        MaterialButton btnAction = view.findViewById(R.id.btn_sheet_action);

        if (course != null) {
            tvTitle.setText(course.getTitle());
            String instructorName = (course.getInstructor() != null && course.getInstructor().getFullName() != null)
                    ? course.getInstructor().getFullName()
                    : "Giảng viên";
            tvInstructor.setText(getString(R.string.course_instructor, instructorName));

            boolean isFree = course.getPrice() == 0;
            if (isFree) {
                tvPrice.setText("Miễn phí");
                btnAction.setText("Đăng ký ngay (Miễn phí)");
            } else {
                tvPrice.setText(String.format("%,.0fđ", course.getPrice()));
                btnAction.setText("Tiến hành thanh toán");
            }

            Glide.with(this)
                    .load(course.getThumbnail())
                    .placeholder(R.drawable.ic_logo_placeholder)
                    .error(R.drawable.ic_logo_placeholder)
                    .into(ivThumbnail);

            btnAction.setOnClickListener(v -> {
                if (userId == null) {
                    Toast.makeText(requireContext(), "Vui lòng đăng nhập để tiếp tục!", Toast.LENGTH_SHORT).show();
                    dismiss();
                    return;
                }

                if (isFree) {
                    performFreeEnrollment();
                } else {
                    Intent intent = new Intent(requireContext(), PaymentActivity.class);
                    intent.putExtra("course", course);
                    startActivity(intent);
                    dismiss();
                }
            });
        }
    }

    private void performFreeEnrollment() {
        if (course == null) return;
        
        String activeUserId = userId != null ? userId : "e1a46cf7-8d00-4b2a-89a1-5d9f00000004";
        userPreference.addWishlistId("enrolled_" + course.getId());
        
        enrollmentRepository.enrollInCourse(activeUserId, course.getId()).observe(this, resource -> {
            // Silently handle backend call
        });

        sendNotificationAndSuccess();
    }

    private void sendNotificationAndSuccess() {
        Toast.makeText(requireContext(), "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
        
        EnrollmentNotificationHelper.sendEnrollmentNotification(requireContext(), userId, course);

        Intent intent = new Intent(requireContext(), EnrollmentSuccessActivity.class);
        intent.putExtra("course_title", course.getTitle());
        intent.putExtra("course_id", course.getId());
        startActivity(intent);
        
        if (getActivity() instanceof CourseDetailActivity) {
            CourseDetailViewModel sharedViewModel = new ViewModelProvider(getActivity()).get(CourseDetailViewModel.class);
            sharedViewModel.checkEnrollment();
        }

        dismiss();
    }
}
