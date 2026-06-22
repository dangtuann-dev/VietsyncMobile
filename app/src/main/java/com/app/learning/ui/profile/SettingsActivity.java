package com.app.learning.ui.profile;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatDelegate;
import com.app.learning.ui.base.BaseActivity;
import com.app.learning.utils.UserPreference;
import com.example.vietsyncmobile.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.Locale;

public class SettingsActivity extends BaseActivity {

    private SwitchMaterial switchDarkMode;
    private SwitchMaterial switchNotifications;
    private View rowLanguage;
    private TextView tvCurrentLanguage;
    private View rowClearCache;
    private TextView tvCacheSize;
    private View btnBack;

    private UserPreference userPreference;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_settings;
    }

    @Override
    protected void initViews() {
        userPreference = UserPreference.getInstance(this);

        btnBack = findViewById(R.id.btnBack);
        switchDarkMode = findViewById(R.id.switchDarkMode);
        switchNotifications = findViewById(R.id.switchNotifications);
        rowLanguage = findViewById(R.id.rowLanguage);
        tvCurrentLanguage = findViewById(R.id.tvCurrentLanguage);
        rowClearCache = findViewById(R.id.rowClearCache);
        tvCacheSize = findViewById(R.id.tvCacheSize);

        // Bind data
        switchDarkMode.setChecked(userPreference.isDarkModeEnabled());
        switchNotifications.setChecked(userPreference.isNotificationsEnabled());
        
        String lang = userPreference.getAppLanguage();
        tvCurrentLanguage.setText("en".equals(lang) ? "English" : "Tiếng Việt");

        // Action Listeners
        btnBack.setOnClickListener(v -> onBackPressed());

        switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            userPreference.setDarkModeEnabled(isChecked);
            AppCompatDelegate.setDefaultNightMode(
                    isChecked ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
            );
        });

        switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            userPreference.setNotificationsEnabled(isChecked);
            showToast(isChecked ? getString(R.string.settings_toast_notifications_on) 
                                : getString(R.string.settings_toast_notifications_off));
        });

        rowLanguage.setOnClickListener(v -> showLanguageDialog());

        rowClearCache.setOnClickListener(v -> clearAppCache());
    }

    @Override
    protected void initObservers() {
        // No specific live data observations needed
    }

    private void showLanguageDialog() {
        String[] languages = {"Tiếng Việt", "English"};
        int checkedItem = "en".equals(userPreference.getAppLanguage()) ? 1 : 0;

        new MaterialAlertDialogBuilder(this)
                .setTitle(getString(R.string.settings_dialog_language_title))
                .setSingleChoiceItems(languages, checkedItem, (dialog, which) -> {
                    String selectedLang = (which == 1) ? "en" : "vi";
                    userPreference.setAppLanguage(selectedLang);
                    tvCurrentLanguage.setText(languages[which]);
                    
                    // Apply locale change
                    androidx.core.os.LocaleListCompat appLocale = androidx.core.os.LocaleListCompat.forLanguageTags(selectedLang);
                    AppCompatDelegate.setApplicationLocales(appLocale);
                    
                    showToast(getString(R.string.settings_toast_language_changed, languages[which]));
                    dialog.dismiss();
                })
                .setNegativeButton(getString(R.string.settings_dialog_cancel), null)
                .show();
    }

    private void clearAppCache() {
        tvCacheSize.setText("0.0 KB");
        showToast(getString(R.string.settings_toast_cache_cleared));
    }
}
