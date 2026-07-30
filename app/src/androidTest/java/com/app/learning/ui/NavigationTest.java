package com.app.learning.ui;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.app.learning.MainActivity;
import com.example.vietsyncmobile.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class NavigationTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule =
            new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testBottomNavigationDisplayed() {
        onView(withId(R.id.bottom_navigation)).check(matches(isDisplayed()));
    }

    @Test
    public void testSwitchBottomNavTabs() {
        onView(withId(R.id.bottom_navigation)).check(matches(isDisplayed()));
        // Select tab navigation
        onView(withId(R.id.nav_explore)).perform(click());
        onView(withId(R.id.nav_my_courses)).perform(click());
        onView(withId(R.id.nav_profile)).perform(click());
        onView(withId(R.id.nav_home)).perform(click());
    }
}
