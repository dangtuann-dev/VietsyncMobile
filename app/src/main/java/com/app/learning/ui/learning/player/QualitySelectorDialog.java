package com.app.learning.ui.learning.player;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.vietsyncmobile.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class QualitySelectorDialog extends BottomSheetDialogFragment {

    public interface OnQualitySelectedListener {
        void onQualitySelected(String quality);
    }

    private OnQualitySelectedListener listener;

    public QualitySelectorDialog(OnQualitySelectedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_quality_selector, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RadioGroup rgQuality = view.findViewById(R.id.rgQuality);
        rgQuality.setOnCheckedChangeListener((group, checkedId) -> {
            String quality = "Auto";
            if (checkedId == R.id.rb1080p) quality = "1080p";
            else if (checkedId == R.id.rb720p) quality = "720p";
            else if (checkedId == R.id.rb480p) quality = "480p";
            else if (checkedId == R.id.rb360p) quality = "360p";

            if (listener != null) listener.onQualitySelected(quality);
            dismiss();
        });
    }
}
