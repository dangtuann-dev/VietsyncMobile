package com.app.learning.ui.certificate;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vietsyncmobile.R;
import com.app.learning.data.model.CertificateModel;
import com.app.learning.data.repository.CertificateRepository;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import android.widget.TextView;

public class CertificateVerifyActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private MaterialButton btnSelectImage, btnVerify;
    private TextInputEditText etCertId;
    private MaterialCardView cardResult;
    private TextView tvStatusHeader, tvUserName, tvCourseTitle, tvInstructor, tvIssuedDate;

    private CertificateRepository repository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificate_verify);

        toolbar = findViewById(R.id.toolbar);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnVerify = findViewById(R.id.btnVerify);
        etCertId = findViewById(R.id.etCertId);
        cardResult = findViewById(R.id.cardResult);
        
        tvStatusHeader = findViewById(R.id.tvStatusHeader);
        tvUserName = findViewById(R.id.tvUserName);
        tvCourseTitle = findViewById(R.id.tvCourseTitle);
        tvInstructor = findViewById(R.id.tvInstructor);
        tvIssuedDate = findViewById(R.id.tvIssuedDate);

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        repository = new CertificateRepository(this);

        btnVerify.setOnClickListener(v -> {
            String certId = etCertId.getText().toString().trim();
            if (certId.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập mã chứng chỉ!", Toast.LENGTH_SHORT).show();
                return;
            }
            performVerification(certId);
        });

        btnSelectImage.setOnClickListener(v -> {
            // Mock a QR scan from gallery photo
            Toast.makeText(this, "Đang phân tích QR code từ ảnh...", Toast.LENGTH_SHORT).show();
            // Let's use a mock ID for demo
            String mockCertId = "cert-demo-01";
            etCertId.setText(mockCertId);
            performVerification(mockCertId);
        });
    }

    private void performVerification(String certId) {
        repository.getCertificateById(certId, new CertificateRepository.CertificateCallback() {
            @Override
            public void onSuccess(CertificateModel certificate) {
                cardResult.setVisibility(View.VISIBLE);
                tvStatusHeader.setText("CHỨNG CHỈ HỢP LỆ");
                tvStatusHeader.setTextColor(0xFF10B981); // Green

                tvUserName.setText("Học viên: " + (certificate.getUserName() != null ? certificate.getUserName() : "N/A"));
                tvCourseTitle.setText("Khóa học: " + (certificate.getCourseTitle() != null ? certificate.getCourseTitle() : "N/A"));
                tvInstructor.setText("Giảng viên: " + (certificate.getInstructorName() != null ? certificate.getInstructorName() : "N/A"));
                tvIssuedDate.setText("Ngày cấp: " + (certificate.getIssuedAt() != null ? certificate.getIssuedAt() : "N/A"));
            }

            @Override
            public void onError(String error) {
                cardResult.setVisibility(View.VISIBLE);
                tvStatusHeader.setText("CHỨNG CHỈ KHÔNG HỢP LỆ");
                tvStatusHeader.setTextColor(0xFFEF4444); // Red

                tvUserName.setText("Mã lỗi: " + error);
                tvCourseTitle.setText("");
                tvInstructor.setText("");
                tvIssuedDate.setText("");
            }
        });
    }
}
