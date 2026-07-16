package com.app.learning.ui.course;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;

import com.app.learning.data.api.Resource;
import com.app.learning.data.model.Course;
import com.app.learning.data.repository.EnrollmentRepository;
import com.app.learning.ui.base.BaseActivity;
import com.app.learning.utils.UserPreference;
import com.example.vietsyncmobile.R;
import com.google.android.material.button.MaterialButton;

public class PaymentActivity extends BaseActivity {

    private TextView tvCourseTitle;
    private TextView tvOriginalPrice;
    private TextView tvDiscount;
    private TextView tvTotalPrice;
    private RadioGroup rgPaymentMethods;
    private MaterialButton btnConfirm;

    private Course course;
    private String userId;
    private EnrollmentRepository enrollmentRepository;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_payment;
    }

    @Override
    protected void initViews() {
        tvCourseTitle = findViewById(R.id.tv_payment_course_title);
        tvOriginalPrice = findViewById(R.id.tv_payment_original_price);
        tvDiscount = findViewById(R.id.tv_payment_discount);
        tvTotalPrice = findViewById(R.id.tv_payment_total_price);
        rgPaymentMethods = findViewById(R.id.rg_payment_methods);
        btnConfirm = findViewById(R.id.btn_payment_confirm);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        course = (Course) getIntent().getSerializableExtra("course");
        enrollmentRepository = new EnrollmentRepository(this);
        userId = UserPreference.getInstance(this).getUserProfile() != null 
                ? UserPreference.getInstance(this).getUserProfile().getId() 
                : null;

        if (course != null) {
            tvCourseTitle.setText(course.getTitle());
            tvOriginalPrice.setText(String.format("%,.0fđ", course.getPrice()));
            tvDiscount.setText("-0đ");
            tvTotalPrice.setText(String.format("%,.0fđ", course.getPrice()));
        }

        btnConfirm.setOnClickListener(v -> {
            if (userId == null) {
                Toast.makeText(this, "Vui lòng đăng nhập để thanh toán!", Toast.LENGTH_SHORT).show();
                return;
            }
            performMockPayment();
        });
    }

    @Override
    protected void initObservers() {
    }

    private void performMockPayment() {
        int checkedId = rgPaymentMethods.getCheckedRadioButtonId();
        String method = "Thẻ Quốc tế";
        if (checkedId == R.id.rb_momo) {
            method = "Ví MoMo";
        } else if (checkedId == R.id.rb_vnpay) {
            method = "VNPAY";
        }

        showLoading("Đang xử lý giao dịch qua " + method + "...");

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            enrollmentRepository.enrollInCourse(userId, course.getId()).observe(this, resource -> {
                if (resource != null) {
                    if (resource.status == Resource.Status.SUCCESS) {
                        hideLoading();
                        Toast.makeText(this, "Thanh toán thành công!", Toast.LENGTH_SHORT).show();

                        EnrollmentNotificationHelper.sendEnrollmentNotification(this, userId, course);

                        Intent intent = new Intent(PaymentActivity.this, EnrollmentSuccessActivity.class);
                        intent.putExtra("course_title", course.getTitle());
                        intent.putExtra("course_id", course.getId());
                        startActivity(intent);
                        finish();
                    } else if (resource.status == Resource.Status.ERROR) {
                        hideLoading();
                        showError(resource.error != null ? resource.error.getMessage() : "Thanh toán thất bại, vui lòng thử lại!");
                    }
                }
            });
        }, 1500);
    }
}
