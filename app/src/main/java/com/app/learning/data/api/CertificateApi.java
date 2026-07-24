package com.app.learning.data.api;

import com.app.learning.data.model.CertificateModel;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface CertificateApi {

    @GET("rest/v1/certificates")
    Call<List<CertificateModel>> getCertificates(@Query("user_id") String userId, @Query("select") String select);

    @GET("rest/v1/certificates")
    Call<List<CertificateModel>> getCertificateByCourse(@Query("user_id") String userId, @Query("course_id") String courseId, @Query("select") String select);

    @POST("rest/v1/certificates")
    Call<List<CertificateModel>> createCertificate(@Body JsonObject certificateData, @Query("select") String select);

    @GET("rest/v1/enrollments")
    Call<List<JsonObject>> checkEnrollmentProgress(@Query("user_id") String userId, @Query("course_id") String courseId, @Query("select") String select);
}
