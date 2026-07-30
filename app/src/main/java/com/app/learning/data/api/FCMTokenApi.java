package com.app.learning.data.api;

import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface FCMTokenApi {
    @POST("rest/v1/fcm_tokens")
    Call<JsonObject> saveToken(@Body JsonObject body);
}
