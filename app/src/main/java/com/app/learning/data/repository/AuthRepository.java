package com.app.learning.data.repository;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.app.learning.data.api.ApiClient;
import com.app.learning.data.api.ApiError;
import com.app.learning.data.api.AuthApi;
import com.app.learning.data.api.Resource;
import com.app.learning.data.model.AuthResponse;
import com.app.learning.data.model.UserModel;
import com.app.learning.utils.SessionManager;

import java.io.IOException;
import java.util.Map;

import retrofit2.Call;

/**
 * AuthRepository coordinates Supabase Auth network requests and persists credentials
 * securely through the {@link SessionManager}.
 */
public class AuthRepository extends BaseRepository {

    private final AuthApi authApi;
    private final SessionManager sessionManager;

    public AuthRepository(@NonNull Context context) {
        super();
        this.authApi = ApiClient.getInstance().createService(AuthApi.class);
        this.sessionManager = SessionManager.getInstance(context);
    }

    /**
     * Authenticates user with email and password.
     * Saves session payload on success.
     */
    public LiveData<Resource<AuthResponse>> login(String email, String password) {
        MutableLiveData<Resource<AuthResponse>> rawLiveData = new MutableLiveData<>();
        MediatorLiveData<Resource<AuthResponse>> resultLiveData = new MediatorLiveData<>();

        resultLiveData.setValue(Resource.loading());

        Call<AuthResponse> call = authApi.signIn(new AuthApi.SignInRequest(email, password));
        executeCall(call, rawLiveData);

        resultLiveData.addSource(rawLiveData, resource -> {
            if (resource.isLoading()) {
                resultLiveData.setValue(Resource.loading());
            } else if (resource.isSuccess() && resource.data != null) {
                // Save token and user details to secure storage
                sessionManager.saveSession(resource.data);
                resultLiveData.setValue(Resource.success(resource.data));
            } else if (resource.isError()) {
                resultLiveData.setValue(Resource.error(resource.error));
            }
        });

        return resultLiveData;
    }

    /**
     * Registers a new user with Supabase Auth, accepting custom metadata mapping.
     */
    public LiveData<Resource<AuthResponse>> register(String email, String password, Map<String, Object> metadata) {
        MutableLiveData<Resource<AuthResponse>> rawLiveData = new MutableLiveData<>();
        MediatorLiveData<Resource<AuthResponse>> resultLiveData = new MediatorLiveData<>();

        resultLiveData.setValue(Resource.loading());

        Call<AuthResponse> call = authApi.signUp(new AuthApi.SignUpRequest(email, password, metadata));
        executeCall(call, rawLiveData);

        resultLiveData.addSource(rawLiveData, resource -> {
            if (resource.isLoading()) {
                resultLiveData.setValue(Resource.loading());
            } else if (resource.isSuccess() && resource.data != null) {
                sessionManager.saveSession(resource.data);
                resultLiveData.setValue(Resource.success(resource.data));
            } else if (resource.isError()) {
                resultLiveData.setValue(Resource.error(resource.error));
            }
        });

        return resultLiveData;
    }

    /**
     * Signs out the user locally and notifies Supabase server.
     * Wipes secure preferences immediately regardless of network outcomes.
     */
    public LiveData<Resource<Void>> logout() {
        MutableLiveData<Resource<Void>> resultLiveData = new MutableLiveData<>();
        resultLiveData.setValue(Resource.loading());

        String token = sessionManager.getAccessToken();
        // Wipe local session instantly
        sessionManager.clearSession();

        if (token == null || token.trim().isEmpty()) {
            resultLiveData.setValue(Resource.success(null));
            return resultLiveData;
        }

        Call<Void> call = authApi.logout("Bearer " + token);
        executors.networkIO().execute(() -> {
            try {
                // Synchronously call logout endpoint on background network thread
                call.execute();
                resultLiveData.postValue(Resource.success(null));
            } catch (IOException e) {
                // Return success even on failure, as the local session is cleared
                resultLiveData.postValue(Resource.success(null));
            }
        });

        return resultLiveData;
    }

    /**
     * Asynchronously refreshes JWT access token using the stored refresh token.
     */
    public LiveData<Resource<AuthResponse>> refreshToken() {
        MutableLiveData<Resource<AuthResponse>> rawLiveData = new MutableLiveData<>();
        MediatorLiveData<Resource<AuthResponse>> resultLiveData = new MediatorLiveData<>();

        resultLiveData.setValue(Resource.loading());

        String refreshToken = sessionManager.getRefreshToken();
        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            resultLiveData.setValue(Resource.error(new ApiError("400", "No refresh token available", null, null)));
            return resultLiveData;
        }

        Call<AuthResponse> call = authApi.refreshToken(new AuthApi.RefreshTokenRequest(refreshToken));
        executeCall(call, rawLiveData);

        resultLiveData.addSource(rawLiveData, resource -> {
            if (resource.isLoading()) {
                resultLiveData.setValue(Resource.loading());
            } else if (resource.isSuccess() && resource.data != null) {
                sessionManager.saveSession(resource.data);
                resultLiveData.setValue(Resource.success(resource.data));
            } else if (resource.isError()) {
                resultLiveData.setValue(Resource.error(resource.error));
            }
        });

        return resultLiveData;
    }

    /**
     * Synchronously refreshes JWT access token.
     * Primarily designed to run inside background Services or Workers.
     *
     * @return The updated AuthResponse if successful, or null if no refresh token found.
     * @throws Exception If network call fails or server returns error.
     */
    @Nullable
    public AuthResponse refreshTokenSync() throws Exception {
        String refreshToken = sessionManager.getRefreshToken();
        if (refreshToken == null || refreshToken.trim().isEmpty()) {
            return null;
        }

        Call<AuthResponse> call = authApi.refreshToken(new AuthApi.RefreshTokenRequest(refreshToken));
        retrofit2.Response<AuthResponse> response = call.execute();

        if (response.isSuccessful() && response.body() != null) {
            AuthResponse authResponse = response.body();
            sessionManager.saveSession(authResponse);
            return authResponse;
        } else {
            String errMsg = "Token refresh failed with code: " + response.code();
            if (response.errorBody() != null) {
                errMsg += ", body: " + response.errorBody().string();
            }
            throw new Exception(errMsg);
        }
    }

    /**
     * Fetches details of the current authenticated user from Supabase.
     */
    public LiveData<Resource<UserModel>> getUser() {
        MutableLiveData<Resource<UserModel>> resultLiveData = new MutableLiveData<>();
        resultLiveData.setValue(Resource.loading());

        String token = sessionManager.getAccessToken();
        if (token == null || token.trim().isEmpty()) {
            resultLiveData.setValue(Resource.error(new ApiError("401", "Unauthorized: No token found", null, null)));
            return resultLiveData;
        }

        Call<UserModel> call = authApi.getUser("Bearer " + token);
        executors.networkIO().execute(() -> {
            try {
                retrofit2.Response<UserModel> response = call.execute();
                if (response.isSuccessful() && response.body() != null) {
                    UserModel user = response.body();
                    sessionManager.updateUser(user);
                    resultLiveData.postValue(Resource.success(user));
                } else {
                    resultLiveData.postValue(Resource.error(new ApiError(
                            String.valueOf(response.code()),
                            "Failed to retrieve user: Code " + response.code(),
                            null,
                            null
                    )));
                }
            } catch (IOException e) {
                resultLiveData.postValue(Resource.error(new ApiError(
                        "503",
                        "Không có kết nối mạng. Vui lòng thử lại.",
                        e.getMessage(),
                        null
                )));
            }
        });

        return resultLiveData;
    }
}
