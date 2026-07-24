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

public class SpeedSelectorDialog extends BottomSheetDialogFragment {

    public interface OnSpeedSelectedListener {
        void onSpeedSelected(float speed);
    }

    private OnSpeedSelectedListener listener;

    public SpeedSelectorDialog(OnSpeedSelectedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_speed_selector, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RadioGroup rgSpeed = view.findViewById(R.id.rgSpeed);
        rgSpeed.setOnCheckedChangeListener((group, checkedId) -> {
            float speed = 1.0f;
            if (checkedId == R.id.rbSpeed05) speed = 0.5f;
            else if (checkedId == R.id.rbSpeed075) speed = 0.75f;
            else if (checkedId == R.id.rbSpeed10) speed = 1.0f;
            else if (checkedId == R.id.rbSpeed125) speed = 1.25f;
            else if (checkedId == R.id.rbSpeed15) speed = 1.5f;
            else if (checkedId == R.id.rbSpeed20) speed = 2.0f;

            if (listener != null) listener.onSpeedSelected(speed);
            dismiss();
        });
    }
}
