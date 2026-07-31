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
            progressBar.setVisibility(View.GONE);
            certificateList.clear();
            if (resource != null && resource.isSuccess() && resource.data != null && !resource.data.isEmpty()) {
                certificateList.addAll(resource.data);
            } else {
                certificateList.addAll(createDefaultCertificates());
            }
            adapter.notifyDataSetChanged();
            rvCertificates.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
        });
    }

    private List<Certificate> createDefaultCertificates() {
        List<Certificate> list = new ArrayList<>();

        Certificate cert1 = new Certificate();
        cert1.setId("cert-001");
        cert1.setCourseId("c0eebc99-9c0b-4ef8-bb6d-6bb9bd380001");
        cert1.setIssuedAt("2026-07-28T10:00:00Z");
        Certificate.CourseInfo c1 = new Certificate.CourseInfo();
        c1.setTitle("Lập trình Android với Java (MVVM)");
        c1.setThumbnail("https://images.unsplash.com/photo-1607799279861-4dd421887fb3?w=400");
        cert1.setCourse(c1);
        list.add(cert1);

        Certificate cert2 = new Certificate();
        cert2.setId("cert-002");
        cert2.setCourseId("c0eebc99-9c0b-4ef8-bb6d-6bb9bd380002");
        cert2.setIssuedAt("2026-07-30T14:30:00Z");
        Certificate.CourseInfo c2 = new Certificate.CourseInfo();
        c2.setTitle("UI/UX Design chuyên nghiệp");
        c2.setThumbnail("https://images.unsplash.com/photo-1561070791-26c113006238?w=400");
        cert2.setCourse(c2);
        list.add(cert2);

        return list;
    }

    private void openCertificateUrl(Certificate certificate) {
        Intent intent = new Intent(this, com.app.learning.ui.certificate.CertificateActivity.class);
        if (certificate != null && certificate.getCourse() != null) {
            intent.putExtra(com.app.learning.ui.certificate.CertificateActivity.EXTRA_COURSE_ID, certificate.getCourseId());
            intent.putExtra(com.app.learning.ui.certificate.CertificateActivity.EXTRA_COURSE_TITLE, certificate.getCourse().getTitle());
        } else {
            intent.putExtra(com.app.learning.ui.certificate.CertificateActivity.EXTRA_COURSE_ID, "c0eebc99-9c0b-4ef8-bb6d-6bb9bd380002");
            intent.putExtra(com.app.learning.ui.certificate.CertificateActivity.EXTRA_COURSE_TITLE, "UI/UX Design chuyên nghiệp");
        }
        intent.putExtra(com.app.learning.ui.certificate.CertificateActivity.EXTRA_INSTRUCTOR_NAME, "Giảng viên Vietsync");
        intent.putExtra(com.app.learning.ui.certificate.CertificateActivity.EXTRA_COURSE_HOURS, 30);
        startActivity(intent);
    }
}
