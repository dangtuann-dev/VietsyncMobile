package com.app.learning.ui.onboarding;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.app.learning.ui.auth.LoginActivity;
import com.app.learning.ui.base.BaseActivity;
import com.app.learning.utils.AppConstants;
import com.example.vietsyncmobile.R;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;





public class OnboardingActivity extends BaseActivity {

    private ViewPager2 viewPager;
    private LinearLayout dotsLayout;
    private MaterialButton btnSkip;
    private MaterialButton btnNext;

    private List<OnboardingPage> onboardingPages;
    private OnboardingAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_onboarding;
    }

    @Override
    protected void initViews() {

        viewPager = findViewById(R.id.view_pager_onboarding);
        dotsLayout = findViewById(R.id.layout_dots);
        btnSkip = findViewById(R.id.btn_skip);
        btnNext = findViewById(R.id.btn_next);


        onboardingPages = new ArrayList<>();
        onboardingPages.add(new OnboardingPage(
                R.raw.learning_anywhere,
                "Học mọi lúc mọi nơi",
                "Học trực tuyến trên mọi thiết bị, linh hoạt sắp xếp thời gian biểu phù hợp với cuộc sống của bạn."
        ));
        onboardingPages.add(new OnboardingPage(
                R.raw.quality_courses,
                "Khóa học chất lượng",
                "Khám phá hàng ngàn khóa học được thiết kế chuyên nghiệp bởi các giảng viên và chuyên gia hàng đầu."
        ));
        onboardingPages.add(new OnboardingPage(
                R.raw.reputable_certificates,
                "Chứng chỉ uy tín",
                "Nhận chứng chỉ hoàn thành khóa học có giá trị quốc tế, mở rộng cơ hội thăng tiến sự nghiệp."
        ));


        adapter = new OnboardingAdapter(onboardingPages);
        viewPager.setAdapter(adapter);
        viewPager.setPageTransformer(new FadeSlidePageTransformer());


        setupDotsIndicator(onboardingPages.size());
        setCurrentDot(0);


        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentDot(position);


                if (position == onboardingPages.size() - 1) {
                    btnNext.setText("Bắt đầu ngay");
                    btnSkip.setVisibility(View.GONE);
                } else {
                    btnNext.setText("Tiếp tục");
                    btnSkip.setVisibility(View.VISIBLE);
                }
            }
        });


        btnSkip.setOnClickListener(v -> finishOnboarding());
        btnNext.setOnClickListener(v -> {
            int current = viewPager.getCurrentItem();
            if (current < onboardingPages.size() - 1) {
                viewPager.setCurrentItem(current + 1, true);
            } else {
                finishOnboarding();
            }
        });
    }

    @Override
    protected void initObservers() {

    }




    private void setupDotsIndicator(int count) {
        ImageView[] dots = new ImageView[count];
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        params.setMargins(8, 0, 8, 0);

        for (int i = 0; i < count; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(this, R.drawable.dot_inactive));
            dots[i].setLayoutParams(params);
            dotsLayout.addView(dots[i]);
        }
    }




    private void setCurrentDot(int position) {
        int childCount = dotsLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView dotView = (ImageView) dotsLayout.getChildAt(i);
            if (i == position) {
                dotView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.dot_active));
            } else {
                dotView.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.dot_inactive));
            }
        }
    }




    private void finishOnboarding() {
        SharedPreferences preferences = getSharedPreferences(AppConstants.PREF_NAME, Context.MODE_PRIVATE);
        preferences.edit().putBoolean("key_is_first_time", false).apply();

        Intent intent = new Intent(OnboardingActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
