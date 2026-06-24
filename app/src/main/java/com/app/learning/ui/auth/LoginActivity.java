package com.app.learning.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.app.learning.data.api.Resource;
import com.app.learning.ui.base.BaseActivity;
import com.app.learning.MainActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.example.vietsyncmobile.R;





public class LoginActivity extends BaseActivity {

    private TextInputLayout tilEmail;
    private TextInputLayout tilPassword;
    private TextInputEditText edtEmail;
    private TextInputEditText edtPassword;
    private CheckBox cbRememberMe;
    private android.widget.Button btnLogin;
    private TextView tvForgotPassword;
    private TextView tvRegister;

    private LoginViewModel loginViewModel;

    @Override
    protected int getLayoutId() {
        return R.id.btn_login == 0 ? 0 : R.layout.activity_login;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



        @Override
    protected void initViews() {

        tilEmail = findViewById(R.id.til_email);
        tilPassword = findViewById(R.id.til_password);
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        cbRememberMe = findViewById(R.id.cb_remember_me);
        btnLogin = findViewById(R.id.btn_login);
        tvForgotPassword = findViewById(R.id.tv_forgot_password);
        tvRegister = findViewById(R.id.tv_register);


        loginViewModel = new ViewModelProvider(this, new LoginViewModel.Factory(this)).get(LoginViewModel.class);


        btnLogin.setOnClickListener(v -> attemptLogin());
        tvForgotPassword.setOnClickListener(v -> navigateToForgotPassword());
        tvRegister.setOnClickListener(v -> navigateToRegister());


        setupTextWatchers();
        setupFocusAnimations();
    }

    @Override
    protected void initObservers() {

        observeViewModel(loginViewModel);


        loginViewModel.getLoginResult().observe(this, resource -> {
            if (resource != null) {
                if (resource.isSuccess()) {
                    showToast("Đăng nhập thành công!");
                    navigateToHome();
                } else if (resource.isError()) {


                }
            }
        });
    }




    private void attemptLogin() {
        if (validateInputs()) {
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            loginViewModel.login(email, password);
        }
    }




    private boolean validateInputs() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        boolean isValid = true;

        if (email.isEmpty()) {
            tilEmail.setError("Email không được để trống");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Email không đúng định dạng");
            isValid = false;
        } else {
            tilEmail.setError(null);
        }

        if (password.isEmpty()) {
            tilPassword.setError("Mật khẩu không được để trống");
            isValid = false;
        } else if (password.length() < 6) {
            tilPassword.setError("Mật khẩu phải chứa ít nhất 6 ký tự");
            isValid = false;
        } else {
            tilPassword.setError(null);
        }

        return isValid;
    }




    private void setupTextWatchers() {
        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilEmail.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        edtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilPassword.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }





    private void setupFocusAnimations() {
        View.OnFocusChangeListener focusChangeListener = (view, hasFocus) -> {
            float scale = hasFocus ? 1.02f : 1.0f;
            float elevation = hasFocus ? dpToPx(4) : 0f;


            View parent = (View) view.getParent();
            if (parent instanceof TextInputLayout) {
                parent.animate()
                        .scaleX(scale)
                        .scaleY(scale)
                        .translationZ(elevation)
                        .setDuration(150)
                        .start();
            }
        };

        edtEmail.setOnFocusChangeListener(focusChangeListener);
        edtPassword.setOnFocusChangeListener(focusChangeListener);
    }

    private float dpToPx(float dp) {
        return dp * getResources().getDisplayMetrics().density;
    }

    private void navigateToHome() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }


    private void navigateToRegister() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    private void navigateToForgotPassword() {
        Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}

