package com.app.learning.ui.pdf;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.vietsyncmobile.R;
import com.app.learning.utils.PdfBookmarkManager;
import com.app.learning.utils.PdfDownloadManager;
import com.google.android.material.appbar.MaterialToolbar;

import java.io.File;

public class PdfViewerActivity extends AppCompatActivity {

    public static final String EXTRA_PDF_URL = "extra_pdf_url";
    public static final String EXTRA_PDF_TITLE = "extra_pdf_title";

    private MaterialToolbar toolbar;
    private TextView tvPageIndicator;
    private ImageButton btnZoomOut, btnZoomIn, btnNightMode, btnScrollToggle;
    private ProgressBar progressBar;
    private WebView webViewPdf;

    private PdfBookmarkManager bookmarkManager;
    private String pdfUrl;
    private String pdfTitle;
    private boolean isNightMode = false;
    private boolean isHorizontalScroll = false;
    private int currentPage = 1;
    private int totalPages = 15;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);

        toolbar = findViewById(R.id.toolbar);
        tvPageIndicator = findViewById(R.id.tvPageIndicator);
        btnZoomOut = findViewById(R.id.btnZoomOut);
        btnZoomIn = findViewById(R.id.btnZoomIn);
        btnNightMode = findViewById(R.id.btnNightMode);
        btnScrollToggle = findViewById(R.id.btnScrollToggle);
        progressBar = findViewById(R.id.progressBar);
        webViewPdf = findViewById(R.id.webViewPdf);

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        pdfUrl = getIntent().getStringExtra(EXTRA_PDF_URL);
        pdfTitle = getIntent().getStringExtra(EXTRA_PDF_TITLE);

        if (pdfUrl == null) pdfUrl = "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf";
        if (pdfTitle == null) pdfTitle = "Tài liệu học tập.pdf";

        toolbar.setTitle(pdfTitle);
        bookmarkManager = new PdfBookmarkManager(this);

        WebSettings settings = webViewPdf.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);

        btnNightMode.setOnClickListener(v -> toggleNightMode());
        btnZoomIn.setOnClickListener(v -> webViewPdf.zoomIn());
        btnZoomOut.setOnClickListener(v -> webViewPdf.zoomOut());
        btnScrollToggle.setOnClickListener(v -> toggleScrollOrientation());

        loadPdf();
    }

    private void loadPdf() {
        progressBar.setVisibility(View.VISIBLE);
        PdfDownloadManager.downloadPdf(this, pdfUrl, new PdfDownloadManager.DownloadCallback() {
            @Override
            public void onProgress(int percent) {
                progressBar.setProgress(percent);
            }

            @Override
            public void onSuccess(File pdfFile) {
                progressBar.setVisibility(View.GONE);

                bookmarkManager.getBookmark(pdfUrl, page -> {
                    currentPage = page;
                    updatePageIndicator();

                    String googleDocsViewer = "https://docs.google.com/gview?embedded=true&url=" + pdfUrl;
                    webViewPdf.loadUrl(googleDocsViewer);
                });
            }

            @Override
            public void onError(String error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(PdfViewerActivity.this, "Lỗi tải PDF: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toggleNightMode() {
        isNightMode = !isNightMode;
        if (isNightMode) {
            float[] invertMatrix = {
                -1.0f,  0.0f,  0.0f, 0.0f, 255.0f,
                 0.0f, -1.0f,  0.0f, 0.0f, 255.0f,
                 0.0f,  0.0f, -1.0f, 0.0f, 255.0f,
                 0.0f,  0.0f,  0.0f, 1.0f,   0.0f
            };
            Paint paint = new Paint();
            paint.setColorFilter(new ColorMatrixColorFilter(new ColorMatrix(invertMatrix)));
            webViewPdf.setLayerType(View.LAYER_TYPE_HARDWARE, paint);
            Toast.makeText(this, "Chế độ đọc ban đêm: BẬT", Toast.LENGTH_SHORT).show();
        } else {
            webViewPdf.setLayerType(View.LAYER_TYPE_HARDWARE, null);
            Toast.makeText(this, "Chế độ đọc ban đêm: TẮT", Toast.LENGTH_SHORT).show();
        }
    }

    private void toggleScrollOrientation() {
        isHorizontalScroll = !isHorizontalScroll;
        Toast.makeText(this, isHorizontalScroll ? "Cuộn ngang" : "Cuộn dọc", Toast.LENGTH_SHORT).show();
    }

    private void updatePageIndicator() {
        tvPageIndicator.setText("Trang " + currentPage + " / " + totalPages);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (bookmarkManager != null && pdfUrl != null) {
            bookmarkManager.saveBookmark(pdfUrl, currentPage, totalPages);
        }
    }
}
