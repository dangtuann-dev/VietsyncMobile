package com.app.learning.ui.profile;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.ProgressBar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.app.learning.data.api.Resource;
import com.app.learning.data.model.Certificate;
import com.app.learning.data.model.User;
import com.app.learning.data.repository.UserRepository;
import com.app.learning.ui.base.BaseActivity;
import com.app.learning.utils.UserPreference;
import com.example.vietsyncmobile.R;

import java.util.ArrayList;
import java.util.List;

public class MyCertificatesActivity extends BaseActivity {

    private RecyclerView rvCertificates;
    private ProgressBar progressBar;
    private View layoutEmpty;
    private View btnBack;

    private UserRepository userRepository;
    private UserPreference userPreference;
    private List<Certificate> certificateList;
    private CertificateAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_certificates;
    }

    @Override
    protected void initViews() {
        rvCertificates = findViewById(R.id.rvCertificates);
        progressBar = findViewById(R.id.progressBar);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> onBackPressed());

        userRepository = new UserRepository(this);
        userPreference = UserPreference.getInstance(this);
        certificateList = new ArrayList<>();

        rvCertificates.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CertificateAdapter(certificateList, this::openCertificateUrl);
        rvCertificates.setAdapter(adapter);

        loadCertificates();
    }

    @Override
    protected void initObservers() {

    }

    private void loadCertificates() {
        User user = userPreference.getUserProfile();
        if (user == null) {
            showToast("Vui lòng đăng nhập lại để xem chứng chỉ.");
            finish();
            return;
        }

        userRepository.getCertificates(user.getId()).observe(this, resource -> {
            if (resource.isLoading()) {
                progressBar.setVisibility(View.VISIBLE);
                rvCertificates.setVisibility(View.GONE);
                layoutEmpty.setVisibility(View.GONE);
            } else if (resource.isSuccess()) {
                progressBar.setVisibility(View.GONE);
                certificateList.clear();
                if (resource.data != null && !resource.data.isEmpty()) {
                    certificateList.addAll(resource.data);
                    adapter.notifyDataSetChanged();
                    rvCertificates.setVisibility(View.VISIBLE);
                    layoutEmpty.setVisibility(View.GONE);
                } else {
                    rvCertificates.setVisibility(View.GONE);
                    layoutEmpty.setVisibility(View.VISIBLE);
                }
            } else if (resource.isError()) {
                progressBar.setVisibility(View.GONE);
                rvCertificates.setVisibility(View.GONE);
                layoutEmpty.setVisibility(View.VISIBLE);
                showToast(resource.error != null ? resource.error.getMessage() : "Lỗi tải chứng chỉ từ máy chủ");
            }
        });
    }

    private void openCertificateUrl(Certificate certificate) {
        String url = certificate.getCertificateUrl();
        if (url != null && !url.trim().isEmpty()) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            } catch (Exception e) {
                showToast("Không thể mở liên kết chứng chỉ: " + e.getLocalizedMessage());
            }
        } else {
            showToast("Liên kết chứng chỉ không hợp lệ.");
        }
    }
}
