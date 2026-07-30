package com.app.learning.ui.quiz;

import android.content.Intent;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.vietsyncmobile.R;
import com.app.learning.ui.exam.FinalExamActivity;

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
public class QuizFlowTest {

    static Intent createTargetIntent() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), FinalExamActivity.class);
        intent.putExtra("course_id", "course_101");
        return intent;
    }

    @Rule
    public ActivityScenarioRule<FinalExamActivity> activityScenarioRule =
            new ActivityScenarioRule<>(createTargetIntent());

    @Test
    public void testExamTimerAndNavigationDisplayed() {
        onView(withId(R.id.tvTimer)).check(matches(isDisplayed()));
        onView(withId(R.id.tvQuestionProgress)).check(matches(isDisplayed()));
        onView(withId(R.id.btnNext)).check(matches(isDisplayed()));
        onView(withId(R.id.btnSubmit)).check(matches(isDisplayed()));
    }

    @Test
    public void testClickNextQuestion() {
        onView(withId(R.id.btnNext)).perform(click());
    }
}
