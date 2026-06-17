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
import com.app.learning.ui.home.HomeActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.example.vietsyncmobile.R;

/**
 * LoginActivity presents the login screen for the users.
 * It features EdX branding, inputs validation, MVVM state observers and custom focus animations.
 */
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
        // Initialize view references
        tilEmail = findViewById(R.id.til_email);
        tilPassword = findViewById(R.id.til_password);
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        cbRememberMe = findViewById(R.id.cb_remember_me);
        btnLogin = findViewById(R.id.btn_login);
        tvForgotPassword = findViewById(R.id.tv_forgot_password);
        tvRegister = findViewById(R.id.tv_register);

        // Bind viewmodel
        loginViewModel = new ViewModelProvider(this, new LoginViewModel.Factory(this)).get(LoginViewModel.class);

        // Connect click listeners
        btnLogin.setOnClickListener(v -> attemptLogin());
        tvForgotPassword.setOnClickListener(v -> showToast("Chức năng khôi phục mật khẩu đang được phát triển"));
        tvRegister.setOnClickListener(v -> navigateToRegister());

        // Setup real-time error clearing and custom micro-animations
        setupTextWatchers();
        setupFocusAnimations();
    }

    @Override
    protected void initObservers() {
        // Observe base loading & error states
        observeViewModel(loginViewModel);

        // Observe custom login results
        loginViewModel.getLoginResult().observe(this, resource -> {
            if (resource != null) {
                if (resource.isSuccess()) {
                    showToast("Đăng nhập thành công!");
                    navigateToHome();
                } else if (resource.isError()) {
                    // Handled automatically by observeViewModel(loginViewModel)
                    // but we can add specific handling if required
                }
            }
        });
    }

    /**
     * Conducts validation checks on user inputs and calls the login service.
     */
    private void attemptLogin() {
        if (validateInputs()) {
            String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();
            loginViewModel.login(email, password);
        }
    }

    /**
     * Standard regex and empty-check input validations.
     */
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

    /**
     * Setup TextWatcher to automatically clear TextInputLayout errors as user types.
     */
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

    /**
     * Setup custom micro-animations for input fields.
     * Scales up the input text layout on focus to create a premium tactile response.
     */
    private void setupFocusAnimations() {
        View.OnFocusChangeListener focusChangeListener = (view, hasFocus) -> {
            float scale = hasFocus ? 1.02f : 1.0f;
            float elevation = hasFocus ? dpToPx(4) : 0f;

            // Find parent TextInputLayout to animate the entire input card together
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
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    // Navigate to Register screen with slide animation
    private void navigateToRegister() {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }
}

