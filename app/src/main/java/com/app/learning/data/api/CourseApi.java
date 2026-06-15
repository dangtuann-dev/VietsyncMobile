package com.app.learning.data.api;

import com.app.learning.data.model.Course;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * CourseApi defines the Retrofit routes for querying and writing course records
 * using the Supabase REST (PostgREST) API endpoints.
 */
public interface CourseApi {

    /**
     * Retrieves all course items with optional sorting and selection filters.
     *
     * @param select Projection filters (e.g. "*", "id,title,price")
     * @param order  Sort orders (e.g. "title.asc", "rating.desc")
     * @return Call carrying the list of matching Course records
     */
    @GET("rest/v1/courses")
    Call<List<Course>> getCourses(
            @Query("select") String select,
            @Query("order") String order
    );

    /**
     * Retrieves courses filtered by a specific category ID.
     *
     * @param categoryIdFilter Category filter parameter (e.g. "eq.1")
     * @param select           Projection filters (e.g. "*")
     * @return Call carrying the list of courses matching the category
     */
    @GET("rest/v1/courses")
    Call<List<Course>> getCoursesByCategory(
            @Query("category_id") String categoryIdFilter,
            @Query("select") String select
    );

    /**
     * Retrieves a single course record matching the ID.
     * In PostgREST, query matching by id returns a list containing that element.
     *
     * @param idFilter ID filter string (e.g. "eq.c0eebc99-9c0b-4ef8-bb6d-6bb9bd380001")
     * @param select   Projection filters
     * @return Call carrying a list containing the matching Course
     */
    @GET("rest/v1/courses")
    Call<List<Course>> getCourseById(
            @Query("id") String idFilter,
            @Query("select") String select
    );

    /**
     * Inserts a new course record.
     *
     * @param course               The course object data to create
     * @param preferRepresentation Header key to request database responses to return the newly inserted object
     *                             (e.g., "return=representation")
     * @return Call carrying the created Course record inside a returned list
     */
    @POST("rest/v1/courses")
    Call<List<Course>> createCourse(
            @Body Course course,
            @Header("Prefer") String preferRepresentation
    );
}
