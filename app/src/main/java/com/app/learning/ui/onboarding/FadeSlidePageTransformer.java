package com.app.learning.ui.onboarding;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.viewpager2.widget.ViewPager2;




public class FadeSlidePageTransformer implements ViewPager2.PageTransformer {

    @Override
    public void transformPage(@NonNull View page, float position) {
        if (position < -1) {

            page.setAlpha(0f);
        } else if (position <= 1) {

            page.setAlpha(1f - Math.abs(position));



            float translationX = -position * page.getWidth() * 0.5f;
            page.setTranslationX(translationX);
        } else {

            page.setAlpha(0f);
        }
    }
}
