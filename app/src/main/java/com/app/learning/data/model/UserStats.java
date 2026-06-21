package com.app.learning.data.model;

import java.io.Serializable;

/**
 * UserStats stores statistics for course enrollments, completions, and certificates.
 */
public class UserStats implements Serializable {
    private final int enrolledCount;
    private final int completedCount;
    private final int certificatesCount;

    public UserStats(int enrolledCount, int completedCount, int certificatesCount) {
        this.enrolledCount = enrolledCount;
        this.completedCount = completedCount;
        this.certificatesCount = certificatesCount;
    }

    public int getEnrolledCount() {
        return enrolledCount;
    }

    public int getCompletedCount() {
        return completedCount;
    }

    public int getCertificatesCount() {
        return certificatesCount;
    }
}
