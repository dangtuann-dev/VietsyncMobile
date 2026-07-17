package com.app.learning.data.api;

import com.app.learning.data.model.Review;
import com.app.learning.data.model.Enrollment;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface ReviewApi {

    @GET("rest/v1/reviews")
    Call<List<Review>> getReviews(
            @QueryMap Map<String, String> options
    );

    @POST("rest/v1/reviews")
    Call<Void> submitReview(
            @Body Map<String, Object> body
    );

    @PATCH("rest/v1/reviews")
    Call<Void> updateReviewHelpfulCount(
            @Query("id") String idFilter,
            @Body Map<String, Object> body
    );

    @GET("rest/v1/enrollments")
    Call<List<Enrollment>> getEnrollment(
            @Query("user_id") String userIdFilter,
            @Query("course_id") String courseIdFilter
    );
}
