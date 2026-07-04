package com.app.learning.ui.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.app.learning.data.local.AppSettingsManager;
import com.example.vietsyncmobile.R;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SettingsFragment extends PreferenceFragmentCompat {

    private AppSettingsManager appSettingsManager;
    private final CompositeDisposable disposables = new CompositeDisposable();

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
        appSettingsManager = AppSettingsManager.getInstance(requireContext());

        setupLanguagePreference();
        setupDarkModePreference();
        setupFontSizePreference();
        setupClearCachePreference();
        setupLinkPreferences();
    }

    private void setupLanguagePreference() {
        ListPreference languagePref = findPreference("app_language");
        if (languagePref != null) {
            languagePref.setOnPreferenceChangeListener((preference, newValue) -> {
                String language = (String) newValue;
                com.app.learning.utils.UserPreference.getInstance(requireContext()).setAppLanguage(language);
                disposables.add(appSettingsManager.setAppLanguage(language)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(prefs -> {
                            androidx.core.os.LocaleListCompat appLocale = androidx.core.os.LocaleListCompat.forLanguageTags(language);
                            AppCompatDelegate.setApplicationLocales(appLocale);
                            Toast.makeText(requireContext(), getString(R.string.settings_toast_language_changed, language), Toast.LENGTH_SHORT).show();
                        }, throwable -> {
                            Toast.makeText(requireContext(), "Error saving language", Toast.LENGTH_SHORT).show();
                        }));
                return true;
            });
        }
    }

    private void setupDarkModePreference() {
        SwitchPreferenceCompat darkModePref = findPreference("dark_mode");
        if (darkModePref != null) {
            darkModePref.setOnPreferenceChangeListener((preference, newValue) -> {
                boolean isDarkMode = (Boolean) newValue;
                com.app.learning.utils.UserPreference.getInstance(requireContext()).setDarkModeEnabled(isDarkMode);
                disposables.add(appSettingsManager.setDarkModeEnabled(isDarkMode)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(prefs -> {
                            AppCompatDelegate.setDefaultNightMode(
                                    isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
                            );
                        }, throwable -> {
                            Toast.makeText(requireContext(), "Error saving dark mode", Toast.LENGTH_SHORT).show();
                        }));
                return true;
            });
        }
    }

    private void setupFontSizePreference() {
        ListPreference fontSizePref = findPreference("font_size");
        if (fontSizePref != null) {
            fontSizePref.setOnPreferenceChangeListener((preference, newValue) -> {
                String fontSize = (String) newValue;
                disposables.add(appSettingsManager.setFontSize(fontSize)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(prefs -> {
                            // In a real app you'd need to apply this text scale globally
                            Toast.makeText(requireContext(), "Font size updated", Toast.LENGTH_SHORT).show();
                        }, throwable -> {
                            Toast.makeText(requireContext(), "Error saving font size", Toast.LENGTH_SHORT).show();
                        }));
                return true;
            });
        }
    }

    private void setupClearCachePreference() {
        Preference clearCachePref = findPreference("clear_cache");
        if (clearCachePref != null) {
            clearCachePref.setOnPreferenceClickListener(preference -> {
                preference.setSummary("0.0 KB");
                Toast.makeText(requireContext(), getString(R.string.settings_toast_cache_cleared), Toast.LENGTH_SHORT).show();
                return true;
            });
        }
    }

    private void setupLinkPreferences() {
        Preference privacyPref = findPreference("privacy_policy");
        if (privacyPref != null) {
            privacyPref.setOnPreferenceClickListener(preference -> {
                openUrl("https://example.com/privacy");
                return true;
            });
        }

        Preference termsPref = findPreference("terms_of_service");
        if (termsPref != null) {
            termsPref.setOnPreferenceClickListener(preference -> {
                openUrl("https://example.com/terms");
                return true;
            });
        }
    }

    private void openUrl(String url) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(browserIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        disposables.clear();
    }
}
