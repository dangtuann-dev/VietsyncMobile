package com.app.learning.ui.course;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.app.learning.data.api.Resource;
import com.app.learning.utils.UserPreference;
import com.example.vietsyncmobile.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class WriteReviewBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_COURSE_ID = "course_id";

    private String courseId;
    private String userId;
    
    private RatingBar rbRating;
    private TextInputLayout tilComment;
    private TextInputEditText etComment;
    private ProgressBar pbSubmitting;
    private MaterialButton btnSubmit;
    
    private ReviewViewModel viewModel;
    private OnReviewSubmitListener listener;

    public interface OnReviewSubmitListener {
        void onReviewSubmitted();
    }

    public static WriteReviewBottomSheet newInstance(String courseId) {
        WriteReviewBottomSheet fragment = new WriteReviewBottomSheet();
        Bundle args = new Bundle();
        args.putString(ARG_COURSE_ID, courseId);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnReviewSubmitListener(OnReviewSubmitListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            courseId = getArguments().getString(ARG_COURSE_ID);
        }
        UserPreference userPreference = UserPreference.getInstance(requireContext());
        userId = userPreference.getUserProfile() != null ? userPreference.getUserProfile().getId() : null;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_write_review_bottom_sheet, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        rbRating = view.findViewById(R.id.rb_write_rating);
        tilComment = view.findViewById(R.id.til_comment);
        etComment = view.findViewById(R.id.et_write_comment);
        pbSubmitting = view.findViewById(R.id.pb_submitting);
        btnSubmit = view.findViewById(R.id.btn_submit_review);
        
        viewModel = new ViewModelProvider(this).get(ReviewViewModel.class);
        
        btnSubmit.setOnClickListener(v -> submitReview());
        
        viewModel.getSubmitResultLiveData().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case LOADING:
                        pbSubmitting.setVisibility(View.VISIBLE);
                        btnSubmit.setEnabled(false);
                        break;
                    case SUCCESS:
                        pbSubmitting.setVisibility(View.GONE);
                        btnSubmit.setEnabled(true);
                        Toast.makeText(requireContext(), "Đánh giá của bạn đã được gửi thành công!", Toast.LENGTH_SHORT).show();
                        if (listener != null) {
                            listener.onReviewSubmitted();
                        }
                        dismiss();
                        break;
                    case ERROR:
                        pbSubmitting.setVisibility(View.GONE);
                        btnSubmit.setEnabled(true);
                        String errorMsg = resource.error != null ? resource.error.getMessage() : "Có lỗi xảy ra, vui lòng thử lại!";
                        Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    private void submitReview() {
        if (userId == null || userId.isEmpty()) {
            userId = "user_guest_88";
        }
        
        float rating = rbRating.getRating();
        if (rating <= 0) {
            rating = 5.0f;
        }
        
        String comment = etComment.getText() != null ? etComment.getText().toString().trim() : "";
        if (comment.length() < 3) {
            tilComment.setError("Vui lòng nhập nhận xét (ít nhất 3 ký tự)!");
            return;
        }
        tilComment.setError(null);
        
        viewModel.submitReview(courseId, userId, rating, comment);
    }
}
