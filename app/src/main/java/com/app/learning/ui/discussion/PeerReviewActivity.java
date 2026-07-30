package com.app.learning.ui.discussion;

import android.widget.RatingBar;
import android.widget.TextView;

import com.example.vietsyncmobile.R;
import com.app.learning.data.model.PeerReviewModel;
import com.app.learning.data.repository.PeerReviewRepository;
import com.app.learning.ui.base.BaseActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PeerReviewActivity extends BaseActivity {

    private TextView tvPeerAlias, tvSubmissionContent;
    private RatingBar rbClarity, rbAccuracy, rbCompleteness;
    private TextInputEditText etReviewComment;
    private MaterialButton btnSubmitReview;

    private PeerReviewRepository repository;
    private String submissionId = "sub_101";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_peer_review;
    }

    @Override
    protected void initViews() {
        tvPeerAlias = findViewById(R.id.tvPeerAlias);
        tvSubmissionContent = findViewById(R.id.tvSubmissionContent);
        rbClarity = findViewById(R.id.rbClarity);
        rbAccuracy = findViewById(R.id.rbAccuracy);
        rbCompleteness = findViewById(R.id.rbCompleteness);
        etReviewComment = findViewById(R.id.etReviewComment);
        btnSubmitReview = findViewById(R.id.btnSubmitReview);

        repository = new PeerReviewRepository(this);

        tvPeerAlias.setText("Bài làm từ: Học viên Ẩn danh #102");
        tvSubmissionContent.setText("Ứng dụng học tập Android tích hợp Supabase REST API, MPAndroidChart, và WorkManager background jobs. Vui lòng góp ý bổ sung.");

        btnSubmitReview.setOnClickListener(v -> submitReview());
    }

    @Override
    protected void initObservers() {

    }

    private void submitReview() {
        String comment = etReviewComment.getText() != null ? etReviewComment.getText().toString().trim() : "";
        if (comment.isEmpty()) {
            showError("Vui lòng nhập nhận xét bài làm");
            return;
        }

        showLoading("Đang gửi đánh giá...");
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());

        PeerReviewModel review = new PeerReviewModel(
                "rev_" + System.currentTimeMillis(),
                submissionId,
                "Người Đánh Giá #" + (1 + (int)(Math.random() * 3)),
                rbClarity.getRating(),
                rbAccuracy.getRating(),
                rbCompleteness.getRating(),
                comment,
                now
        );

        repository.submitReview(review, new PeerReviewRepository.RepositoryCallback<PeerReviewModel>() {
            @Override
            public void onSuccess(PeerReviewModel data) {
                hideLoading();
                showToast("Cảm ơn bạn đã đóng góp nhận xét!");
                finish();
            }

            @Override
            public void onError(String message) {
                hideLoading();
                showError("Lỗi gửi đánh giá: " + message);
            }
        });
    }
}
