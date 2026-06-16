package com.app.learning.ui.auth;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.app.learning.data.api.Resource;
import com.app.learning.data.model.User;
import com.app.learning.data.repository.UserRepository;
import com.app.learning.ui.base.BaseViewModel;

/**
 * LoginViewModel manages state changes for the EdX style login flows,
 * interacting with UserRepository and communicating updates to LoginActivity.
 */
public class LoginViewModel extends BaseViewModel {

    private final UserRepository userRepository;
    private final MediatorLiveData<Resource<User>> loginResult = new MediatorLiveData<>();

    public LoginViewModel(@NonNull UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Exposes the login resource wrapper to the Activity.
     */
    public LiveData<Resource<User>> getLoginResult() {
        return loginResult;
    }

    /**
     * Executes the login operation. Manages internal view model loading/error states
     * alongside wrapping results in a LiveData object.
     *
     * @param email    The email to login with.
     * @param password The password to login with.
     */
    public void login(String email, String password) {
        showLoading();
        clearError();

        LiveData<Resource<User>> repoSource = userRepository.login(email, password);
        loginResult.addSource(repoSource, resource -> {
            if (resource != null) {
                if (resource.isLoading()) {
                    showLoading();
                } else if (resource.isSuccess()) {
                    hideLoading();
                    loginResult.setValue(resource);
                    loginResult.removeSource(repoSource);
                } else if (resource.isError()) {
                    hideLoading();
                    if (resource.error != null) {
                        setError(resource.error.getMessage());
                    } else {
                        setError("Lỗi xác thực không xác định");
                    }
                    loginResult.setValue(resource);
                    loginResult.removeSource(repoSource);
                }
            }
        });
    }

    /**
     * ViewModelProvider.Factory implementation to handle UserRepository injection.
     */
    public static class Factory implements ViewModelProvider.Factory {
        private final Context context;

        public Factory(@NonNull Context context) {
            this.context = context.getApplicationContext();
        }

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(LoginViewModel.class)) {
                return (T) new LoginViewModel(new UserRepository(context));
            }
            throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
        }
    }
}
