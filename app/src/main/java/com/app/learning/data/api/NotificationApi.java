package com.app.learning.data.api;

import com.app.learning.data.model.NotificationModel;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface NotificationApi {

    @GET("rest/v1/notifications")
    Call<List<NotificationModel>> getNotifications(
            @Query("user_id") String userIdFilter,
            @Query("order") String order
    );

    @PATCH("rest/v1/notifications")
    Call<Void> updateNotification(
            @Query("id") String idFilter,
            @Body Map<String, Object> body
    );

    @PATCH("rest/v1/notifications")
    Call<Void> updateAllNotifications(
            @Query("user_id") String userIdFilter,
            @Body Map<String, Object> body
    );

    @DELETE("rest/v1/notifications")
    Call<Void> deleteNotification(
            @Query("id") String idFilter
    );

    @DELETE("rest/v1/notifications")
    Call<Void> deleteAllNotifications(
            @Query("user_id") String userIdFilter
    );

    @DELETE("rest/v1/notifications")
    Call<Void> deleteMultipleNotifications(
            @Query(value = "id", encoded = true) String idFilter
    );

    @POST("rest/v1/notifications")
    Call<Void> createNotification(
            @Body NotificationModel notification
    );
}
