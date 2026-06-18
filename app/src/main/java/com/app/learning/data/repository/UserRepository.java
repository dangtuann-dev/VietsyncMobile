package com.app.learning.data.repository;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.app.learning.data.api.ApiClient;
import com.app.learning.data.api.AuthApi;
import com.app.learning.data.api.Resource;
import com.app.learning.data.model.AuthResponse;
import com.app.learning.data.model.User;
import com.app.learning.utils.UserPreference;

import retrofit2.Call;

/**
 * UserRepository coordinates authentication tasks (login, registration) with Supabase GoTrue Auth services.
 * It automatically stores access tokens and profile snapshots locally on successful auth.
 */
public class UserRepository extends BaseRepository {

    private final AuthApi authApi;
    private final UserPreference userPreference;

    public UserRepository(@NonNull Context context) {
        super();
        this.authApi = ApiClient.getInstance().createService(AuthApi.class);
        this.userPreference = UserPreference.getInstance(context);
    }

    /**
     * Attempts login against Supabase Auth.
     *
     * @param email    User email
     * @param password User password
     * @return LiveData containing the logged in User details wrapped inside Resource status
     */
    public LiveData<Resource<User>> login(String email, String password) {
        MutableLiveData<Resource<AuthResponse>> rawResponseLiveData = new MutableLiveData<>();
        MediatorLiveData<Resource<User>> resultLiveData = new MediatorLiveData<>();

        // 1. Dispatch loading state
        resultLiveData.setValue(Resource.loading());

        // 2. Prepare and execute Retrofit Call
        Call<AuthResponse> call = authApi.signIn(new AuthApi.SignInRequest(email, password));
        executeCall(call, rawResponseLiveData);

        // 3. Coordinate raw network response to map object types and persist session
        resultLiveData.addSource(rawResponseLiveData, resource -> {
            if (resource.isLoading()) {
                resultLiveData.setValue(Resource.loading());
            } else if (resource.isSuccess() && resource.data != null) {
                AuthResponse authResponse = resource.data;

                // Map auth response to domain User model
                User user = new User();
                user.setId(authResponse.getUser().getId());
                user.setEmail(authResponse.getUser().getEmail());
                user.setFullName(authResponse.getUser().getFullName());
                user.setRole("student"); // Default fallback role

                // Save JWT and credentials to SharedPreferences
                userPreference.saveSession(authResponse.getAccessToken(), user);

                resultLiveData.setValue(Resource.success(user));
            } else if (resource.isError()) {
                resultLiveData.setValue(Resource.error(resource.error));
            }
        });

        return resultLiveData;
    }

    /**
     * Registers a new account and triggers automatic login.
     *
     * @param email    New user email
     * @param password New user password
     * @param fullName New user display name
     * @return LiveData containing the registered User details wrapped inside Resource status
     */
    public LiveData<Resource<User>> register(String email, String password, String fullName) {
        MutableLiveData<Resource<AuthResponse>> rawResponseLiveData = new MutableLiveData<>();
        MediatorLiveData<Resource<User>> resultLiveData = new MediatorLiveData<>();

        resultLiveData.setValue(Resource.loading());

        java.util.Map<String, Object> metadata = new java.util.HashMap<>();
        metadata.put("full_name", fullName);

        Call<AuthResponse> call = authApi.signUp(new AuthApi.SignUpRequest(email, password, metadata));
        executeCall(call, rawResponseLiveData);

        resultLiveData.addSource(rawResponseLiveData, resource -> {
            if (resource.isLoading()) {
                resultLiveData.setValue(Resource.loading());
            } else if (resource.isSuccess() && resource.data != null) {
                AuthResponse authResponse = resource.data;

                User user = new User();
                user.setId(authResponse.getUser().getId());
                user.setEmail(authResponse.getUser().getEmail());
                user.setFullName(authResponse.getUser().getFullName());
                user.setRole("student");

                // Save credentials to local storage
                userPreference.saveSession(authResponse.getAccessToken(), user);

                resultLiveData.setValue(Resource.success(user));
            } else if (resource.isError()) {
                resultLiveData.setValue(Resource.error(resource.error));
            }
        });

        return resultLiveData;
    }

    /**
     * Signs out the user by wiping credentials.
     */
    public void logout() {
        userPreference.clearSession();
    }
}
