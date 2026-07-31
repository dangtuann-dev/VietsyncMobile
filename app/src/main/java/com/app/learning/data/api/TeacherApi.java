package com.app.learning.data.api;

import com.app.learning.data.model.Course;
import com.app.learning.data.model.Lesson;

import java.util.List;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface TeacherApi {

    @POST("rest/v1/courses")
    Call<List<Course>> createCourse(
            @Body Course course,
            @Header("Prefer") String preferRepresentation
    );

    @PATCH("rest/v1/courses")
    Call<List<Course>> updateCourse(
            @Query("id") String idFilter,
            @Body Map<String, Object> updates,
            @Header("Prefer") String preferRepresentation
    );

    @POST("rest/v1/lessons")
    Call<List<Lesson>> createLesson(
            @Body Lesson lesson,
            @Header("Prefer") String preferRepresentation
    );

    @PATCH("rest/v1/lessons")
    Call<List<Lesson>> updateLesson(
            @Query("id") String idFilter,
            @Body Map<String, Object> updates,
            @Header("Prefer") String preferRepresentation
    );

    @DELETE("rest/v1/lessons")
    Call<Void> deleteLesson(
            @Query("id") String idFilter
    );

    @GET("rest/v1/courses")
    Call<List<Course>> getCourses(
            @Query("instructor_id") String instructorIdFilter,
            @Query("select") String select
    );

    @GET("rest/v1/enrollments")
    Call<List<com.app.learning.data.model.Enrollment>> getEnrollments(
            @Query("course_id") String courseIdFilter,
            @Query("select") String select
    );

    @POST("rest/v1/enrollments")
    Call<List<com.app.learning.data.model.Enrollment>> addEnrollment(
            @Body Map<String, Object> body,
            @Header("Prefer") String preferRepresentation
    );

    @PATCH("rest/v1/enrollments")
    Call<Void> updateEnrollment(
            @Query("user_id") String userIdFilter,
            @Query("course_id") String courseIdFilter,
            @Body Map<String, Object> updates
    );

    @DELETE("rest/v1/enrollments")
    Call<Void> deleteEnrollment(
            @Query("user_id") String userIdFilter,
            @Query("course_id") String courseIdFilter
    );

    @GET("rest/v1/users")
    Call<List<com.app.learning.data.model.User>> getUserByEmail(
            @Query("email") String emailFilter
    );

    @POST("storage/v1/object/course_thumbnails/{filename}")
    Call<Map<String, String>> uploadThumbnail(
            @retrofit2.http.Path("filename") String filename,
            @Body RequestBody image,
            @Query("upsert") String upsert
    );
}
