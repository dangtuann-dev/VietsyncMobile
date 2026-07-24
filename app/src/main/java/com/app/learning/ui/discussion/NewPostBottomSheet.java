package com.app.learning.ui.discussion;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.vietsyncmobile.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class NewPostBottomSheet extends BottomSheetDialogFragment {

    public interface OnPostCreatedListener {
        void onPostCreated(String title, String body, String tags);
    }

    private OnPostCreatedListener listener;

    public NewPostBottomSheet(OnPostCreatedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_new_post, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextInputEditText etTitle = view.findViewById(R.id.etPostTitle);
        TextInputEditText etBody = view.findViewById(R.id.etPostBody);
        TextInputEditText etTags = view.findViewById(R.id.etPostTags);
        MaterialButton btnSubmit = view.findViewById(R.id.btnSubmitPost);

        btnSubmit.setOnClickListener(v -> {
            String title = etTitle.getText() != null ? etTitle.getText().toString().trim() : "";
            String body = etBody.getText() != null ? etBody.getText().toString().trim() : "";
            String tags = etTags.getText() != null ? etTags.getText().toString().trim() : "";

            if (title.isEmpty() || body.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng nhập đầy đủ tiêu đề và nội dung!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (listener != null) listener.onPostCreated(title, body, tags);
            dismiss();
        });
    }
}
