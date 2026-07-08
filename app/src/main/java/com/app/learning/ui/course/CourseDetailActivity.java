package com.app.learning.ui.course;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import com.app.learning.data.api.Resource;
import com.app.learning.data.model.Course;
import com.app.learning.data.repository.CourseRepository;
import com.app.learning.ui.base.BaseActivity;
import com.app.learning.ui.wishlist.WishlistViewModel;
import com.bumptech.glide.Glide;
import com.example.vietsyncmobile.R;
import com.google.android.material.button.MaterialButton;

public class CourseDetailActivity extends BaseActivity {

    private ImageView ivThumbnail;
    private TextView tvLevel;
    private TextView tvTitle;
    private TextView tvInstructor;
    private TextView tvPrice;
    private TextView tvDescription;
    private MaterialButton btnEnroll;

    private Course course;
    private String courseId;
    private boolean isWishlisted = false;
    private MenuItem wishlistMenuItem;

    private WishlistViewModel wishlistViewModel;
    private CourseRepository courseRepository;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_course_detail;
    }

    @Override
    protected void initViews() {
        ivThumbnail = findViewById(R.id.iv_thumbnail);
        tvLevel = findViewById(R.id.tv_level);
        tvTitle = findViewById(R.id.tv_title);
        tvInstructor = findViewById(R.id.tv_instructor);
        tvPrice = findViewById(R.id.tv_price);
        tvDescription = findViewById(R.id.tv_description);
        btnEnroll = findViewById(R.id.btn_enroll);

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
            displayCourseDetails(course);
        }

        courseRepository = new CourseRepository(this);

        btnEnroll.setOnClickListener(v -> {
            if (courseId != null) {
                wishlistViewModel.enrollFromWishlist(courseId);
            }
        });
    }

    @Override
    protected void initObservers() {
        wishlistViewModel = new ViewModelProvider(this).get(WishlistViewModel.class);

        wishlistViewModel.getIsWishlistedLiveData().observe(this, resource -> {
            if (resource != null) {
                if (resource.status == Resource.Status.SUCCESS && resource.data != null) {
                    isWishlisted = resource.data;
                    updateWishlistMenuIcon();
                }
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
                        Toast.makeText(this, "Thao tác thành công!", Toast.LENGTH_SHORT).show();
                        break;
                    case ERROR:
                        hideLoading();
                        showError(resource.error != null ? resource.error.getMessage() : "Đã xảy ra lỗi");
                        break;
                }
            }
        });

        if (courseId != null) {
            wishlistViewModel.checkWishlistStatus(courseId);
            if (course == null) {
                loadCourseDetails(courseId);
            }
        }
    }

    private void loadCourseDetails(String id) {
        showLoading();
        courseRepository.getCourseById(id).observe(this, resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case SUCCESS:
                        hideLoading();
                        if (resource.data != null) {
                            course = resource.data;
                            displayCourseDetails(course);
                        }
                        break;
                    case ERROR:
                        hideLoading();
                        showError(resource.error != null ? resource.error.getMessage() : "Lỗi tải chi tiết khóa học");
                        break;
                }
            }
        });
    }

    private void displayCourseDetails(Course course) {
        tvTitle.setText(course.getTitle());
        tvLevel.setText(course.getLevel() != null ? course.getLevel() : "Cơ bản");
        tvDescription.setText(course.getDescription() != null ? course.getDescription() : "Chưa có mô tả.");
        
        String instructorName = (course.getInstructor() != null && course.getInstructor().getFullName() != null)
                ? course.getInstructor().getFullName()
                : "Giảng viên";
        tvInstructor.setText(getString(R.string.course_instructor, instructorName));

        tvPrice.setText(course.getPrice() == 0
                ? "Miễn phí"
                : String.format("%,.0fđ", course.getPrice()));

        Glide.with(this)
                .load(course.getThumbnail())
                .placeholder(R.drawable.ic_logo_placeholder)
                .error(R.drawable.ic_logo_placeholder)
                .into(ivThumbnail);
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
        if (courseId == null) return;
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
}
