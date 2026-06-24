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








    public LiveData<Resource<User>> login(String email, String password) {
        MutableLiveData<Resource<AuthResponse>> rawResponseLiveData = new MutableLiveData<>();
        MediatorLiveData<Resource<User>> resultLiveData = new MediatorLiveData<>();


        resultLiveData.setValue(Resource.loading());


        Call<AuthResponse> call = authApi.signIn(new AuthApi.SignInRequest(email, password));
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

                String role = authResponse.getUser().getRole();
                if (role == null || role.isEmpty()) {
                    role = "student";
                }
                user.setRole(role);


                userPreference.saveSession(authResponse.getAccessToken(), user);

                resultLiveData.setValue(Resource.success(user));
            } else if (resource.isError()) {
                resultLiveData.setValue(Resource.error(resource.error));
            }
        });

        return resultLiveData;
    }









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
                    userRole = role;
                }
                user.setRole(userRole);


                userPreference.saveSession(authResponse.getAccessToken(), user);

                resultLiveData.setValue(Resource.success(user));
            } else if (resource.isError()) {
                resultLiveData.setValue(Resource.error(resource.error));
            }
        });

        return resultLiveData;
    }




    public void logout() {
        userPreference.clearSession();
    }




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




    public LiveData<Resource<UserStats>> getUserStats(String userId) {
        MutableLiveData<Resource<UserStats>> resultLiveData = new MutableLiveData<>();
        resultLiveData.setValue(Resource.loading());

        executors.networkIO().execute(() -> {
            try {

                Call<List<UserApi.EnrollmentDto>> enrollmentsCall = userApi.getUserEnrollments("eq." + userId, "progress_percent");
                Response<List<UserApi.EnrollmentDto>> enrollmentsResponse = enrollmentsCall.execute();


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

                String publicUrl = BuildConfig.SUPABASE_URL + "/storage/v1/object/public/avatars/" + filename;

                String finalUrl = publicUrl + "?t=" + System.currentTimeMillis();
                resultLiveData.setValue(Resource.success(finalUrl));
            } else if (resource.isError()) {
                resultLiveData.setValue(Resource.error(resource.error));
            }
        });

        return resultLiveData;
    }




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




    public LiveData<Resource<List<com.app.learning.data.model.Certificate>>> getCertificates(String userId) {
        MutableLiveData<Resource<List<com.app.learning.data.model.Certificate>>> resultLiveData = new MutableLiveData<>();
        resultLiveData.setValue(Resource.loading());

        Call<List<com.app.learning.data.model.Certificate>> call = userApi.getFullUserCertificates(
                "eq." + userId,
                "id,course_id,issued_at,certificate_url,courses(title,thumbnail)"
        );
        executeCall(call, resultLiveData);

        return resultLiveData;
    }




    public LiveData<Resource<Map<String, Object>>> getUserSettingsFromSupabase(String userId) {
        MutableLiveData<Resource<List<Map<String, Object>>>> rawLiveData = new MutableLiveData<>();
        MediatorLiveData<Resource<Map<String, Object>>> resultLiveData = new MediatorLiveData<>();

        resultLiveData.setValue(Resource.loading());

        Call<List<Map<String, Object>>> call = userApi.getUserSettings("eq." + userId);
        executeCall(call, rawLiveData);

        resultLiveData.addSource(rawLiveData, resource -> {
            if (resource.isLoading()) {
                resultLiveData.setValue(Resource.loading());
            } else if (resource.isSuccess() && resource.data != null) {
                List<Map<String, Object>> list = resource.data;
                if (!list.isEmpty()) {
                    resultLiveData.setValue(Resource.success(list.get(0)));
                } else {

                    resultLiveData.setValue(Resource.success(new HashMap<>()));
                }
            } else if (resource.isError()) {
                resultLiveData.setValue(Resource.error(resource.error));
            }
        });

        return resultLiveData;
    }




    public LiveData<Resource<Void>> saveUserSettingsToSupabase(String userId, Map<String, Object> settings) {
        MutableLiveData<Resource<Void>> resultLiveData = new MutableLiveData<>();
        resultLiveData.setValue(Resource.loading());

        Map<String, Object> body = new HashMap<>(settings);
        body.put("user_id", userId);

        Call<Void> call = userApi.upsertUserSettings(body, "resolution=merge-duplicates");
        executeCall(call, resultLiveData);

        return resultLiveData;
    }
}
