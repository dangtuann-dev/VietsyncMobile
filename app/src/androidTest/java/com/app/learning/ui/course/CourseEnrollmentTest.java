package com.app.learning.ui.course;

import android.content.Intent;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

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
public class CourseEnrollmentTest {

    static Intent createTargetIntent() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), CourseDetailActivity.class);
        intent.putExtra("course_id", "c101");
        return intent;
    }

    @Rule
    public ActivityScenarioRule<CourseDetailActivity> activityRule =
            new ActivityScenarioRule<>(createTargetIntent());

    @Test
    public void testCourseDetailAndEnrollFlow() {
        onView(withId(R.id.btn_enroll)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_enroll)).perform(click());
    }
}
