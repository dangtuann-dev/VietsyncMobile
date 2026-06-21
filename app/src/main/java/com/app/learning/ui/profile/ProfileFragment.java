package com.app.learning.ui.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.vietsyncmobile.R;
import com.app.learning.data.api.Resource;
import com.app.learning.data.model.User;
import com.app.learning.data.model.UserStats;
import com.app.learning.ui.auth.LoginActivity;
import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * ProfileFragment renders the profile screen for the student, managing Glide images,
 * stats row updates, edit dialog integrations, storage uploads, and session logs.
 */
public class ProfileFragment extends Fragment {

    private ProfileViewModel viewModel;
    
    private CircleImageView imgAvatar;
    private TextView txtUserName;
    private TextView txtUserEmail;
    private TextView txtUserBio;
    
    private TextView txtEnrolledCount;
    private TextView txtCompletedCount;
    private TextView txtCertificatesCount;

    private AlertDialog progressDialog;

    // Image Picker Launcher
    private final ActivityResultLauncher<String> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), this::onImagePicked);

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Bind Views
        imgAvatar = view.findViewById(R.id.imgAvatar);
        View imgCameraOverlay = view.findViewById(R.id.imgCameraOverlay);
        txtUserName = view.findViewById(R.id.txtUserName);
        txtUserEmail = view.findViewById(R.id.txtUserEmail);
        txtUserBio = view.findViewById(R.id.txtUserBio);
        
        txtEnrolledCount = view.findViewById(R.id.txtEnrolledCount);
        txtCompletedCount = view.findViewById(R.id.txtCompletedCount);
        txtCertificatesCount = view.findViewById(R.id.txtCertificatesCount);

        View rowEditProfile = view.findViewById(R.id.rowEditProfile);
        View rowCertificates = view.findViewById(R.id.rowCertificates);
        View rowDownloads = view.findViewById(R.id.rowDownloads);
        View rowSettings = view.findViewById(R.id.rowSettings);
        View rowLogout = view.findViewById(R.id.rowLogout);
        MaterialButton btnChangePassword = view.findViewById(R.id.btnChangePassword);

        // Init ViewModel
        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        // Load local data instantly
        User localUser = viewModel.getLocalProfile();
        if (localUser != null) {
            bindUserProfile(localUser);
            // Trigger remote reload
            viewModel.loadUserProfile(localUser.getId());
        } else {
            navigateToLogin();
            return;
        }

        // Set Click Listeners
        View.OnClickListener pickImageListener = v -> pickImageLauncher.launch("image/*");
        imgAvatar.setOnClickListener(pickImageListener);
        imgCameraOverlay.setOnClickListener(pickImageListener);

        rowEditProfile.setOnClickListener(v -> openEditProfileBottomSheet());
        btnChangePassword.setOnClickListener(v -> openChangePasswordDialog());

        rowCertificates.setOnClickListener(v -> showToast("Tính năng Chứng chỉ của tôi đang được phát triển"));
        rowDownloads.setOnClickListener(v -> showToast("Tính năng Lịch sử tải xuống đang được phát triển"));
        rowSettings.setOnClickListener(v -> showToast("Tính năng Cài đặt đang được phát triển"));
        
        rowLogout.setOnClickListener(v -> showLogoutConfirmation());

        // Observe Data States
        observeViewModel();
    }

    private void observeViewModel() {
        // Observe Remote Profile updates
        viewModel.getProfileData().observe(getViewLifecycleOwner(), resource -> {
            if (resource.isSuccess() && resource.data != null) {
                bindUserProfile(resource.data);
            }
        });

        // Observe Stats updates
        viewModel.getStatsData().observe(getViewLifecycleOwner(), resource -> {
            if (resource.isSuccess() && resource.data != null) {
                bindUserStats(resource.data);
            }
        });
    }

    private void bindUserProfile(User user) {
        txtUserName.setText(user.getFullName() != null ? user.getFullName() : "Học viên");
        txtUserEmail.setText(user.getEmail());
        
        if (user.getBio() != null && !user.getBio().trim().isEmpty()) {
            txtUserBio.setText(user.getBio());
        } else {
            txtUserBio.setText("Chưa có giới thiệu bản thân.");
        }

        // Load circular avatar with Glide
        Glide.with(this)
                .load(user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty() ? user.getAvatarUrl() : R.drawable.ic_profile_placeholder)
                .placeholder(R.drawable.ic_profile_placeholder)
                .error(R.drawable.ic_profile_placeholder)
                .circleCrop()
                .into(imgAvatar);
    }

    private void bindUserStats(UserStats stats) {
        txtEnrolledCount.setText(String.valueOf(stats.getEnrolledCount()));
        txtCompletedCount.setText(String.valueOf(stats.getCompletedCount()));
        txtCertificatesCount.setText(String.valueOf(stats.getCertificatesCount()));
    }

    /**
     * Launch BottomSheet view to edit profile details.
     */
    private void openEditProfileBottomSheet() {
        User user = viewModel.getLocalProfile();
        if (user == null) return;

        EditProfileBottomSheet bottomSheet = EditProfileBottomSheet.newInstance(user.getFullName(), user.getBio());
        bottomSheet.setOnProfileSavedListener((fullName, bio) -> {
            showLoading(true);
            viewModel.updateProfile(user.getId(), fullName, bio, null).observe(getViewLifecycleOwner(), resource -> {
                if (!resource.isLoading()) {
                    showLoading(false);
                    if (resource.isSuccess() && resource.data != null) {
                        bindUserProfile(resource.data);
                        showToast("Cập nhật hồ sơ thành công");
                    } else if (resource.isError()) {
                        showToast(resource.error != null ? resource.error.getMessage() : "Lỗi cập nhật hồ sơ");
                    }
                }
            });
        });
        bottomSheet.show(getChildFragmentManager(), "EditProfileBottomSheet");
    }

    /**
     * Process chosen image Uri, converts it to bytes and uploads it to Supabase Storage.
     */
    private void onImagePicked(@Nullable Uri uri) {
        if (uri == null) return;
        User user = viewModel.getLocalProfile();
        if (user == null) return;

        try {
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            if (inputStream == null) return;
            
            byte[] imageBytes = readAllBytes(inputStream);
            String mimeType = requireContext().getContentResolver().getType(uri);
            if (mimeType == null) mimeType = "image/jpeg";

            showLoading(true);
            // 1. Upload to Supabase storage
            viewModel.uploadAvatar(user.getId(), imageBytes, mimeType).observe(getViewLifecycleOwner(), uploadRes -> {
                if (!uploadRes.isLoading()) {
                    if (uploadRes.isSuccess() && uploadRes.data != null) {
                        String publicUrl = uploadRes.data;
                        // 2. Save new avatar URL to user profile row
                        viewModel.updateProfile(user.getId(), null, null, publicUrl).observe(getViewLifecycleOwner(), profileRes -> {
                            if (!profileRes.isLoading()) {
                                showLoading(false);
                                if (profileRes.isSuccess() && profileRes.data != null) {
                                    bindUserProfile(profileRes.data);
                                    showToast("Cập nhật ảnh đại diện thành công");
                                } else if (profileRes.isError()) {
                                    showToast("Lỗi đồng bộ ảnh đại diện");
                                }
                            }
                        });
                    } else if (uploadRes.isError()) {
                        showLoading(false);
                        showToast(uploadRes.error != null ? uploadRes.error.getMessage() : "Lỗi tải ảnh đại diện lên máy chủ");
                    }
                }
            });

        } catch (IOException e) {
            showToast("Lỗi đọc tệp tin hình ảnh: " + e.getLocalizedMessage());
        }
    }

    /**
     * Popup dialog for change password details.
     */
    private void openChangePasswordDialog() {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_change_password, null);
        TextInputEditText etNewPassword = dialogView.findViewById(R.id.etNewPassword);
        TextInputEditText etConfirmPassword = dialogView.findViewById(R.id.etConfirmPassword);
        TextInputLayout layoutNewPassword = dialogView.findViewById(R.id.layoutNewPassword);
        TextInputLayout layoutConfirmPassword = dialogView.findViewById(R.id.layoutConfirmPassword);

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .setCancelable(false)
                .create();

        dialogView.findViewById(R.id.btnCancelPassword).setOnClickListener(v -> dialog.dismiss());
        dialogView.findViewById(R.id.btnConfirmPassword).setOnClickListener(v -> {
            String newPass = etNewPassword.getText() != null ? etNewPassword.getText().toString().trim() : "";
            String confirmPass = etConfirmPassword.getText() != null ? etConfirmPassword.getText().toString().trim() : "";

            boolean valid = true;
            if (newPass.isEmpty()) {
                layoutNewPassword.setError("Mật khẩu không được để trống");
                valid = false;
            } else if (newPass.length() < 6) {
                layoutNewPassword.setError("Mật khẩu phải chứa ít nhất 6 ký tự");
                valid = false;
            } else {
                layoutNewPassword.setError(null);
            }

            if (confirmPass.isEmpty()) {
                layoutConfirmPassword.setError("Vui lòng xác nhận mật khẩu");
                valid = false;
            } else if (!confirmPass.equals(newPass)) {
                layoutConfirmPassword.setError("Mật khẩu xác nhận không trùng khớp");
                valid = false;
            } else {
                layoutConfirmPassword.setError(null);
            }

            if (!valid) return;

            showLoading(true);
            viewModel.changePassword(newPass).observe(getViewLifecycleOwner(), resource -> {
                if (!resource.isLoading()) {
                    showLoading(false);
                    if (resource.isSuccess()) {
                        showToast("Đổi mật khẩu thành công!");
                        dialog.dismiss();
                    } else if (resource.isError()) {
                        showToast(resource.error != null ? resource.error.getMessage() : "Lỗi đổi mật khẩu");
                    }
                }
            });
        });

        dialog.show();
    }

    /**
     * Sign out warning dialogue popup.
     */
    private void showLogoutConfirmation() {
        new MaterialAlertDialogBuilder(requireContext())
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc chắn muốn đăng xuất khỏi tài khoản?")
                .setNegativeButton("Hủy", null)
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    viewModel.logout();
                    navigateToLogin();
                })
                .show();
    }

    private void showLoading(boolean show) {
        if (show) {
            if (progressDialog == null) {
                ProgressBar progressBar = new ProgressBar(requireContext());
                progressBar.setPadding(60, 60, 60, 60);
                progressDialog = new AlertDialog.Builder(requireContext())
                        .setView(progressBar)
                        .setCancelable(false)
                        .create();
            }
            progressDialog.show();
        } else {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

    private void showToast(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(requireContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }

    private byte[] readAllBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }
}
