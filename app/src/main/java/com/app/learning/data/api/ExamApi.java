package com.app.learning.data.api;

import com.app.learning.data.model.ExamAttemptModel;
import com.app.learning.data.model.QuizQuestionModel;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ExamApi {

    @GET("rest/v1/exam_attempts")
    Call<List<ExamAttemptModel>> getExamAttempts(
            @Query("user_id") String userIdFilter,
            @Query("course_id") String courseIdFilter,
            @Query("order") String order
    );

    @POST("rest/v1/exam_attempts")
    Call<List<ExamAttemptModel>> submitExamAttempt(
            @Body ExamAttemptModel attempt,
            @Header("Prefer") String preferHeader
    );

    @GET("rest/v1/quizzes")
    Call<List<QuizQuestionModel>> getQuizzesForCourse(
            @Query("select") String select,
            @Query("lessons.course_id") String courseIdFilter
    );

    @PATCH("rest/v1/enrollments")
    Call<Void> updateEnrollmentProgress(
            @Query("user_id") String userIdFilter,
            @Query("course_id") String courseIdFilter,
            @Body Map<String, Object> body
    );
}
