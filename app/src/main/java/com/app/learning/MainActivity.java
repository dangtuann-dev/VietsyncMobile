package com.app.learning;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.app.learning.ui.base.BaseActivity;
import com.app.learning.utils.RoleManager;
import com.example.vietsyncmobile.R;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends BaseActivity {

    private BottomNavigationView bottomNavigationView;
    private NavController navController;
    private boolean isBottomNavNeededForDestination = true;
    private boolean isKeyboardVisible = false;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            NavInflater inflater = navController.getNavInflater();
            NavGraph graph = inflater.inflate(R.navigation.nav_graph);

            RoleManager.Role role = RoleManager.getInstance(this).getCurrentRole();
            if (role == RoleManager.Role.TEACHER) {
                graph.setStartDestination(R.id.fragment_teacher_dashboard);
            } else {
                graph.setStartDestination(R.id.fragment_home);
            }

            navController.setGraph(graph);

            NavigationUI.setupWithNavController(bottomNavigationView, navController);
        }

        setupKeyboardListener();
        setupDestinationListener();

        setNotificationBadge(3);
    }

    @Override
    protected void initObservers() {
    }

    public void setNotificationBadge(int count) {
        if (bottomNavigationView != null) {
            BadgeDrawable badge = bottomNavigationView.getOrCreateBadge(R.id.fragment_my_learning);
            if (count > 0) {
                badge.setVisible(true);
                badge.setNumber(count);
                badge.setBackgroundColor(ContextCompat.getColor(this, R.color.primary));
                badge.setBadgeTextColor(ContextCompat.getColor(this, R.color.on_primary));
            } else {
                badge.setVisible(false);
                bottomNavigationView.removeBadge(R.id.fragment_my_learning);
            }
        }
    }

    private void setupKeyboardListener() {
        final View rootView = findViewById(android.R.id.content);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            private boolean wasKeyboardVisible = false;

            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);
                int screenHeight = rootView.getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom;

                boolean visible = keypadHeight > screenHeight * 0.15;
                if (visible != wasKeyboardVisible) {
                    wasKeyboardVisible = visible;
                    isKeyboardVisible = visible;
                    updateBottomNavVisibility();
                }
            }
        });
    }

    private void setupDestinationListener() {
        if (navController != null) {
            navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
                int id = destination.getId();
                if (id == R.id.fragment_lesson_detail) {
                    isBottomNavNeededForDestination = false;
                } else {
                    isBottomNavNeededForDestination = true;
                }
                updateBottomNavVisibility();
            });
        }
    }

    private void updateBottomNavVisibility() {
        if (bottomNavigationView == null) return;
        int targetVisibility = (isBottomNavNeededForDestination && !isKeyboardVisible) ? View.VISIBLE : View.GONE;
        setBottomNavigationVisibility(targetVisibility);
    }

    private void setBottomNavigationVisibility(int visibility) {
        if (bottomNavigationView != null && bottomNavigationView.getVisibility() != visibility) {
            bottomNavigationView.setVisibility(visibility);
            View navHostView = findViewById(R.id.nav_host_fragment);
            if (navHostView != null) {
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) navHostView.getLayoutParams();
                int marginDp = (visibility == View.VISIBLE) ? 56 : 0;
                params.bottomMargin = Math.round(marginDp * getResources().getDisplayMetrics().density);
                navHostView.setLayoutParams(params);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (navController != null) {
            if (navController.navigateUp()) {
                return;
            }
        }
        super.onBackPressed();
    }
}
