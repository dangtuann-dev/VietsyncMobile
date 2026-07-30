package com.app.learning.ui.auth;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.vietsyncmobile.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class LoginFlowTest {

    @Rule
    public ActivityScenarioRule<LoginActivity> activityScenarioRule =
            new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void testLoginViewsDisplayed() {
        onView(withId(R.id.edt_email)).check(matches(isDisplayed()));
        onView(withId(R.id.edt_password)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_login)).check(matches(isDisplayed()));
    }

    @Test
    public void testLoginFailure_shortPassword() {
        onView(withId(R.id.edt_email)).perform(typeText("user@example.com"), closeSoftKeyboard());
        onView(withId(R.id.edt_password)).perform(typeText("123"), closeSoftKeyboard());
        onView(withId(R.id.btn_login)).perform(click());

        // Verify button is still displayed on error
        onView(withId(R.id.btn_login)).check(matches(isDisplayed()));
    }

    @Test
    public void testForgotPasswordNavigation() {
        onView(withId(R.id.tv_forgot_password)).perform(click());
    }

    @Test
    public void testRegisterNavigation() {
        onView(withId(R.id.tv_register)).perform(click());
    }
}
