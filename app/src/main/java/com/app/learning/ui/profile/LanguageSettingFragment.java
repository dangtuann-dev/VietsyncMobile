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
import com.app.learning.utils.LocaleManager;
import com.app.learning.utils.UserPreference;

public class LanguageSettingFragment extends Fragment {

    private RadioGroup rgLanguageOptions;
    private RadioButton rbVietnamese, rbEnglish;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_language_setting, container, false);

        rgLanguageOptions = view.findViewById(R.id.rgLanguageOptions);
        rbVietnamese = view.findViewById(R.id.rbVietnamese);
        rbEnglish = view.findViewById(R.id.rbEnglish);

        String currentLang = UserPreference.getInstance(requireContext()).getAppLanguage();
        if (LocaleManager.LANGUAGE_EN.equalsIgnoreCase(currentLang)) {
            rbEnglish.setChecked(true);
        } else {
            rbVietnamese.setChecked(true);
        }

        rgLanguageOptions.setOnCheckedChangeListener((group, checkedId) -> {
            String selectedLang;
            if (checkedId == R.id.rbEnglish) {
                selectedLang = LocaleManager.LANGUAGE_EN;
            } else {
                selectedLang = LocaleManager.LANGUAGE_VI;
            }
            LocaleManager.setNewLocale(requireContext(), selectedLang);
            if (getActivity() != null) {
                getActivity().recreate();
            }
        });

        return view;
    }
}
