package com.app.learning.data.api;

import com.app.learning.data.model.WishlistModel;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface WishlistApi {

    @GET("rest/v1/wishlists")
    Call<List<WishlistModel>> getWishlist(
            @Query("user_id") String userIdFilter,
            @Query("select") String select
    );

    @POST("rest/v1/wishlists")
    Call<Void> addToWishlist(
            @Body WishlistModel wishlist
    );

    @DELETE("rest/v1/wishlists")
    Call<Void> removeFromWishlist(
            @Query("user_id") String userIdFilter,
            @Query("course_id") String courseIdFilter
    );

    @POST("rest/v1/enrollments")
    Call<Void> enrollInCourse(
            @Body Map<String, Object> body,
            @Header("Prefer") String preferHeader
    );
}
