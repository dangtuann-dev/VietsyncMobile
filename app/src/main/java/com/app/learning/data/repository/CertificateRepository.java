package com.app.learning.data.repository;

import android.content.Context;

import com.app.learning.data.api.ApiClient;
import com.app.learning.data.api.CertificateApi;
import com.app.learning.data.model.CertificateModel;
import com.app.learning.utils.SessionManager;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CertificateRepository {

    public interface EligibilityCallback {
        void onResult(boolean isEligible, String message);
    }

    public interface CertificateCallback {
        void onSuccess(CertificateModel certificate);
        void onError(String error);
    }

    public interface ListCallback {
        void onSuccess(List<CertificateModel> certificates);
        void onError(String error);
    }

    private final CertificateApi certificateApi;
    private final SessionManager sessionManager;
    private final Context context;

    public CertificateRepository(Context context) {
        this.context = context != null ? context.getApplicationContext() : null;
        this.certificateApi = ApiClient.getInstance().createService(CertificateApi.class);
        this.sessionManager = SessionManager.getInstance(context);
    }

    private String getCurrentUserName() {
        if (context != null) {
            com.app.learning.data.model.User profile = com.app.learning.utils.UserPreference.getInstance(context).getUserProfile();
            if (profile != null && profile.getFullName() != null && !profile.getFullName().trim().isEmpty()) {
                return profile.getFullName().trim();
            }
        }
        if (sessionManager != null && sessionManager.getUserFullName() != null && !sessionManager.getUserFullName().trim().isEmpty()) {
            return sessionManager.getUserFullName().trim();
        }
        return "Học viên Vietsync";
    }

    public void checkEligibility(String courseId, EligibilityCallback callback) {
        callback.onResult(true, "Đã sẵn sàng tạo chứng chỉ!");
    }

    public void generateCertificate(String courseId, String courseTitle, String instructorName, int hours, CertificateCallback callback) {
        String userId = sessionManager != null ? sessionManager.getUserId() : "user-101";
        String userName = getCurrentUserName();

        String finalUserName = userName;

        certificateApi.getCertificateByCourse("eq." + userId, "eq." + courseId, "*").enqueue(new Callback<List<CertificateModel>>() {
            @Override
            public void onResponse(Call<List<CertificateModel>> call, Response<List<CertificateModel>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    CertificateModel existing = response.body().get(0);
                    existing.setCourseTitle(courseTitle);
                    existing.setUserName(finalUserName);
                    existing.setInstructorName(instructorName);
                    existing.setDurationHours(hours);
                    callback.onSuccess(existing);
                } else {
                    createNewCertificate(userId, courseId, courseTitle, finalUserName, instructorName, hours, callback);
                }
            }

            @Override
            public void onFailure(Call<List<CertificateModel>> call, Throwable t) {
                createNewCertificate(userId, courseId, courseTitle, finalUserName, instructorName, hours, callback);
            }
        });
    }

    private void createNewCertificate(String userId, String courseId, String courseTitle, String userName, String instructorName, int hours, CertificateCallback callback) {
        String certId = UUID.randomUUID().toString();
        String verifyUrl = "https://vietsync.edu.vn/verify/" + certId;

        JsonObject body = new JsonObject();
        body.addProperty("id", certId);
        body.addProperty("user_id", userId);
        body.addProperty("course_id", courseId);
        body.addProperty("certificate_url", verifyUrl);

        certificateApi.createCertificate(body, "*").enqueue(new Callback<List<CertificateModel>>() {
            @Override
            public void onResponse(Call<List<CertificateModel>> call, Response<List<CertificateModel>> response) {
                CertificateModel model = new CertificateModel();
                model.setId(certId);
                model.setUserId(userId);
                model.setCourseId(courseId);
                model.setCertificateUrl(verifyUrl);
                model.setCourseTitle(courseTitle);
                model.setUserName(userName);
                model.setInstructorName(instructorName);
                model.setDurationHours(hours);
                model.setIssuedAt(new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(new java.util.Date()));
                callback.onSuccess(model);
            }

            @Override
            public void onFailure(Call<List<CertificateModel>> call, Throwable t) {
                CertificateModel model = new CertificateModel();
                model.setId(certId);
                model.setUserId(userId);
                model.setCourseId(courseId);
                model.setCertificateUrl(verifyUrl);
                model.setCourseTitle(courseTitle);
                model.setUserName(userName);
                model.setInstructorName(instructorName);
                model.setDurationHours(hours);
                model.setIssuedAt(new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(new java.util.Date()));
                callback.onSuccess(model);
            }
        });
    }

    public void getUserCertificates(ListCallback callback) {
        String userId = sessionManager.getUserId();
        certificateApi.getCertificates("eq." + userId, "*").enqueue(new Callback<List<CertificateModel>>() {
            @Override
            public void onResponse(Call<List<CertificateModel>> call, Response<List<CertificateModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Không thể tải chứng chỉ");
                }
            }

            @Override
            public void onFailure(Call<List<CertificateModel>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getCertificateById(String certId, CertificateCallback callback) {
        certificateApi.getCertificateById("eq." + certId, "*").enqueue(new Callback<List<CertificateModel>>() {
            @Override
            public void onResponse(Call<List<CertificateModel>> call, Response<List<CertificateModel>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    callback.onSuccess(response.body().get(0));
                } else {
                    callback.onError("Không tìm thấy chứng chỉ hợp lệ!");
                }
            }

            @Override
            public void onFailure(Call<List<CertificateModel>> call, Throwable t) {
                callback.onError("Lỗi kết nối mạng: " + t.getMessage());
            }
        });
    }
}
