package com.app.learning.data.api;

import com.app.learning.data.model.User;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;





public interface UserApi {

    class EnrollmentDto {
        @SerializedName("progress_percent")
        private int progressPercent;

        public int getProgressPercent() {
            return progressPercent;
        }

        public void setProgressPercent(int progressPercent) {
            this.progressPercent = progressPercent;
        }
    }

    class CertificateDto {
        @SerializedName("id")
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }




    @GET("rest/v1/users")
    Call<List<User>> getUser(
            @Query("id") String idFilter,
            @Query("select") String select
    );




    @PATCH("rest/v1/users")
    Call<List<User>> updateUser(
            @Query("id") String idFilter,
            @Body Map<String, Object> body,
            @Header("Prefer") String preferRepresentation
    );




    @GET("rest/v1/enrollments")
    Call<List<EnrollmentDto>> getUserEnrollments(
            @Query("user_id") String userIdFilter,
            @Query("select") String select
    );




    @GET("rest/v1/certificates")
    Call<List<CertificateDto>> getUserCertificates(
            @Query("user_id") String userIdFilter,
            @Query("select") String select
    );




    @GET("rest/v1/certificates")
    Call<List<com.app.learning.data.model.Certificate>> getFullUserCertificates(
            @Query("user_id") String userIdFilter,
            @Query("select") String select
    );





    @POST("storage/v1/object/avatars/{filename}")
    Call<Map<String, String>> uploadAvatar(
            @Path("filename") String filename,
            @Body RequestBody imageBytes,
            @Header("x-upsert") String xUpsert
    );




    @GET("rest/v1/user_settings")
    Call<List<Map<String, Object>>> getUserSettings(
            @Query("user_id") String userIdFilter
    );




    @POST("rest/v1/user_settings")
    Call<Void> upsertUserSettings(
            @Body Map<String, Object> body,
            @Header("Prefer") String preferHeader
    );
}
