package com.app.learning;

import android.app.Application;
import com.app.learning.data.api.ApiClient;

/**
 * LearningApplication serves as the entry point for initialization task configurations.
 */
public class LearningApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        
        // Initialize the ApiClient singleton with the application context and a mock Supabase key
        ApiClient.initialize(this, "mock_supabase_anon_key_for_local_debugging");
    }
}
