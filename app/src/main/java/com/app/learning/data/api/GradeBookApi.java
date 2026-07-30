package com.app.learning.data.api;

import com.google.gson.JsonObject;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GradeBookApi {

    @GET("rest/v1/quiz_attempts")
    Call<List<JsonObject>> getUserQuizAttempts(
            @Query("select") String select,
            @Query("user_id") String userIdFilter,
            @Query("quizzes.lessons.course_id") String courseIdFilter
    );

    @GET("rest/v1/quiz_attempts")
    Call<List<JsonObject>> getClassAverageAttempts(
            @Query("select") String select,
            @Query("quizzes.lessons.course_id") String courseIdFilter
    );
}
