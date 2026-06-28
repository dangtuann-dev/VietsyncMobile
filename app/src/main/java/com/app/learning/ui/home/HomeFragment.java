package com.app.learning.ui.home;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.app.learning.data.model.User;
import com.app.learning.ui.base.BaseFragment;
import com.app.learning.utils.UserPreference;
import com.bumptech.glide.Glide;
import com.example.vietsyncmobile.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeFragment extends BaseFragment {

    private TextView tvGreeting;
    private TextView tvStudentName;
    private CircleImageView ivAvatar;
    private EditText etSearch;
    private ViewPager2 vpBanners;
    private RecyclerView rvCategories;
    private RecyclerView rvFeaturedCourses;
    private RecyclerView rvContinueLearning;
    private RecyclerView rvPopularCourses;

    private HomeViewModel viewModel;
    private final Handler bannerHandler = new Handler(Looper.getMainLooper());
    private Runnable bannerRunnable;
    private int bannerCount = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initViews(View view) {
        tvGreeting = view.findViewById(R.id.tv_greeting);
        tvStudentName = view.findViewById(R.id.tv_student_name);
        ivAvatar = view.findViewById(R.id.iv_avatar);
        etSearch = view.findViewById(R.id.et_search);
        vpBanners = view.findViewById(R.id.vp_banners);
        rvCategories = view.findViewById(R.id.rv_categories);
        rvFeaturedCourses = view.findViewById(R.id.rv_featured_courses);
        rvContinueLearning = view.findViewById(R.id.rv_continue_learning);
        rvPopularCourses = view.findViewById(R.id.rv_popular_courses);

        rvCategories.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rvFeaturedCourses.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rvContinueLearning.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvContinueLearning.setNestedScrollingEnabled(false);
        rvPopularCourses.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));

        loadUserProfile();
    }

    @Override
    protected void initObservers() {
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        observeViewModel(viewModel);

        viewModel.getBanners().observe(getViewLifecycleOwner(), banners -> {
            if (banners != null && !banners.isEmpty()) {
                bannerCount = banners.size();
                BannerAdapter bannerAdapter = new BannerAdapter(banners);
                vpBanners.setAdapter(bannerAdapter);
                startBannerAutoScroll();
            }
        });

        viewModel.getCategories().observe(getViewLifecycleOwner(), categories -> {
            if (categories != null) {
                CategoryChipAdapter categoryAdapter = new CategoryChipAdapter(categories);
                rvCategories.setAdapter(categoryAdapter);
            }
        });

        viewModel.getFeaturedCourses().observe(getViewLifecycleOwner(), courses -> {
            if (courses != null) {
                CourseAdapter featuredAdapter = new CourseAdapter(courses);
                rvFeaturedCourses.setAdapter(featuredAdapter);
            }
        });

        viewModel.getContinueLearning().observe(getViewLifecycleOwner(), courses -> {
            if (courses != null) {
                ContinueLearningAdapter continueAdapter = new ContinueLearningAdapter(courses);
                rvContinueLearning.setAdapter(continueAdapter);
            }
        });

        viewModel.getPopularCourses().observe(getViewLifecycleOwner(), courses -> {
            if (courses != null) {
                CourseAdapter popularAdapter = new CourseAdapter(courses);
                rvPopularCourses.setAdapter(popularAdapter);
            }
        });
    }

    private void loadUserProfile() {
        User user = UserPreference.getInstance(requireContext()).getUserProfile();
        if (user != null) {
            String name = !TextUtils.isEmpty(user.getFullName()) ? user.getFullName() : "Học viên";
            tvStudentName.setText(name);

            if (!TextUtils.isEmpty(user.getAvatarUrl())) {
                Glide.with(this)
                        .load(user.getAvatarUrl())
                        .placeholder(R.drawable.ic_profile_placeholder)
                        .error(R.drawable.ic_profile_placeholder)
                        .into(ivAvatar);
            } else {
                ivAvatar.setImageResource(R.drawable.ic_profile_placeholder);
            }
        } else {
            tvStudentName.setText("Học viên");
            ivAvatar.setImageResource(R.drawable.ic_profile_placeholder);
        }
    }

    private void startBannerAutoScroll() {
        stopBannerAutoScroll();
        if (bannerCount <= 1) return;

        bannerRunnable = new Runnable() {
            @Override
            public void run() {
                if (vpBanners != null && bannerCount > 0) {
                    int currentItem = vpBanners.getCurrentItem();
                    int nextItem = (currentItem + 1) % bannerCount;
                    vpBanners.setCurrentItem(nextItem, true);
                    bannerHandler.postDelayed(this, 3000);
                }
            }
        };
        bannerHandler.postDelayed(bannerRunnable, 3000);
    }

    private void stopBannerAutoScroll() {
        if (bannerRunnable != null) {
            bannerHandler.removeCallbacks(bannerRunnable);
            bannerRunnable = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (bannerCount > 0) {
            startBannerAutoScroll();
        }
    }

    @Override
    public void onPause() {
        stopBannerAutoScroll();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        stopBannerAutoScroll();
        super.onDestroyView();
    }
}

