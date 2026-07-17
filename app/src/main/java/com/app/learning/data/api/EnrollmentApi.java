package com.app.learning.data.api;

import com.app.learning.data.model.Enrollment;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface EnrollmentApi {

    @GET("rest/v1/enrollments")
    Call<List<Enrollment>> getEnrollments(
            @Query("user_id") String userIdFilter,
            @Query("select") String select
    );

    @GET("rest/v1/enrollments")
    Call<List<Enrollment>> getEnrollmentsFiltered(
            @Query("user_id") String userIdFilter,
            @Query("progress_percent") String progressPercentFilter,
            @Query("select") String select
    );
}
