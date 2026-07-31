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
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.core.view.GravityCompat;
import com.google.android.material.navigation.NavigationView;
import android.content.Intent;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import com.app.learning.ui.profile.DownloadHistoryActivity;
import com.app.learning.ui.profile.MyCertificatesActivity;
import com.app.learning.ui.base.BaseActivity;
import com.app.learning.utils.RoleManager;
import com.example.vietsyncmobile.R;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.activity.OnBackPressedCallback;

public class MainActivity extends BaseActivity {

    private BottomNavigationView bottomNavigationView;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private NavController navController;
    private boolean isBottomNavNeededForDestination = true;
    private boolean isKeyboardVisible = false;
    private com.app.learning.ui.wishlist.WishlistViewModel wishlistViewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            NavInflater inflater = navController.getNavInflater();
            NavGraph graph = inflater.inflate(R.navigation.nav_graph);

            RoleManager.Role role = RoleManager.getInstance(this).getCurrentRole();
            android.view.Menu menu = bottomNavigationView.getMenu();
            if (role == RoleManager.Role.TEACHER) {
                graph.setStartDestination(R.id.fragment_home);
                menu.findItem(R.id.fragment_teacher_dashboard).setVisible(true);
                bottomNavigationView.setSelectedItemId(R.id.fragment_home);
            } else {
                graph.setStartDestination(R.id.fragment_home);
                menu.findItem(R.id.fragment_teacher_dashboard).setVisible(false);
                bottomNavigationView.setSelectedItemId(R.id.fragment_home);
            }

            navController.setGraph(graph);

            bottomNavigationView.setOnItemSelectedListener(item -> {
                int itemId = item.getItemId();
                int currentId = navController.getCurrentDestination() != null ? navController.getCurrentDestination().getId() : 0;

                if (itemId == currentId) {
                    return true;
                }

                androidx.navigation.NavOptions navOptions = new androidx.navigation.NavOptions.Builder()
                        .setLaunchSingleTop(true)
                        .setPopUpTo(R.id.fragment_home, itemId == R.id.fragment_home)
                        .setEnterAnim(android.R.anim.fade_in)
                        .setExitAnim(android.R.anim.fade_out)
                        .build();

                try {
                    navController.navigate(itemId, null, navOptions);
                    return true;
                } catch (Exception e) {
                    return false;
                }
            });
        }

        setupKeyboardListener();
        setupDestinationListener();

        setNotificationBadge(3);
        setupDrawerMenu();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                    return;
                }
                if (navController != null) {
                    if (navController.navigateUp()) {
                        return;
                    }
                }
                setEnabled(false);
                getOnBackPressedDispatcher().onBackPressed();
                setEnabled(true);
            }
        });
    }
    
    private void setupDrawerMenu() {
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_my_courses) {
                    navController.navigate(R.id.fragment_my_courses);
                } else if (id == R.id.nav_wishlist) {
                    navController.navigate(R.id.fragment_wishlist);
                } else if (id == R.id.nav_downloads) {
                    startActivity(new Intent(this, DownloadHistoryActivity.class));
                } else if (id == R.id.nav_certificates) {
                    startActivity(new Intent(this, MyCertificatesActivity.class));
                } else if (id == R.id.nav_help_support) {
                    Toast.makeText(this, "Help & Support - Coming Soon", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.nav_rate_app) {
                    Toast.makeText(this, "Rate App - Coming Soon", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.nav_settings) {
                    navController.navigate(R.id.fragment_settings);
                } else if (id == R.id.nav_logout) {
                    Toast.makeText(this, "Logout clicked", Toast.LENGTH_SHORT).show();
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            });
        }
    }

    @Override
    protected void initObservers() {
        wishlistViewModel = new ViewModelProvider(this).get(com.app.learning.ui.wishlist.WishlistViewModel.class);
        wishlistViewModel.getWishlistLiveData().observe(this, resource -> {
            if (resource != null && resource.status == com.app.learning.data.api.Resource.Status.SUCCESS && resource.data != null) {
                setWishlistBadge(resource.data.size());
            }
        });
        wishlistViewModel.loadWishlists();
    }

    public void setWishlistBadge(int count) {
        if (navigationView != null) {
            View actionView = navigationView.getMenu().findItem(R.id.nav_wishlist).getActionView();
            if (actionView instanceof android.widget.TextView) {
                android.widget.TextView badge = (android.widget.TextView) actionView;
                if (count > 0) {
                    badge.setVisibility(View.VISIBLE);
                    badge.setText(String.valueOf(count));
                } else {
                    badge.setVisibility(View.GONE);
                }
            }
        }
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
                int marginDp = (visibility == View.VISIBLE) ? 80 : 0;
                params.bottomMargin = Math.round(marginDp * getResources().getDisplayMetrics().density);
                navHostView.setLayoutParams(params);
            }
        }
    }

}
