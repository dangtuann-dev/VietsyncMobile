package com.app.learning;

import android.app.Application;
import com.app.learning.data.api.ApiClient;
import com.example.vietsyncmobile.BuildConfig;

/**
 * LearningApplication serves as the entry point for initialization task configurations.
 */
public class LearningApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        
        // Initialize the ApiClient singleton with the application context and the Supabase key from BuildConfig
        ApiClient.initialize(this, BuildConfig.SUPABASE_ANON_KEY);
    }
}
