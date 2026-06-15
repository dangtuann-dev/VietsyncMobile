package com.app.learning.data.api;

import android.content.Context;

import com.app.learning.utils.AppConstants;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * ApiClient is a thread-safe singleton that configures and provides
 * instance mappings for Retrofit services. It constructs OkHttpClient
 * with interceptors to talk to the Supabase backend.
 */
public class ApiClient {

    private static volatile ApiClient instance;
    private Retrofit retrofit;

    private ApiClient(Context context, String supabaseApiKey) {
        // Logging Interceptor for debugging HTTP requests
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // Build OkHttpClient
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(AppConstants.CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(AppConstants.READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(AppConstants.WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .addInterceptor(new AuthInterceptor(context, supabaseApiKey))
                .addInterceptor(loggingInterceptor)
                .build();

        // Build Retrofit
        retrofit = new Retrofit.Builder()
                .baseUrl(AppConstants.API_BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    /**
     * Initializes the ApiClient singleton instance.
     *
     * @param context        Application context
     * @param supabaseApiKey Supabase API Key (anon/service_role key)
     * @return The initialized ApiClient instance
     */
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

    /**
     * Get the singleton instance. Throws exception if initialize was not called.
     */
    public static ApiClient getInstance() {
        if (instance == null) {
            throw new IllegalStateException("ApiClient must be initialized with initialize(context, apiKey) first.");
        }
        return instance;
    }

    /**
     * Create an implementation of the API endpoint defined by the service interface.
     *
     * @param serviceClass Retrofit interface definition
     * @param <T>          Type of the service
     * @return Service implementation
     */
    public <T> T createService(Class<T> serviceClass) {
        return retrofit.create(serviceClass);
    }
}
