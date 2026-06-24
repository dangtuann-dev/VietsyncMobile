package com.app.learning.ui.auth;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.app.learning.data.api.Resource;
import com.app.learning.data.model.UserModel;
import com.app.learning.data.repository.AuthRepository;
import com.app.learning.ui.base.BaseViewModel;




public class ForgotPasswordViewModel extends BaseViewModel {

    private final AuthRepository authRepository;
    private final MediatorLiveData<Resource<Void>> resetPasswordResult = new MediatorLiveData<>();
    private final MediatorLiveData<Resource<UserModel>> updatePasswordResult = new MediatorLiveData<>();

    public ForgotPasswordViewModel(@NonNull AuthRepository authRepository) {
        this.authRepository = authRepository;
    }




    public LiveData<Resource<Void>> getResetPasswordResult() {
        return resetPasswordResult;
    }




    public LiveData<Resource<UserModel>> getUpdatePasswordResult() {
        return updatePasswordResult;
    }




    public void resetPassword(String email) {
        showLoading();
        clearError();

        LiveData<Resource<Void>> repoSource = authRepository.resetPassword(email);
        resetPasswordResult.addSource(repoSource, resource -> {
            if (resource != null) {
                if (resource.isLoading()) {
                    showLoading();
                } else if (resource.isSuccess()) {
                    hideLoading();
                    resetPasswordResult.setValue(resource);
                    resetPasswordResult.removeSource(repoSource);
                } else if (resource.isError()) {
                    hideLoading();
                    if (resource.error != null) {
                        setError(resource.error.getMessage());
                    } else {
                        setError("Lỗi gửi email khôi phục mật khẩu");
                    }
                    resetPasswordResult.setValue(resource);
                    resetPasswordResult.removeSource(repoSource);
                }
            }
        });
    }




    public void updatePassword(String token, String newPassword) {
        showLoading();
        clearError();

        LiveData<Resource<UserModel>> repoSource = authRepository.updatePassword(token, newPassword);
        updatePasswordResult.addSource(repoSource, resource -> {
            if (resource != null) {
                if (resource.isLoading()) {
                    showLoading();
                } else if (resource.isSuccess()) {
                    hideLoading();
                    updatePasswordResult.setValue(resource);
                    updatePasswordResult.removeSource(repoSource);
                } else if (resource.isError()) {
                    hideLoading();
                    if (resource.error != null) {
                        setError(resource.error.getMessage());
                    } else {
                        setError("Lỗi cập nhật mật khẩu mới");
                    }
                    updatePasswordResult.setValue(resource);
                    updatePasswordResult.removeSource(repoSource);
                }
            }
        });
    }




    public static class Factory implements ViewModelProvider.Factory {
        private final Context context;

        public Factory(@NonNull Context context) {
            this.context = context.getApplicationContext();
        }

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(ForgotPasswordViewModel.class)) {
                return (T) new ForgotPasswordViewModel(new AuthRepository(context));
            }
            throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
        }
    }
}
