package com.app.learning.ui.auth;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.app.learning.data.api.Resource;
import com.app.learning.data.model.User;
import com.app.learning.data.repository.UserRepository;
import com.app.learning.ui.base.BaseViewModel;
import com.app.learning.utils.ValidationUtils;

public class AuthViewModel extends BaseViewModel {

    private final UserRepository userRepository;
    private final MutableLiveData<Resource<User>> authResult = new MutableLiveData<>();

    public AuthViewModel(@NonNull UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LiveData<Resource<User>> getAuthResult() {
        return authResult;
    }

    public void login(String email, String password) {
        if (!ValidationUtils.isValidEmail(email)) {
            authResult.setValue(Resource.error(new com.app.learning.data.api.ApiError("Email không hợp lệ")));
            return;
        }
        if (!ValidationUtils.isValidPassword(password)) {
            authResult.setValue(Resource.error(new com.app.learning.data.api.ApiError("Mật khẩu phải từ 6 ký tự trở lên")));
            return;
        }

        showLoading();
        LiveData<Resource<User>> source = userRepository.login(email, password);
        source.observeForever(resource -> {
            hideLoading();
            if (resource != null) {
                authResult.setValue(resource);
            }
        });
    }
}
