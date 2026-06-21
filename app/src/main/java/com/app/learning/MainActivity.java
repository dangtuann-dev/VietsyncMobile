package com.app.learning;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.NavInflater;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.app.learning.ui.base.BaseActivity;
import com.app.learning.utils.RoleManager;
import com.example.vietsyncmobile.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * MainActivity is the primary navigation hub of the learning application.
 * It dynamically configures the navigation graph and bottom navigation menus based on user roles.
 */
public class MainActivity extends BaseActivity {

    private BottomNavigationView bottomNavigationView;
    private NavController navController;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViews() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // 1. Retrieve the user's role from Session via RoleManager
        RoleManager.Role role = RoleManager.getInstance(this).getCurrentRole();

        // 2. Dynamically inflate bottom navigation menu based on role
        if (role == RoleManager.Role.TEACHER) {
            bottomNavigationView.inflateMenu(R.menu.menu_teacher_nav);
        } else {
            bottomNavigationView.inflateMenu(R.menu.menu_student_nav);
        }

        // 3. Configure Jetpack NavController programmatically
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            NavInflater inflater = navController.getNavInflater();
            NavGraph graph = inflater.inflate(R.navigation.nav_graph);

            // Set different starting entry points depending on student vs teacher
            if (role == RoleManager.Role.TEACHER) {
                graph.setStartDestination(R.id.fragment_teacher_dashboard);
            } else {
                graph.setStartDestination(R.id.fragment_home);
            }
            
            navController.setGraph(graph);

            // 4. Bind BottomNavigationView to NavController
            NavigationUI.setupWithNavController(bottomNavigationView, navController);
        }
    }

    @Override
    protected void initObservers() {
        // No global observers needed for navigation host
    }

    @Override
    public void onBackPressed() {
        // Handle custom back navigation inside NavController destinations
        if (navController != null && navController.navigateUp()) {
            // Handled by NavController
        } else {
            super.onBackPressed();
        }
    }
}
