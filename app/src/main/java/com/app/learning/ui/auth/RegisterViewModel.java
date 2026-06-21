package com.app.learning.ui.auth;

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
 * RegisterViewModel handles the registration flow. It forwards the data to {@link UserRepository}
 * which communicates with Supabase Auth. The repository returns a LiveData<Resource<User>> that
 * we expose via {@code getRegisterResult()} for the UI to observe.
 */
public class RegisterViewModel extends BaseViewModel {

    private final UserRepository userRepository;
    private final MediatorLiveData<Resource<User>> registerResult = new MediatorLiveData<>();

    public RegisterViewModel(@NonNull UserRepository repository) {
        this.userRepository = repository;
    }

    public LiveData<Resource<User>> getRegisterResult() {
        return registerResult;
    }

    /**
     * Initiates registration. The repository will also store the session and the ViewModel will
     * automatically expose loading/error states via {@link BaseViewModel}.
     */
    public void register(String fullName, String email, String password, String role) {
        showLoading();
        clearError();
        LiveData<Resource<User>> source = userRepository.register(email, password, fullName, role);
        // The repository currently registers with role default "student"; we set role locally if needed.
        // Role handling could be extended in the backend; for UI we simply pass the role onward.
        registerResult.addSource(source, resource -> {
            if (resource != null) {
                if (resource.isLoading()) {
                    showLoading();
                } else if (resource.isSuccess()) {
                    hideLoading();
                    // Optionally update role on the User object if backend supports it
                    User user = resource.data;
                    if (user != null) {
                        user.setRole(role);
                    }
                    registerResult.setValue(resource);
                    registerResult.removeSource(source);
                } else if (resource.isError()) {
                    hideLoading();
                    if (resource.error != null) {
                        setError(resource.error.getMessage());
                    } else {
                        setError("Lỗi đăng ký không xác định");
                    }
                    registerResult.setValue(resource);
                    registerResult.removeSource(source);
                }
            }
        });
    }

    /** Factory for ViewModelProvider */
    public static class Factory implements ViewModelProvider.Factory {
        private final android.content.Context context;

        public Factory(@NonNull android.content.Context context) {
            this.context = context.getApplicationContext();
        }

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(RegisterViewModel.class)) {
                return (T) new RegisterViewModel(new UserRepository(context));
            }
            throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
        }
    }
}
