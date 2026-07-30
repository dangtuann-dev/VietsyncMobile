package com.app.learning.utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.app.learning.MainActivity;
import com.app.learning.ui.course.CourseDetailActivity;
import com.app.learning.ui.exam.FinalExamActivity;
import com.app.learning.ui.gradebook.GradeBookActivity;

public class DeepLinkHandler {

    public static Intent createTargetIntent(Context context, Bundle extras) {
        if (extras == null) return new Intent(context, MainActivity.class);

        String targetScreen = extras.getString("target_screen", "home");
        String courseId = extras.getString("course_id", "");

        Intent intent;
        switch (targetScreen.toLowerCase()) {
            case "course_detail":
                intent = new Intent(context, CourseDetailActivity.class);
                intent.putExtra("course_id", courseId);
                break;
            case "final_exam":
                intent = new Intent(context, FinalExamActivity.class);
                intent.putExtra("course_id", courseId);
                break;
            case "gradebook":
                intent = new Intent(context, GradeBookActivity.class);
                break;
            default:
                intent = new Intent(context, MainActivity.class);
                break;
        }
        return intent;
    }
}
