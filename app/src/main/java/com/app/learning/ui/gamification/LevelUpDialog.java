package com.app.learning.ui.gamification;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.vietsyncmobile.R;
import com.google.android.material.button.MaterialButton;

public class LevelUpDialog extends DialogFragment {

    private final int newLevel;
    private final String levelTitle;

    public LevelUpDialog(int newLevel, String levelTitle) {
        this.newLevel = newLevel;
        this.levelTitle = levelTitle;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_level_up, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvNewLevelTitle = view.findViewById(R.id.tvNewLevelTitle);
        MaterialButton btnClose = view.findViewById(R.id.btnCloseCelebration);

        tvNewLevelTitle.setText("Đã thăng cấp lên Level " + newLevel + ": " + levelTitle);
        btnClose.setOnClickListener(v -> dismiss());
    }
}
