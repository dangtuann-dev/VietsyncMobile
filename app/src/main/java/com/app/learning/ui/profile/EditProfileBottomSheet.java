package com.app.learning.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.vietsyncmobile.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * EditProfileBottomSheet displays a BottomSheet dialog letting users modify
 * their display full name and bio profile details.
 */
public class EditProfileBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_FULL_NAME = "arg_full_name";
    private static final String ARG_BIO = "arg_bio";

    private TextInputEditText etFullName;
    private TextInputEditText etBio;
    private TextInputLayout layoutFullName;

    private OnProfileSavedListener listener;

    public interface OnProfileSavedListener {
        void onProfileSaved(String fullName, String bio);
    }

    /**
     * Factory method creating a new dialog instance loaded with current profile values.
     */
    public static EditProfileBottomSheet newInstance(String fullName, String bio) {
        EditProfileBottomSheet fragment = new EditProfileBottomSheet();
        Bundle args = new Bundle();
        args.putString(ARG_FULL_NAME, fullName);
        args.putString(ARG_BIO, bio);
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnProfileSavedListener(OnProfileSavedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Bind Views
        etFullName = view.findViewById(R.id.etFullName);
        etBio = view.findViewById(R.id.etBio);
        layoutFullName = view.findViewById(R.id.layoutFullName);
        MaterialButton btnCancel = view.findViewById(R.id.btnCancel);
        MaterialButton btnSave = view.findViewById(R.id.btnSave);

        // Retrieve and populate default arguments
        if (getArguments() != null) {
            String initialName = getArguments().getString(ARG_FULL_NAME, "");
            String initialBio = getArguments().getString(ARG_BIO, "");

            etFullName.setText(initialName);
            etBio.setText(initialBio);
        }

        // Cancel click handler
        btnCancel.setOnClickListener(v -> dismiss());

        // Save click handler
        btnSave.setOnClickListener(v -> {
            String fullName = etFullName.getText() != null ? etFullName.getText().toString().trim() : "";
            String bio = etBio.getText() != null ? etBio.getText().toString().trim() : "";

            if (fullName.isEmpty()) {
                layoutFullName.setError("Họ và tên không được để trống");
                return;
            }

            layoutFullName.setError(null);

            if (listener != null) {
                listener.onProfileSaved(fullName, bio);
            }

            dismiss();
        });
    }

    @Override
    public int getTheme() {
        // Return custom theme style if bottom sheet rounded borders are desired.
        // Material3 handles bottom sheet roundings natively, but this is a fallback.
        return super.getTheme();
    }
}
