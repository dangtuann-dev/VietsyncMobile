package com.app.learning.data.api;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.app.learning.utils.AppConstants;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * AuthInterceptor intercepts all outbound HTTP requests to append
 * necessary authentication headers (API Key and JWT access tokens)
 * before transmission to the Supabase REST server.
 */
public class AuthInterceptor implements Interceptor {

    private final Context context;
    private final String supabaseApiKey;

    public AuthInterceptor(Context context, String supabaseApiKey) {
        this.context = context.getApplicationContext();
        this.supabaseApiKey = supabaseApiKey;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Request.Builder requestBuilder = originalRequest.newBuilder()
                // Supabase API Key header (always required)
                .header("apikey", supabaseApiKey)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json");

        // Dynamically fetch and attach JWT access token from Shared Preferences if logged in
        SharedPreferences preferences = context.getSharedPreferences(AppConstants.PREF_NAME, Context.MODE_PRIVATE);
        String token = preferences.getString(AppConstants.PREF_KEY_ACCESS_TOKEN, null);

        if (token != null && !token.trim().isEmpty()) {
            requestBuilder.header(
                    AppConstants.HEADER_AUTHORIZATION,
                    AppConstants.HEADER_BEARER_PREFIX + token
            );
        }

        return chain.proceed(requestBuilder.build());
    }
}
