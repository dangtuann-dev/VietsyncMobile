package com.app.learning.ui.auth;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.app.learning.data.api.Resource;
import com.app.learning.ui.base.BaseActivity;
import com.example.vietsyncmobile.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;





public class ForgotPasswordActivity extends BaseActivity {


    private ImageView btnBack;
    private LinearLayout layoutStepRequest;
    private TextInputLayout tilEmail;
    private TextInputEditText edtEmail;
    private Button btnSend;
    private TextView tvBackToLogin;

    private LinearLayout layoutStepReset;
    private TextInputLayout tilNewPassword;
    private TextInputEditText edtNewPassword;
    private TextInputLayout tilConfirmPassword;
    private TextInputEditText edtConfirmPassword;
    private Button btnResetPassword;

    private ForgotPasswordViewModel forgotPasswordViewModel;
    private String recoveryToken;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_forgot_password;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    @Override
    protected void initViews() {

        btnBack = findViewById(R.id.btn_back);
        layoutStepRequest = findViewById(R.id.layout_step_request);
        tilEmail = findViewById(R.id.til_email);
        edtEmail = findViewById(R.id.edt_email);
        btnSend = findViewById(R.id.btn_send);
        tvBackToLogin = findViewById(R.id.tv_back_to_login);

        layoutStepReset = findViewById(R.id.layout_step_reset);
        tilNewPassword = findViewById(R.id.til_new_password);
        edtNewPassword = findViewById(R.id.edt_new_password);
        tilConfirmPassword = findViewById(R.id.til_confirm_password);
        edtConfirmPassword = findViewById(R.id.edt_confirm_password);
        btnResetPassword = findViewById(R.id.btn_reset_password);


        forgotPasswordViewModel = new ViewModelProvider(this, new ForgotPasswordViewModel.Factory(this))
                .get(ForgotPasswordViewModel.class);


        btnBack.setOnClickListener(v -> onBackPressed());
        tvBackToLogin.setOnClickListener(v -> navigateToLogin());
        btnSend.setOnClickListener(v -> attemptSendRecovery());
        btnResetPassword.setOnClickListener(v -> attemptResetPassword());


        setupTextWatchers();
        setupFocusAnimations();
    }

    @Override
    protected void initObservers() {
        observeViewModel(forgotPasswordViewModel);


        forgotPasswordViewModel.getResetPasswordResult().observe(this, resource -> {
            if (resource != null) {
                if (resource.isSuccess()) {
                    showSuccessDialog();
                }
            }
        });


        forgotPasswordViewModel.getUpdatePasswordResult().observe(this, resource -> {
            if (resource != null) {
                if (resource.isSuccess()) {
                    showToast("Mật khẩu đã được cập nhật thành công!");
                    navigateToLogin();
                }
            }
        });
    }




    private void handleIntent(Intent intent) {
        if (intent == null) return;
        android.net.Uri data = intent.getData();
        if (data != null) {
            String scheme = data.getScheme();
            String host = data.getHost();

            if ("vietsync".equals(scheme) && "reset-password".equals(host)) {
                String fragment = data.getFragment();
                Map<String, String> params = parseFragment(fragment);

                String token = params.get("access_token");
                String type = params.get("type");


                if (token == null || token.isEmpty()) {
                    token = data.getQueryParameter("access_token");
                }
                if (type == null || type.isEmpty()) {
                    type = data.getQueryParameter("type");
                }

                if (token != null && !token.isEmpty()) {
                    this.recoveryToken = token;
                    showResetPasswordStep();
                }
            }
        }
    }




    private void showResetPasswordStep() {
        layoutStepRequest.setVisibility(View.GONE);
        layoutStepReset.setVisibility(View.VISIBLE);
    }




    private void attemptSendRecovery() {
        String email = edtEmail.getText().toString().trim();
        if (email.isEmpty()) {
            tilEmail.setError("Email không được để trống");
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Email không đúng định dạng");
            return;
        }
        tilEmail.setError(null);
        forgotPasswordViewModel.resetPassword(email);
    }




    private void attemptResetPassword() {
        String newPassword = edtNewPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();
        boolean isValid = true;

        if (newPassword.isEmpty()) {
            tilNewPassword.setError("Mật khẩu không được để trống");
            isValid = false;
        } else if (newPassword.length() < 6) {
            tilNewPassword.setError("Mật khẩu phải từ 6 ký tự trở lên");
            isValid = false;
        } else {
            tilNewPassword.setError(null);
        }

        if (confirmPassword.isEmpty()) {
            tilConfirmPassword.setError("Vui lòng xác nhận mật khẩu");
            isValid = false;
        } else if (!confirmPassword.equals(newPassword)) {
            tilConfirmPassword.setError("Mật khẩu xác nhận không khớp");
            isValid = false;
        } else {
            tilConfirmPassword.setError(null);
        }

        if (isValid && recoveryToken != null) {
            forgotPasswordViewModel.updatePassword(recoveryToken, newPassword);
        } else if (recoveryToken == null) {
            showToast("Phiên làm việc hết hạn hoặc token không hợp lệ.");
        }
    }




    private void showSuccessDialog() {
        Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_success_email);
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        dialog.setCancelable(true);

        Button btnOk = dialog.findViewById(R.id.btn_dialog_ok);
        btnOk.setOnClickListener(v -> {
            dialog.dismiss();
            navigateToLogin();
        });

        dialog.show();
    }




    private Map<String, String> parseFragment(String fragment) {
        Map<String, String> params = new HashMap<>();
        if (fragment != null && !fragment.isEmpty()) {
            String[] pairs = fragment.split("&");
            for (String pair : pairs) {
                String[] kv = pair.split("=");
                if (kv.length == 2) {
                    try {
                        String key = URLDecoder.decode(kv[0], "UTF-8");
                        String value = URLDecoder.decode(kv[1], "UTF-8");
                        params.put(key, value);
                    } catch (UnsupportedEncodingException e) {
                        params.put(kv[0], kv[1]);
                    }
                }
            }
        }
        return params;
    }




    private void setupTextWatchers() {
        TextWatcher emailWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilEmail.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };
        edtEmail.addTextChangedListener(emailWatcher);

        edtNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilNewPassword.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        edtConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tilConfirmPassword.setError(null);
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
        edtNewPassword.setOnFocusChangeListener(focusChangeListener);
        edtConfirmPassword.setOnFocusChangeListener(focusChangeListener);
    }

    private float dpToPx(float dp) {
        return dp * getResources().getDisplayMetrics().density;
    }

    private void navigateToLogin() {
        Intent intent = new Intent(ForgotPasswordActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
