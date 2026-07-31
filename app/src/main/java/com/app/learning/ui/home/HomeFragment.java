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
    private TextView tvNotificationBadge;

    private HomeViewModel viewModel;
    private com.app.learning.ui.notification.NotificationViewModel notificationViewModel;
    private final Handler bannerHandler = new Handler(Looper.getMainLooper());
    private Runnable bannerRunnable;
    private int bannerCount = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_home;
    }

    private RecyclerView rvItCourses, rvBizCourses, rvDesignCourses, rvLanguageCourses;
    private View sectionIt, sectionBiz, sectionDesign, sectionLanguage;
    private androidx.core.widget.NestedScrollView nestedScrollView;

    @Override
    protected void initViews(View view) {
        nestedScrollView = view.findViewById(R.id.nestedScrollView);
        tvGreeting = view.findViewById(R.id.tv_greeting);
        tvStudentName = view.findViewById(R.id.tv_student_name);
        ivAvatar = view.findViewById(R.id.iv_avatar);
        etSearch = view.findViewById(R.id.et_search);
        vpBanners = view.findViewById(R.id.vp_banners);
        rvCategories = view.findViewById(R.id.rv_categories);
        rvFeaturedCourses = view.findViewById(R.id.rv_featured_courses);
        rvContinueLearning = view.findViewById(R.id.rv_continue_learning);
        tvNotificationBadge = view.findViewById(R.id.tv_notification_badge);

        rvItCourses = view.findViewById(R.id.rv_it_courses);
        rvBizCourses = view.findViewById(R.id.rv_biz_courses);
        rvDesignCourses = view.findViewById(R.id.rv_design_courses);
        rvLanguageCourses = view.findViewById(R.id.rv_language_courses);

        sectionIt = view.findViewById(R.id.section_it);
        sectionBiz = view.findViewById(R.id.section_biz);
        sectionDesign = view.findViewById(R.id.section_design);
        sectionLanguage = view.findViewById(R.id.section_language);

        rvCategories.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rvFeaturedCourses.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rvContinueLearning.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvContinueLearning.setNestedScrollingEnabled(false);

        if (rvItCourses != null) rvItCourses.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        if (rvBizCourses != null) rvBizCourses.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        if (rvDesignCourses != null) rvDesignCourses.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        if (rvLanguageCourses != null) rvLanguageCourses.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));

        loadUserProfile();

        View cvSearch = view.findViewById(R.id.cv_search);
        if (cvSearch != null) cvSearch.setOnClickListener(v -> openSearchActivity());
        if (etSearch != null) {
            etSearch.setFocusable(false);
            etSearch.setOnClickListener(v -> openSearchActivity());
        }

        View ivNotification = view.findViewById(R.id.iv_notification);
        if (ivNotification != null) {
            ivNotification.setOnClickListener(v -> {
                androidx.navigation.Navigation.findNavController(v).navigate(R.id.fragment_notification);
            });
        }

        View.OnClickListener openProfileListener = v -> {
            androidx.navigation.Navigation.findNavController(v).navigate(R.id.fragment_profile);
        };
        if (ivAvatar != null) ivAvatar.setOnClickListener(openProfileListener);
        if (tvStudentName != null) tvStudentName.setOnClickListener(openProfileListener);
    }

    @Override
    protected void initObservers() {
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        notificationViewModel = new ViewModelProvider(this).get(com.app.learning.ui.notification.NotificationViewModel.class);
        
        observeViewModel(viewModel);

        notificationViewModel.getNotificationsLiveData().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.data != null) {
                int unreadCount = 0;
                for (com.app.learning.data.model.NotificationModel notif : resource.data) {
                    if (!notif.isRead()) unreadCount++;
                }
                if (unreadCount > 0) {
                    tvNotificationBadge.setVisibility(View.VISIBLE);
                    tvNotificationBadge.setText(unreadCount > 99 ? "99+" : String.valueOf(unreadCount));
                } else {
                    tvNotificationBadge.setVisibility(View.GONE);
                }
            }
        });
        
        notificationViewModel.loadNotifications();

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
                CategoryChipAdapter categoryAdapter = new CategoryChipAdapter(categories, category -> {
                    View target = sectionIt;
                    if (category.getName().contains("Thiết kế")) target = sectionDesign;
                    else if (category.getName().contains("Ngoại ngữ")) target = sectionLanguage;
                    else if (category.getName().contains("Kinh doanh") || category.getName().contains("Marketing")) target = sectionBiz;

                    if (target != null && nestedScrollView != null) {
                        final View finalTarget = target;
                        nestedScrollView.post(() -> nestedScrollView.smoothScrollTo(0, finalTarget.getTop()));
                    }
                });
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

        viewModel.getItCourses().observe(getViewLifecycleOwner(), courses -> {
            if (courses != null && rvItCourses != null) {
                rvItCourses.setAdapter(new CourseAdapter(courses));
            }
        });

        viewModel.getBizCourses().observe(getViewLifecycleOwner(), courses -> {
            if (courses != null && rvBizCourses != null) {
                rvBizCourses.setAdapter(new CourseAdapter(courses));
            }
        });

        viewModel.getDesignCourses().observe(getViewLifecycleOwner(), courses -> {
            if (courses != null && rvDesignCourses != null) {
                rvDesignCourses.setAdapter(new CourseAdapter(courses));
            }
        });

        viewModel.getLanguageCourses().observe(getViewLifecycleOwner(), courses -> {
            if (courses != null && rvLanguageCourses != null) {
                rvLanguageCourses.setAdapter(new CourseAdapter(courses));
            }
        });
    }

    private void loadUserProfile() {
        User user = UserPreference.getInstance(requireContext()).getUserProfile();
        if (user != null) {
            String name = !TextUtils.isEmpty(user.getFullName()) ? user.getFullName() : getString(R.string.profile_role_student);
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
            tvStudentName.setText(getString(R.string.profile_role_student));
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

    private void openSearchActivity() {
        android.content.Intent intent = new android.content.Intent(requireActivity(), com.app.learning.ui.search.SearchActivity.class);
        startActivity(intent);
        requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}

