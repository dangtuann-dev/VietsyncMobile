package com.app.learning.ui.onboarding;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;

/**
 * FadeSlidePageTransformer creates a parallax fade and slide page transition animation for ViewPager2.
 */
public class FadeSlidePageTransformer implements ViewPager2.PageTransformer {

    @Override
    public void transformPage(@NonNull View page, float position) {
        if (position < -1) {
            // Page is way off-screen to the left.
            page.setAlpha(0f);
        } else if (position <= 1) {
            // Fade the page based on position.
            page.setAlpha(1f - Math.abs(position));

            // Custom slide translation (parallax effect).
            // Canceling out some of the default scrolling motion creates a subtle, slower slide in/out.
            float translationX = -position * page.getWidth() * 0.5f;
            page.setTranslationX(translationX);
        } else {
            // Page is way off-screen to the right.
            page.setAlpha(0f);
        }
    }
}
