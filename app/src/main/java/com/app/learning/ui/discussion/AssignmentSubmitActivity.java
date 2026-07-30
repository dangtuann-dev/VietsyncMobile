package com.app.learning.ui.discussion;

import android.content.Intent;
import android.net.Uri;
import android.widget.TextView;

import com.example.vietsyncmobile.R;
import com.app.learning.data.model.PeerSubmissionModel;
import com.app.learning.data.repository.PeerReviewRepository;
import com.app.learning.ui.base.BaseActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AssignmentSubmitActivity extends BaseActivity {

    private TextInputEditText etAssignmentContent;
    private MaterialButton btnAttachFile, btnSubmitAssignment;
    private TextView tvFileName;

    private Uri selectedFileUri;
    private PeerReviewRepository repository;
    private String assignmentId = "assign_101";

    @Override
    protected int getLayoutId() {
        return R.layout.activity_assignment_submit;
    }

    @Override
    protected void initViews() {
        etAssignmentContent = findViewById(R.id.etAssignmentContent);
        btnAttachFile = findViewById(R.id.btnAttachFile);
        btnSubmitAssignment = findViewById(R.id.btnSubmitAssignment);
        tvFileName = findViewById(R.id.tvFileName);

        repository = new PeerReviewRepository(this);

        btnAttachFile.setOnClickListener(v -> pickFile());
        btnSubmitAssignment.setOnClickListener(v -> submitAssignment());
    }

    @Override
    protected void initObservers() {

    }

    private void pickFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        String[] mimeTypes = {"application/pdf", "image/*"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        startActivityForResult(Intent.createChooser(intent, "Chọn bài làm"), 1001);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedFileUri = data.getData();
            tvFileName.setText(selectedFileUri.getLastPathSegment());
        }
    }

    private void submitAssignment() {
        String content = etAssignmentContent.getText() != null ? etAssignmentContent.getText().toString().trim() : "";
        if (content.isEmpty()) {
            showError("Vui lòng nhập nội dung bài làm");
            return;
        }

        showLoading("Đang nộp bài tập...");
        String now = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
        String fileUrl = selectedFileUri != null ? selectedFileUri.toString() : "";

        PeerSubmissionModel submission = new PeerSubmissionModel(
                "sub_" + System.currentTimeMillis(),
                assignmentId,
                "Học viên Ẩn danh #" + (100 + (int)(Math.random() * 900)),
                content,
                fileUrl,
                now
        );

        repository.submitAssignment(submission, new PeerReviewRepository.RepositoryCallback<PeerSubmissionModel>() {
            @Override
            public void onSuccess(PeerSubmissionModel data) {
                hideLoading();
                showToast("Nộp bài tập thành công!");
                finish();
            }

            @Override
            public void onError(String message) {
                hideLoading();
                showError("Lỗi nộp bài tập: " + message);
            }
        });
    }
}
