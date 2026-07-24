package com.app.learning.ui.note;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.vietsyncmobile.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Locale;

public class AddNoteDialog extends BottomSheetDialogFragment {

    public interface OnNoteSaveListener {
        void onSave(String noteText, String color);
    }

    private final long timestampSeconds;
    private final OnNoteSaveListener listener;
    private String selectedColor = "#FFEB3B"; // default yellow

    public AddNoteDialog(long timestampSeconds, OnNoteSaveListener listener) {
        this.timestampSeconds = timestampSeconds;
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_add_note, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView tvTimestamp = view.findViewById(R.id.tvCurrentTimestamp);
        TextInputEditText etNoteText = view.findViewById(R.id.etNoteText);
        MaterialButton btnSave = view.findViewById(R.id.btnSaveNote);

        View colorYellow = view.findViewById(R.id.colorYellow);
        View colorGreen = view.findViewById(R.id.colorGreen);
        View colorBlue = view.findViewById(R.id.colorBlue);
        View colorPink = view.findViewById(R.id.colorPink);

        long min = timestampSeconds / 60;
        long sec = timestampSeconds % 60;
        tvTimestamp.setText(String.format(Locale.getDefault(), "%02d:%02d", min, sec));

        colorYellow.setOnClickListener(v -> selectedColor = "#FFEB3B");
        colorGreen.setOnClickListener(v -> selectedColor = "#81C784");
        colorBlue.setOnClickListener(v -> selectedColor = "#64B5F6");
        colorPink.setOnClickListener(v -> selectedColor = "#F48FB1");

        btnSave.setOnClickListener(v -> {
            String text = etNoteText.getText() != null ? etNoteText.getText().toString().trim() : "";
            if (text.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập nội dung ghi chú!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (listener != null) listener.onSave(text, selectedColor);
            dismiss();
        });
    }
}
