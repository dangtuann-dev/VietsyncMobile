package com.app.learning.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.app.learning.data.api.Resource;
import com.app.learning.data.model.User;
import com.app.learning.ui.base.BaseActivity;
import com.app.learning.MainActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.example.vietsyncmobile.R;

/**
 * RegisterActivity presents a premium‑styled registration screen.
 * It validates inputs (full name, email, password, confirm password, role, terms) and
 * delegates registration to RegisterViewModel which communicates with UserRepository.
 */
public class RegisterActivity extends BaseActivity {

    private TextInputLayout tilFullName, tilEmail, tilPassword, tilConfirmPassword;
    private TextInputEditText edtFullName, edtEmail, edtPassword, edtConfirmPassword;
    private RadioGroup rgRole;
    private RadioButton rbStudent, rbTeacher;
    private CheckBox cbTerms;
    private Button btnRegister;
    private TextView tvLogin;

    private RegisterViewModel registerViewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void initViews() {
        tilFullName = findViewById(R.id.til_full_name);
        tilEmail = findViewById(R.id.til_email);
        tilPassword = findViewById(R.id.til_password);
        tilConfirmPassword = findViewById(R.id.til_confirm_password);

        edtFullName = findViewById(R.id.edt_full_name);
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        edtConfirmPassword = findViewById(R.id.edt_confirm_password);

        rgRole = findViewById(R.id.rg_role);
        rbStudent = findViewById(R.id.rb_student);
        rbTeacher = findViewById(R.id.rb_teacher);
        cbTerms = findViewById(R.id.cb_terms);
        btnRegister = findViewById(R.id.btn_register);
        tvLogin = findViewById(R.id.tv_login);

        registerViewModel = new ViewModelProvider(this, new RegisterViewModel.Factory(this)).get(RegisterViewModel.class);

        btnRegister.setOnClickListener(v -> attemptRegister());
        tvLogin.setOnClickListener(v -> navigateToLogin());
    }

    @Override
    protected void initObservers() {
        observeViewModel(registerViewModel);
        registerViewModel.getRegisterResult().observe(this, resource -> {
            if (resource != null) {
                if (resource.isSuccess()) {
                    showToast("Đăng ký và đăng nhập thành công!");
                    navigateToHome();
                } else if (resource.isError()) {
                    // error is handled by BaseActivity's observeViewModel
                }
            }
        });
    }

    private void attemptRegister() {
        if (validateInputs()) {
            String fullName = edtFullName.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            String role = (rbTeacher.isChecked()) ? "teacher" : "student";
            registerViewModel.register(fullName, email, password, role);
        }
    }

    private boolean validateInputs() {
        boolean isValid = true;
        String fullName = edtFullName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String confirm = edtConfirmPassword.getText().toString().trim();

        if (fullName.length() < 2) {
            tilFullName.setError("Họ và tên ít nhất 2 ký tự");
            isValid = false;
        } else {
            tilFullName.setError(null);
        }

        if (email.isEmpty()) {
            tilEmail.setError("Email không được để trống");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Email không đúng định dạng");
            isValid = false;
        } else {
            tilEmail.setError(null);
        }

        if (password.length() < 8) {
            tilPassword.setError("Mật khẩu ít nhất 8 ký tự");
            isValid = false;
        } else {
            tilPassword.setError(null);
        }

        if (!password.equals(confirm)) {
            tilConfirmPassword.setError("Mật khẩu không khớp");
            isValid = false;
        } else {
            tilConfirmPassword.setError(null);
        }

        if (!cbTerms.isChecked()) {
            showError("Vui lòng đồng ý với Điều khoản và Chính sách");
            isValid = false;
        }

        return isValid;
    }

    private void navigateToLogin() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    private void navigateToHome() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
