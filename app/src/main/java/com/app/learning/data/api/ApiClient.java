package com.app.learning.data.api;

import android.content.Context;

import com.app.learning.utils.AppConstants;
import com.example.vietsyncmobile.BuildConfig;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;






public class ApiClient {

    private static volatile ApiClient instance;
    private Retrofit retrofit;

    private ApiClient(Context context, String supabaseApiKey) {

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);


        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(AppConstants.CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(AppConstants.READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(AppConstants.WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .addInterceptor(new AuthInterceptor(context, supabaseApiKey))
                .addInterceptor(loggingInterceptor)
                .build();


        String baseUrl = BuildConfig.SUPABASE_URL;
        if (baseUrl == null || baseUrl.trim().isEmpty()) {
            baseUrl = AppConstants.API_BASE_URL;
        } else if (!baseUrl.endsWith("/")) {
            baseUrl = baseUrl + "/";
        }


        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }








    public static ApiClient initialize(Context context, String supabaseApiKey) {
        if (instance == null) {
            synchronized (ApiClient.class) {
                if (instance == null) {
                    instance = new ApiClient(context, supabaseApiKey);
                }
            }
        }
        return instance;
    }




    public static ApiClient getInstance() {
        if (instance == null) {
            throw new IllegalStateException("ApiClient must be initialized with initialize(context, apiKey) first.");
        }
        return instance;
    }








    public <T> T createService(Class<T> serviceClass) {
        return retrofit.create(serviceClass);
    }
}
