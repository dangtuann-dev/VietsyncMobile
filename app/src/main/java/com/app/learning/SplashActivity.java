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
import com.app.learning.MainActivity;
import com.app.learning.ui.onboarding.OnboardingActivity;
import com.app.learning.utils.AppConstants;
import com.example.vietsyncmobile.R;






public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY_MS = 2000;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        View logoContainer = findViewById(R.id.logo_container);
        if (logoContainer != null) {
            Animator logoAnimator = AnimatorInflater.loadAnimator(this, R.animator.splash_animation);
            logoAnimator.setTarget(logoContainer);
            logoAnimator.start();
        }


        new Handler(Looper.getMainLooper()).postDelayed(this::checkAuthenticationAndNavigate, SPLASH_DELAY_MS);
    }




    private void checkAuthenticationAndNavigate() {
        SharedPreferences sharedPreferences = getSharedPreferences(AppConstants.PREF_NAME, Context.MODE_PRIVATE);
        boolean isFirstTime = sharedPreferences.getBoolean("key_is_first_time", true);
        boolean isLoggedIn = sharedPreferences.getBoolean(AppConstants.PREF_KEY_IS_LOGGED_IN, false);

        Intent intent;
        if (isFirstTime) {
            intent = new Intent(SplashActivity.this, OnboardingActivity.class);
        } else if (isLoggedIn) {
            intent = new Intent(SplashActivity.this, MainActivity.class);
        } else {
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        }

        startActivity(intent);
        finish();


        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
