package com.app.learning.ui.base;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

import android.content.Context;
import android.content.res.Configuration;
import java.util.Locale;









public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void attachBaseContext(Context newBase) {
        String lang = com.app.learning.utils.UserPreference.getInstance(newBase).getAppLanguage();
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);

        Configuration config = new Configuration(newBase.getResources().getConfiguration());
        config.setLocale(locale);

        Context context = newBase.createConfigurationContext(config);
        super.attachBaseContext(context);
    }

    private Dialog loadingDialog;
    private TextView loadingTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        if (getClass().isAnnotationPresent(com.app.learning.utils.RequireRole.class)) {
            com.app.learning.utils.RequireRole annotation = getClass().getAnnotation(com.app.learning.utils.RequireRole.class);
            if (annotation != null) {
                com.app.learning.utils.RoleManager.Role requiredRole = annotation.value();
                com.app.learning.utils.RoleManager.Role currentRole = com.app.learning.utils.RoleManager.getInstance(this).getCurrentRole();
                if (currentRole != requiredRole && currentRole != com.app.learning.utils.RoleManager.Role.ADMIN) {
                    android.widget.Toast.makeText(this, "Bạn không có quyền truy cập màn hình này!", android.widget.Toast.LENGTH_SHORT).show();
                    finish();
                    super.onCreate(savedInstanceState);
                    return;
                }
            }
        }

        super.onCreate(savedInstanceState);

        int layoutId = getLayoutId();
        if (layoutId != 0) {
            setContentView(layoutId);
        }

        setupLoadingDialog();
        initViews();
        initObservers();
    }







    @LayoutRes
    protected abstract int getLayoutId();




    protected abstract void initViews();




    protected abstract void initObservers();




    private void setupLoadingDialog() {
        loadingDialog = new Dialog(this);
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);


        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER_VERTICAL);
        int padding = convertDpToPx(24);
        layout.setPadding(padding, padding, padding, padding);
        layout.setBackgroundColor(Color.WHITE);


        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setIndeterminate(true);
        LinearLayout.LayoutParams progressParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        progressParams.rightMargin = convertDpToPx(16);
        progressBar.setLayoutParams(progressParams);


        loadingTextView = new TextView(this);
        loadingTextView.setText("Đang tải...");
        loadingTextView.setTextColor(Color.BLACK);
        loadingTextView.setTextSize(16);

        layout.addView(progressBar);
        layout.addView(loadingTextView);

        loadingDialog.setContentView(layout);
        if (loadingDialog.getWindow() != null) {
            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        loadingDialog.setCancelable(false);
    }




    public void showLoading() {
        showLoading("Đang tải...");
    }






    public void showLoading(String message) {
        if (loadingDialog != null) {
            if (loadingTextView != null && message != null) {
                loadingTextView.setText(message);
            }
            if (!loadingDialog.isShowing() && !isFinishing()) {
                loadingDialog.show();
            }
        }
    }




    public void hideLoading() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }






    public void showError(String message) {
        if (message != null && !message.trim().isEmpty()) {
            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
                    .setBackgroundTint(Color.parseColor("#D32F2F"))
                    .setTextColor(Color.WHITE)
                    .show();
        }
    }






    public void showToast(String message) {
        if (message != null && !message.trim().isEmpty()) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }







    protected void observeViewModel(BaseViewModel viewModel) {
        if (viewModel != null) {
            viewModel.getIsLoading().observe(this, isLoading -> {
                if (isLoading != null) {
                    if (isLoading) {
                        showLoading();
                    } else {
                        hideLoading();
                    }
                }
            });

            viewModel.getErrorMessage().observe(this, error -> {
                if (error != null) {
                    showError(error);
                }
            });
        }
    }




    private int convertDpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    @Override
    protected void onDestroy() {

        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
        super.onDestroy();
    }
}
