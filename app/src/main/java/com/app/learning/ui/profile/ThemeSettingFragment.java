package com.app.learning.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.vietsyncmobile.R;
import com.app.learning.utils.ThemeManager;

public class ThemeSettingFragment extends Fragment {

    private RadioGroup rgThemeOptions;
    private RadioButton rbLight, rbDark, rbSystem;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_theme_setting, container, false);

        rgThemeOptions = view.findViewById(R.id.rgThemeOptions);
        rbLight = view.findViewById(R.id.rbLight);
        rbDark = view.findViewById(R.id.rbDark);
        rbSystem = view.findViewById(R.id.rbSystem);

        int currentMode = ThemeManager.getInstance(requireContext()).getThemeMode();
        if (currentMode == ThemeManager.THEME_LIGHT) {
            rbLight.setChecked(true);
        } else if (currentMode == ThemeManager.THEME_DARK) {
            rbDark.setChecked(true);
        } else {
            rbSystem.setChecked(true);
        }

        rgThemeOptions.setOnCheckedChangeListener((group, checkedId) -> {
            int selectedMode;
            if (checkedId == R.id.rbLight) {
                selectedMode = ThemeManager.THEME_LIGHT;
            } else if (checkedId == R.id.rbDark) {
                selectedMode = ThemeManager.THEME_DARK;
            } else {
                selectedMode = ThemeManager.THEME_SYSTEM;
            }
            ThemeManager.getInstance(requireContext()).setThemeMode(selectedMode);
        });

        return view;
    }
}
