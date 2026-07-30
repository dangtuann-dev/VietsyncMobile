package com.app.learning.data.repository;

import android.content.Context;
import com.app.learning.data.api.ApiClient;
import com.app.learning.data.api.FCMTokenApi;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FCMTokenRepository {

    private FCMTokenApi api;

    public FCMTokenRepository(Context context) {
        try {
            this.api = ApiClient.getInstance().createService(FCMTokenApi.class);
        } catch (Exception e) {
            this.api = null;
        }
    }

    public void saveFCMToken(String userId, String token) {
        if (api == null || token == null || token.isEmpty()) return;

        JsonObject body = new JsonObject();
        body.addProperty("user_id", userId);
        body.addProperty("fcm_token", token);
        body.addProperty("updated_at", System.currentTimeMillis());

        api.saveToken(body).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {}

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {}
        });
    }
}
