package com.app.learning;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.app.learning.ui.auth.LoginActivity;
import com.app.learning.ui.home.HomeActivity;
import com.app.learning.utils.AppConstants;
import com.example.vietsyncmobile.R;

/**
 * SplashActivity represents the starting landing screen of the application.
 * It is compatible with Android 12+ SplashScreen API and performs an entry scale/fade logo transition
 * before verifying the active login session and routing the user to the proper flow.
 */
public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY_MS = 2000; // 2 seconds display time

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // 1. Install Android 12+ SplashScreen API BEFORE calling super.onCreate()
        SplashScreen.installSplashScreen(this);
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // 2. Load and play the premium entrance animation
        View logoContainer = findViewById(R.id.logo_container);
        if (logoContainer != null) {
            Animator logoAnimator = AnimatorInflater.loadAnimator(this, R.animator.splash_animation);
            logoAnimator.setTarget(logoContainer);
            logoAnimator.start();
        }

        // 3. Keep the screen active for 2 seconds and then proceed with security navigation checks
        new Handler(Looper.getMainLooper()).postDelayed(this::checkAuthenticationAndNavigate, SPLASH_DELAY_MS);
    }

    /**
     * Inspects local storage for user credentials and routes them to the dashboard or login portals.
     */
    private void checkAuthenticationAndNavigate() {
        SharedPreferences sharedPreferences = getSharedPreferences(AppConstants.PREF_NAME, Context.MODE_PRIVATE);
        boolean isLoggedIn = sharedPreferences.getBoolean(AppConstants.PREF_KEY_IS_LOGGED_IN, false);

        Intent intent;
        if (isLoggedIn) {
            intent = new Intent(SplashActivity.this, HomeActivity.class);
        } else {
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        }

        startActivity(intent);
        finish(); // Remove SplashActivity from back stack

        // Apply a smooth transition between screens
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
