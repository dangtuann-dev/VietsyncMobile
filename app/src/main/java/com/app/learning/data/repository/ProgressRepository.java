package com.app.learning.data.repository;

import android.util.Log;
import com.app.learning.data.model.LessonProgressModel;

public class ProgressRepository {
    private static final String TAG = "ProgressRepository";

    // Simulate getting course progress. In a real app, you would query a database.
    public CourseProgress getCourseProgress(String courseId) {
        // Dummy data for illustration
        int totalLessons = 20;
        int completedLessons = 5;
        float percent = (float) completedLessons / totalLessons * 100;
        
        return new CourseProgress(percent, completedLessons, totalLessons);
    }

    public void markLessonComplete(String lessonId, long timeSpent) {
        Log.d(TAG, "Lesson " + lessonId + " marked as complete. Time spent: " + timeSpent + "ms");
        // Save to local database (Room/SQLite) or memory
    }

    public void updateProgressToSupabase() {
        Log.d(TAG, "Syncing progress to Supabase...");
        // Network call to update progress on Supabase
    }

    public static class CourseProgress {
        public float percent;
        public int completedLessons;
        public int totalLessons;

        public CourseProgress(float percent, int completedLessons, int totalLessons) {
            this.percent = percent;
            this.completedLessons = completedLessons;
            this.totalLessons = totalLessons;
        }
    }
}
