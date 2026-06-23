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

/**
 * BaseActivity is the base class for all Activities in the application.
 * It provides centralized mechanisms for:
 * 1. Loading dialogue management (showing/hiding progress indicators)
 * 2. Error representation (via Toasts and Snackbars)
 * 3. Consistent Lifecycle hooks (initViews, initObservers)
 * 4. Automatic binding to {@link BaseViewModel} loading and error live states
 */
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
        // Enforce role-based access control if annotation is present
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

    /**
     * Define the layout resource ID for the activity.
     * Return 0 if you are using ViewBinding directly in your child class onCreate.
     *
     * @return layout resource ID (e.g., R.layout.activity_main)
     */
    @LayoutRes
    protected abstract int getLayoutId();

    /**
     * Initialize UI widgets, adapters, listeners. Called in onCreate.
     */
    protected abstract void initViews();

    /**
     * Set up LiveData observations. Called in onCreate.
     */
    protected abstract void initObservers();

    /**
     * Initializes the loading dialog layout programmatically to avoid xml dependency.
     */
    private void setupLoadingDialog() {
        loadingDialog = new Dialog(this);
        loadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Parent layout setup
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER_VERTICAL);
        int padding = convertDpToPx(24);
        layout.setPadding(padding, padding, padding, padding);
        layout.setBackgroundColor(Color.WHITE);

        // Circular progress bar setup
        ProgressBar progressBar = new ProgressBar(this);
        progressBar.setIndeterminate(true);
        LinearLayout.LayoutParams progressParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        progressParams.rightMargin = convertDpToPx(16);
        progressBar.setLayoutParams(progressParams);

        // Text description setup
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

    /**
     * Show loading dialog with default message.
     */
    public void showLoading() {
        showLoading("Đang tải...");
    }

    /**
     * Show loading dialog with a custom message.
     *
     * @param message Text to display
     */
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

    /**
     * Hide loading dialog.
     */
    public void hideLoading() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    /**
     * Show an error message via a bottom Snackbar.
     *
     * @param message Error content
     */
    public void showError(String message) {
        if (message != null && !message.trim().isEmpty()) {
            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
                    .setBackgroundTint(Color.parseColor("#D32F2F")) // Red error indicator
                    .setTextColor(Color.WHITE)
                    .show();
        }
    }

    /**
     * Show a feedback toast.
     *
     * @param message Toast message
     */
    public void showToast(String message) {
        if (message != null && !message.trim().isEmpty()) {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Automatically link the activity's loading and error display states
     * to the lifecycle states of the provided ViewModel.
     *
     * @param viewModel The ViewModel to bind to
     */
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

    /**
     * Utility method to scale dp sizes dynamically.
     */
    private int convertDpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    @Override
    protected void onDestroy() {
        // Prevent dialog leak on configuration change / exit
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
        super.onDestroy();
    }
}
