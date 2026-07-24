package com.app.learning.ui.certificate;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vietsyncmobile.R;
import com.app.learning.data.model.CertificateModel;
import com.app.learning.data.repository.CertificateRepository;
import com.app.learning.utils.PdfGenerator;
import com.app.learning.utils.QrCodeUtils;
import com.app.learning.utils.ShareHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.io.InputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;

public class CertificateActivity extends AppCompatActivity {

    public static final String EXTRA_COURSE_ID = "extra_course_id";
    public static final String EXTRA_COURSE_TITLE = "extra_course_title";
    public static final String EXTRA_INSTRUCTOR_NAME = "extra_instructor_name";
    public static final String EXTRA_COURSE_HOURS = "extra_course_hours";

    private MaterialToolbar toolbar;
    private WebView webViewCertificate;
    private MaterialButton btnDownloadPdf;
    private MaterialButton btnShare;
    private ProgressBar progressBar;

    private CertificateRepository repository;
    private CertificateModel currentCertificate;
    private File generatedPdfFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_certificate);

        toolbar = findViewById(R.id.toolbar);
        webViewCertificate = findViewById(R.id.webViewCertificate);
        btnDownloadPdf = findViewById(R.id.btnDownloadPdf);
        btnShare = findViewById(R.id.btnShare);
        progressBar = findViewById(R.id.progressBar);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        WebSettings settings = webViewCertificate.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        settings.setAllowFileAccess(true);

        repository = new CertificateRepository(this);

        String courseId = getIntent().getStringExtra(EXTRA_COURSE_ID);
        String courseTitle = getIntent().getStringExtra(EXTRA_COURSE_TITLE);
        String instructorName = getIntent().getStringExtra(EXTRA_INSTRUCTOR_NAME);
        int hours = getIntent().getIntExtra(EXTRA_COURSE_HOURS, 20);

        if (courseId == null) courseId = "c0eebc99-9c0b-4ef8-bb6d-6bb9bd380001";
        if (courseTitle == null) courseTitle = "Lập trình Android với Java (MVVM)";
        if (instructorName == null) instructorName = "Dr. Nguyễn Minh Tuấn";

        loadCertificate(courseId, courseTitle, instructorName, hours);

        btnDownloadPdf.setOnClickListener(v -> downloadPdf());
        btnShare.setOnClickListener(v -> shareCertificate());
    }

    private void loadCertificate(String courseId, String courseTitle, String instructorName, int hours) {
        progressBar.setVisibility(View.VISIBLE);
        repository.checkEligibility(courseId, (isEligible, message) -> {
            if (isEligible) {
                repository.generateCertificate(courseId, courseTitle, instructorName, hours, new CertificateRepository.CertificateCallback() {
                    @Override
                    public void onSuccess(CertificateModel certificate) {
                        progressBar.setVisibility(View.GONE);
                        currentCertificate = certificate;
                        renderHtml(certificate);
                    }

                    @Override
                    public void onError(String error) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(CertificateActivity.this, "Lỗi: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(CertificateActivity.this, message, Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void renderHtml(CertificateModel cert) {
        try {
            InputStream is = getAssets().open("certificate_template.html");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();

            String htmlTemplate = new String(buffer, StandardCharsets.UTF_8);

            String qrBase64 = QrCodeUtils.generateQrBase64(cert.getCertificateUrl() != null ? cert.getCertificateUrl() : "https://vietsync.edu.vn", 160, 160);

            String htmlContent = htmlTemplate
                    .replace("{{USER_NAME}}", cert.getUserName() != null ? cert.getUserName() : "Học viên")
                    .replace("{{COURSE_TITLE}}", cert.getCourseTitle() != null ? cert.getCourseTitle() : "Khóa học")
                    .replace("{{CERTIFICATE_ID}}", cert.getId() != null ? cert.getId().substring(0, 8) : "CERT-1234")
                    .replace("{{COURSE_HOURS}}", String.valueOf(cert.getDurationHours() > 0 ? cert.getDurationHours() : 20))
                    .replace("{{ISSUE_DATE}}", cert.getIssuedAt() != null ? cert.getIssuedAt() : "2026-07-22")
                    .replace("{{INSTRUCTOR_NAME}}", cert.getInstructorName() != null ? cert.getInstructorName() : "Giảng viên")
                    .replace("{{QR_CODE_BASE64}}", qrBase64);

            webViewCertificate.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    // Pre-generate PDF file in cache
                    PdfGenerator.generatePdfFromWebView(CertificateActivity.this, webViewCertificate, "Certificate_" + cert.getCourseId(), new PdfGenerator.PdfCallback() {
                        @Override
                        public void onSuccess(File pdfFile) {
                            generatedPdfFile = pdfFile;
                        }

                        @Override
                        public void onError(String message) {}
                    });
                }
            });

            webViewCertificate.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Không thể đọc template chứng chỉ", Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadPdf() {
        if (webViewCertificate != null) {
            PdfGenerator.printWebView(this, webViewCertificate, "Certificate_" + (currentCertificate != null ? currentCertificate.getCourseId() : "Document"));
            Toast.makeText(this, "Đang khởi tạo in/tải PDF...", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareCertificate() {
        if (generatedPdfFile != null && generatedPdfFile.exists()) {
            ShareHelper.sharePdfFile(this, generatedPdfFile, "Chứng chỉ " + (currentCertificate != null ? currentCertificate.getCourseTitle() : ""));
        } else {
            PdfGenerator.generatePdfFromWebView(this, webViewCertificate, "Certificate_Share", new PdfGenerator.PdfCallback() {
                @Override
                public void onSuccess(File pdfFile) {
                    generatedPdfFile = pdfFile;
                    ShareHelper.sharePdfFile(CertificateActivity.this, pdfFile, "Chứng chỉ khóa học");
                }

                @Override
                public void onError(String message) {
                    Toast.makeText(CertificateActivity.this, "Không thể chia sẻ PDF: " + message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
