package com.app.learning.data.api;

import com.app.learning.data.model.Course;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import com.app.learning.data.model.Category;
import com.app.learning.data.model.Lesson;
import com.google.gson.JsonObject;






public interface CourseApi {








    @GET("rest/v1/courses")
    Call<List<Course>> getCourses(
            @Query("select") String select,
            @Query("order") String order
    );








    @GET("rest/v1/courses")
    Call<List<Course>> getCoursesByCategory(
            @Query("category_id") String categoryIdFilter,
            @Query("select") String select
    );









    @GET("rest/v1/courses")
    Call<List<Course>> getCourseById(
            @Query("id") String idFilter,
            @Query("select") String select
    );









    @POST("rest/v1/courses")
    Call<List<Course>> createCourse(
            @Body Course course,
            @Header("Prefer") String preferRepresentation
    );

    @GET("rest/v1/courses")
    Call<List<Course>> searchCourses(
            @QueryMap Map<String, String> options
    );

    @GET("rest/v1/categories")
    Call<List<Category>> getCategories(
            @Query("select") String select
    );

    @GET("rest/v1/lessons")
    Call<List<Lesson>> getLessonsByCourseId(
            @Query("course_id") String courseIdFilter,
            @Query("order") String order
    );

    @GET("rest/v1/enrollments")
    Call<List<Map<String, Object>>> checkEnrollment(
            @Query("user_id") String userIdFilter,
            @Query("course_id") String courseIdFilter
    );

    @POST("rest/v1/enrollments")
    Call<Void> enrollInCourse(
            @Body Map<String, Object> body,
            @Header("Prefer") String preferHeader
    );

    @GET("rest/v1/lessons")
    Call<List<JsonObject>> getLessonById(
            @Query("id") String idFilter,
            @Query("select") String select
    );
}
