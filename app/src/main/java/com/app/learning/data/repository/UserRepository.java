package com.app.learning.data.repository;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.app.learning.data.api.ApiClient;
import com.app.learning.data.api.ApiError;
import com.app.learning.data.api.AuthApi;
import com.app.learning.data.api.Resource;
import com.app.learning.data.api.UserApi;
import com.app.learning.data.model.AuthResponse;
import com.app.learning.data.model.User;
import com.app.learning.data.model.UserModel;
import com.app.learning.data.model.UserStats;
import com.app.learning.utils.UserPreference;
import com.example.vietsyncmobile.BuildConfig;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Response;

/**
 * UserRepository coordinates authentication tasks (login, registration) with Supabase GoTrue Auth services.
 * It automatically stores access tokens and profile snapshots locally on successful auth.
 */
public class UserRepository extends BaseRepository {

    private final AuthApi authApi;
    private final UserApi userApi;
    private final UserPreference userPreference;

    public UserRepository(@NonNull Context context) {
        super();
        this.authApi = ApiClient.getInstance().createService(AuthApi.class);
        this.userApi = ApiClient.getInstance().createService(UserApi.class);
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
                
                String role = authResponse.getUser().getRole();
                if (role == null || role.isEmpty()) {
                    role = "student";
                }
                user.setRole(role);

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
    public LiveData<Resource<User>> register(String email, String password, String fullName, String role) {
        MutableLiveData<Resource<AuthResponse>> rawResponseLiveData = new MutableLiveData<>();
        MediatorLiveData<Resource<User>> resultLiveData = new MediatorLiveData<>();

        resultLiveData.setValue(Resource.loading());

        java.util.Map<String, Object> metadata = new java.util.HashMap<>();
        metadata.put("full_name", fullName);
        metadata.put("role", role);

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
                
                String userRole = authResponse.getUser().getRole();
                if (userRole == null || userRole.isEmpty()) {
                    userRole = role; // Fallback to registration role choice
                }
                user.setRole(userRole);

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

    /**
     * Fetches user profile record from Supabase database.
     */
    public LiveData<Resource<User>> getUserProfile(String userId) {
        MutableLiveData<Resource<List<User>>> rawLiveData = new MutableLiveData<>();
        MediatorLiveData<Resource<User>> resultLiveData = new MediatorLiveData<>();

        resultLiveData.setValue(Resource.loading());

        Call<List<User>> call = userApi.getUser("eq." + userId, "*");
        executeCall(call, rawLiveData);

        resultLiveData.addSource(rawLiveData, resource -> {
            if (resource.isLoading()) {
                resultLiveData.setValue(Resource.loading());
            } else if (resource.isSuccess() && resource.data != null) {
                List<User> list = resource.data;
                if (!list.isEmpty()) {
                    User user = list.get(0);
                    // Cache locally in SharedPreferences
                    userPreference.updateUserProfile(user);
                    resultLiveData.setValue(Resource.success(user));
                } else {
                    resultLiveData.setValue(Resource.error(new ApiError("404", "Không tìm thấy hồ sơ người dùng", null, null)));
                }
            } else if (resource.isError()) {
                resultLiveData.setValue(Resource.error(resource.error));
            }
        });

        return resultLiveData;
    }

    /**
     * Updates profile fields in Supabase database.
     */
    public LiveData<Resource<User>> updateProfile(String userId, String fullName, String bio, String avatarUrl) {
        MutableLiveData<Resource<List<User>>> rawLiveData = new MutableLiveData<>();
        MediatorLiveData<Resource<User>> resultLiveData = new MediatorLiveData<>();

        resultLiveData.setValue(Resource.loading());

        Map<String, Object> fields = new HashMap<>();
        if (fullName != null) fields.put("full_name", fullName);
        if (bio != null) fields.put("bio", bio);
        if (avatarUrl != null) fields.put("avatar_url", avatarUrl);

        Call<List<User>> call = userApi.updateUser("eq." + userId, fields, "return=representation");
        executeCall(call, rawLiveData);

        resultLiveData.addSource(rawLiveData, resource -> {
            if (resource.isLoading()) {
                resultLiveData.setValue(Resource.loading());
            } else if (resource.isSuccess() && resource.data != null) {
                List<User> list = resource.data;
                if (!list.isEmpty()) {
                    User user = list.get(0);
                    // Sync locally
                    userPreference.updateUserProfile(user);
                    resultLiveData.setValue(Resource.success(user));
                } else {
                    resultLiveData.setValue(Resource.error(new ApiError("500", "Cập nhật hồ sơ thất bại", null, null)));
                }
            } else if (resource.isError()) {
                resultLiveData.setValue(Resource.error(resource.error));
            }
        });

        return resultLiveData;
    }

    /**
     * Coordinates multiple endpoints (enrollments + certificates) to calculate profile stats.
     */
    public LiveData<Resource<UserStats>> getUserStats(String userId) {
        MutableLiveData<Resource<UserStats>> resultLiveData = new MutableLiveData<>();
        resultLiveData.setValue(Resource.loading());

        executors.networkIO().execute(() -> {
            try {
                // Query enrollments count and completion status
                Call<List<UserApi.EnrollmentDto>> enrollmentsCall = userApi.getUserEnrollments("eq." + userId, "progress_percent");
                Response<List<UserApi.EnrollmentDto>> enrollmentsResponse = enrollmentsCall.execute();

                // Query certificates count
                Call<List<UserApi.CertificateDto>> certificatesCall = userApi.getUserCertificates("eq." + userId, "id");
                Response<List<UserApi.CertificateDto>> certificatesResponse = certificatesCall.execute();

                if (enrollmentsResponse.isSuccessful() && enrollmentsResponse.body() != null &&
                        certificatesResponse.isSuccessful() && certificatesResponse.body() != null) {

                    List<UserApi.EnrollmentDto> enrollments = enrollmentsResponse.body();
                    List<UserApi.CertificateDto> certificates = certificatesResponse.body();

                    int enrolledCount = enrollments.size();
                    int completedCount = 0;
                    for (UserApi.EnrollmentDto enrollment : enrollments) {
                        if (enrollment.getProgressPercent() == 100) {
                            completedCount++;
                        }
                    }
                    int certificatesCount = certificates.size();

                    UserStats stats = new UserStats(enrolledCount, completedCount, certificatesCount);
                    resultLiveData.postValue(Resource.success(stats));
                } else {
                    ApiError error = enrollmentsResponse.isSuccessful() ?
                            parseError(certificatesResponse) : parseError(enrollmentsResponse);
                    resultLiveData.postValue(Resource.error(error));
                }
            } catch (IOException e) {
                resultLiveData.postValue(Resource.error(new ApiError("503", "Không có kết nối mạng. Vui lòng thử lại.", e.getLocalizedMessage(), null)));
            } catch (Exception e) {
                resultLiveData.postValue(Resource.error(new ApiError("500", "Lỗi tải thống kê: " + e.getLocalizedMessage(), null, null)));
            }
        });

        return resultLiveData;
    }

    /**
     * Uploads user avatar file bytes to Supabase Storage and returns the public file URL.
     */
    public LiveData<Resource<String>> uploadAvatar(String userId, byte[] imageBytes, String mimeType) {
        MutableLiveData<Resource<Map<String, String>>> rawLiveData = new MutableLiveData<>();
        MediatorLiveData<Resource<String>> resultLiveData = new MediatorLiveData<>();

        resultLiveData.setValue(Resource.loading());

        String filename = userId + ".jpg";
        okhttp3.RequestBody body = okhttp3.RequestBody.create(
                imageBytes,
                okhttp3.MediaType.parse(mimeType)
        );

        Call<Map<String, String>> call = userApi.uploadAvatar(filename, body, "true");
        executeCall(call, rawLiveData);

        resultLiveData.addSource(rawLiveData, resource -> {
            if (resource.isLoading()) {
                resultLiveData.setValue(Resource.loading());
            } else if (resource.isSuccess() && resource.data != null) {
                // Construct public storage access url
                String publicUrl = BuildConfig.SUPABASE_URL + "/storage/v1/object/public/avatars/" + filename;
                // Append timestamp to break Glide cached images
                String finalUrl = publicUrl + "?t=" + System.currentTimeMillis();
                resultLiveData.setValue(Resource.success(finalUrl));
            } else if (resource.isError()) {
                resultLiveData.setValue(Resource.error(resource.error));
            }
        });

        return resultLiveData;
    }

    /**
     * Changes the current user password via GoTrue Auth API.
     */
    public LiveData<Resource<Void>> changePassword(String newPassword) {
        MutableLiveData<Resource<UserModel>> rawLiveData = new MutableLiveData<>();
        MediatorLiveData<Resource<Void>> resultLiveData = new MediatorLiveData<>();

        resultLiveData.setValue(Resource.loading());

        String token = userPreference.getAccessToken();
        if (token == null || token.isEmpty()) {
            resultLiveData.setValue(Resource.error(new ApiError("401", "Chưa đăng nhập", null, null)));
            return resultLiveData;
        }

        Call<UserModel> call = authApi.updateUser(
                "Bearer " + token,
                new AuthApi.UpdateUserRequest(newPassword)
        );
        executeCall(call, rawLiveData);

        resultLiveData.addSource(rawLiveData, resource -> {
            if (resource.isLoading()) {
                resultLiveData.setValue(Resource.loading());
            } else if (resource.isSuccess()) {
                resultLiveData.setValue(Resource.success(null));
            } else if (resource.isError()) {
                resultLiveData.setValue(Resource.error(resource.error));
            }
        });

        return resultLiveData;
    }
}
