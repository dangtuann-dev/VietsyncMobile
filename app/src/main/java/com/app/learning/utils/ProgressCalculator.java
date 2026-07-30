package com.app.learning.utils;

import java.util.List;

public class ProgressCalculator {

    public static int calculateProgress(int completedLessons, int totalLessons) {
        if (totalLessons <= 0 || completedLessons <= 0) {
            return 0;
        }
        if (completedLessons >= totalLessons) {
            return 100;
        }
        return Math.round((float) completedLessons * 100 / totalLessons);
    }

    public static int calculateProgressFromList(List<Boolean> lessonCompletionStatuses) {
        if (lessonCompletionStatuses == null || lessonCompletionStatuses.isEmpty()) {
            return 0;
        }
        int completed = 0;
        for (Boolean status : lessonCompletionStatuses) {
            if (Boolean.TRUE.equals(status)) {
                completed++;
            }
        }
        return calculateProgress(completed, lessonCompletionStatuses.size());
    }
}
