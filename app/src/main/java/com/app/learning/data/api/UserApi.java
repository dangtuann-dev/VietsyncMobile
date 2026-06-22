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

/**
 * UserApi defines endpoints for user metadata, stats retrieval, and avatar upload
 * directly communicating with Supabase database and storage systems.
 */
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

    /**
     * Gets user profile row by user ID.
     */
    @GET("rest/v1/users")
    Call<List<User>> getUser(
            @Query("id") String idFilter,
            @Query("select") String select
    );

    /**
     * Updates user profile fields by user ID.
     */
    @PATCH("rest/v1/users")
    Call<List<User>> updateUser(
            @Query("id") String idFilter,
            @Body Map<String, Object> body,
            @Header("Prefer") String preferRepresentation
    );

    /**
     * Retrieves user enrollments to count stats.
     */
    @GET("rest/v1/enrollments")
    Call<List<EnrollmentDto>> getUserEnrollments(
            @Query("user_id") String userIdFilter,
            @Query("select") String select
    );

    /**
     * Retrieves user certificates to count achievement stats.
     */
    @GET("rest/v1/certificates")
    Call<List<CertificateDto>> getUserCertificates(
            @Query("user_id") String userIdFilter,
            @Query("select") String select
    );

    /**
     * Retrieves user certificates with complete course details.
     */
    @GET("rest/v1/certificates")
    Call<List<com.app.learning.data.model.Certificate>> getFullUserCertificates(
            @Query("user_id") String userIdFilter,
            @Query("select") String select
    );

    /**
     * Uploads raw avatar image bytes to the "avatars" bucket on Supabase Storage.
     * We pass x-upsert header to overwrite old avatars.
     */
    @POST("storage/v1/object/avatars/{filename}")
    Call<Map<String, String>> uploadAvatar(
            @Path("filename") String filename,
            @Body RequestBody imageBytes,
            @Header("x-upsert") String xUpsert
    );
}
