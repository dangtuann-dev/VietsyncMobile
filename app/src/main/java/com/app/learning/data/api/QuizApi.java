package com.app.learning.data.api;

import com.app.learning.data.model.QuizQuestionModel;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface QuizApi {

    @GET("rest/v1/quizzes")
    Call<List<QuizQuestionModel>> getQuizzesByLesson(@Query("lesson_id") String lessonId, @Query("select") String select);

    @POST("rest/v1/quiz_attempts")
    Call<List<JsonObject>> submitQuizAttempt(@Body JsonObject attemptData);
}
