package com.app.learning.data.api;

import com.app.learning.data.model.Course;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;





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
}
