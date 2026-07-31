package com.app.learning.ui.course;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.app.learning.data.api.Resource;
import com.app.learning.data.model.Course;
import com.app.learning.ui.base.BaseActivity;
import com.app.learning.ui.wishlist.WishlistViewModel;
import com.app.learning.utils.UserPreference;
import com.bumptech.glide.Glide;
import com.example.vietsyncmobile.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class CourseDetailActivity extends BaseActivity {

    private ImageView ivThumbnail;
    private ImageView ivPlayPreview;
    private TextView tvLevel;
    private TextView tvTitle;
    private TextView tvInstructor;
    private TextView tvRating;
    private TextView tvEnrolledCount;
    private TextView tvPrice;
    private MaterialButton btnEnroll;
    private FloatingActionButton fabShare;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    private Course course;
    private String courseId;
    private String userId;
    private boolean isWishlisted = false;
    private boolean isEnrolled = false;
    private MenuItem wishlistMenuItem;

    private WishlistViewModel wishlistViewModel;
    private CourseDetailViewModel courseDetailViewModel;
    private UserPreference userPreference;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_course_detail;
    }

    @Override
    protected void initViews() {
        ivThumbnail = findViewById(R.id.iv_thumbnail);
        ivPlayPreview = findViewById(R.id.iv_play_preview);
        tvLevel = findViewById(R.id.tv_level);
        tvTitle = findViewById(R.id.tv_title);
        tvInstructor = findViewById(R.id.tv_instructor);
        tvRating = findViewById(R.id.tv_rating);
        tvEnrolledCount = findViewById(R.id.tv_enrolled_count);
        tvPrice = findViewById(R.id.tv_price);
        btnEnroll = findViewById(R.id.btn_enroll);
        fabShare = findViewById(R.id.fab_share);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        course = (Course) getIntent().getSerializableExtra("course");
        courseId = getIntent().getStringExtra("course_id");

        if (course != null) {
            courseId = course.getId();
            displayCourseHeader(course);
        }

        userPreference = UserPreference.getInstance(this);
        userId = (userPreference.getUserProfile() != null) ? userPreference.getUserProfile().getId() : null;

        // ViewPager & Tabs configuration
        viewPager.setAdapter(new CourseDetailPagerAdapter(this));
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Tổng quan");
                    break;
                case 1:
                    tab.setText("Nội dung");
                    break;
                case 2:
                    tab.setText("Giảng viên");
                    break;
                case 3:
                    tab.setText("Đánh giá");
                    break;
            }
        }).attach();

        // Share functionality
        fabShare.setOnClickListener(v -> shareCourse());

        // Enroll functionality
        btnEnroll.setOnClickListener(v -> {
            if (isEnrolled) {
                Toast.makeText(this, "Bắt đầu bài học!", Toast.LENGTH_SHORT).show();
                viewPager.setCurrentItem(1, true);
            } else {
                if (course != null) {
                    EnrollmentBottomSheet.newInstance(course).show(getSupportFragmentManager(), "enrollment_sheet");
                }
            }
        });

        // Video Preview Play overlay click
        ivPlayPreview.setOnClickListener(v -> {
            if (course != null) {
                Toast.makeText(this, "Đang phát video giới thiệu khóa học...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void initObservers() {
        wishlistViewModel = new ViewModelProvider(this).get(WishlistViewModel.class);
        courseDetailViewModel = new ViewModelProvider(this).get(CourseDetailViewModel.class);

        // Wishlist observations
        wishlistViewModel.getIsWishlistedLiveData().observe(this, resource -> {
            if (resource != null && resource.status == Resource.Status.SUCCESS && resource.data != null) {
                isWishlisted = resource.data;
                updateWishlistMenuIcon();
            }
        });

        wishlistViewModel.getActionResultLiveData().observe(this, resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case LOADING:
                        showLoading();
                        break;
                    case SUCCESS:
                        hideLoading();
                        Toast.makeText(this, "Cập nhật yêu thích thành công!", Toast.LENGTH_SHORT).show();
                        break;
                    case ERROR:
                        hideLoading();
                        showError(resource.error != null ? resource.error.getMessage() : "Đã xảy ra lỗi");
                        break;
                }
            }
        });

        // Course Detail observations
        courseDetailViewModel.getCourseDetail().observe(this, resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case SUCCESS:
                        if (resource.data != null) {
                            course = resource.data;
                            displayCourseHeader(course);
                        }
                        break;
                    case ERROR:
                        showError(resource.error != null ? resource.error.getMessage() : "Lỗi tải chi tiết khóa học");
                        break;
                }
            }
        });

        // Enrollment observations
        courseDetailViewModel.getEnrollmentStatus().observe(this, resource -> {
            if (resource != null && resource.status == Resource.Status.SUCCESS && resource.data != null) {
                isEnrolled = resource.data;
                updateEnrollmentButton();
            }
        });

        courseDetailViewModel.getEnrollResult().observe(this, resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case LOADING:
                        showLoading("Đang đăng ký...");
                        break;
                    case SUCCESS:
                        hideLoading();
                        Toast.makeText(this, "Đăng ký khóa học thành công!", Toast.LENGTH_SHORT).show();
                        isEnrolled = true;
                        updateEnrollmentButton();
                        break;
                    case ERROR:
                        hideLoading();
                        showError(resource.error != null ? resource.error.getMessage() : "Đăng ký thất bại");
                        break;
                }
            }
        });

        // Trigger loading
        if (courseId != null) {
            wishlistViewModel.checkWishlistStatus(courseId);
            courseDetailViewModel.init(courseId, userId);
        }
    }

    private void displayCourseHeader(Course course) {
        tvTitle.setText(course.getTitle());
        
        String lvl = "Cơ bản";
        if ("intermediate".equalsIgnoreCase(course.getLevel())) lvl = "Trung cấp";
        else if ("advanced".equalsIgnoreCase(course.getLevel())) lvl = "Nâng cao";
        tvLevel.setText(lvl);
        
        String instructorName = (course.getInstructor() != null && course.getInstructor().getFullName() != null)
                ? course.getInstructor().getFullName()
                : "Giảng viên Vietsync";
        tvInstructor.setText(getString(R.string.course_instructor, instructorName));

        double rating = course.getRating() > 0 ? course.getRating() : 4.8;
        tvRating.setText(String.format("%.1f", rating));
        tvEnrolledCount.setText(course.getEnrolledCount() + " học viên");

        tvPrice.setText(course.getPrice() == 0
                ? "Miễn phí"
                : String.format("%,.0fđ", course.getPrice()));

        Glide.with(this)
                .load(course.getThumbnail())
                .placeholder(R.drawable.ic_logo_placeholder)
                .error(R.drawable.ic_logo_placeholder)
                .into(ivThumbnail);
    }

    private void updateEnrollmentButton() {
        if (isEnrolled) {
            btnEnroll.setText("Continue Learning");
            btnEnroll.setBackgroundColor(getResources().getColor(R.color.category_green));
        } else {
            btnEnroll.setText("Đăng ký học");
            btnEnroll.setBackgroundColor(getResources().getColor(R.color.primary));
        }
    }

    private void shareCourse() {
        if (course == null) return;
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        String shareBody = "Học cùng tôi khóa học tuyệt vời này trên Vietsync Learning: " 
                + course.getTitle() + "\nLink: https://vietsync.learning/courses/" + course.getId();
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, course.getTitle());
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(shareIntent, "Chia sẻ khóa học qua"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_course_detail, menu);
        wishlistMenuItem = menu.findItem(R.id.action_wishlist);
        updateWishlistMenuIcon();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_wishlist) {
            toggleWishlist();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void toggleWishlist() {
        if (courseId == null || userId == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để lưu khóa học!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (isWishlisted) {
            wishlistViewModel.removeFromWishlist(courseId);
        } else {
            wishlistViewModel.addToWishlist(courseId);
        }
    }

    private void updateWishlistMenuIcon() {
        if (wishlistMenuItem != null) {
            wishlistMenuItem.setIcon(isWishlisted ? R.drawable.ic_heart_filled : R.drawable.ic_heart_border);
        }
    }

    private static class CourseDetailPagerAdapter extends FragmentStateAdapter {
        public CourseDetailPagerAdapter(@NonNull CourseDetailActivity activity) {
            super(activity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position) {
                case 0:
                    return new OverviewFragment();
                case 1:
                    return new CurriculumFragment();
                case 2:
                    return new InstructorFragment();
                case 3:
                    return new ReviewsFragment();
                default:
                    return new OverviewFragment();
            }
        }

        @Override
        public int getItemCount() {
            return 4;
        }
    }
}
