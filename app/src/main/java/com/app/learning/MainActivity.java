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


        RoleManager.Role role = RoleManager.getInstance(this).getCurrentRole();


        if (role == RoleManager.Role.TEACHER) {
            bottomNavigationView.inflateMenu(R.menu.menu_teacher_nav);
        } else {
            bottomNavigationView.inflateMenu(R.menu.menu_student_nav);
        }


        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            navController = navHostFragment.getNavController();
            NavInflater inflater = navController.getNavInflater();
            NavGraph graph = inflater.inflate(R.navigation.nav_graph);


            if (role == RoleManager.Role.TEACHER) {
                graph.setStartDestination(R.id.fragment_teacher_dashboard);
            } else {
                graph.setStartDestination(R.id.fragment_home);
            }

            navController.setGraph(graph);


            NavigationUI.setupWithNavController(bottomNavigationView, navController);
        }
    }

    @Override
    protected void initObservers() {

    }

    @Override
    public void onBackPressed() {

        if (navController != null && navController.navigateUp()) {

        } else {
            super.onBackPressed();
        }
    }
}
