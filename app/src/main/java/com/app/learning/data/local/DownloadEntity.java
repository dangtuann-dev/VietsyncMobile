package com.app.learning.data.local;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "downloads")
public class DownloadEntity {

    @PrimaryKey(autoGenerate = false)
    @androidx.annotation.NonNull
    private String lessonId;

    @ColumnInfo(name = "course_id")
    private String courseId;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "download_url")
    private String downloadUrl;

    @ColumnInfo(name = "local_path")
    private String localPath;

    @ColumnInfo(name = "status")
    private String status; // QUEUED, DOWNLOADING, PAUSED, COMPLETED, FAILED

    @ColumnInfo(name = "progress")
    private int progress; // 0 to 100

    @ColumnInfo(name = "total_bytes")
    private long totalBytes;

    @ColumnInfo(name = "downloaded_bytes")
    private long downloadedBytes;

    @ColumnInfo(name = "created_at")
    private long createdAt;

    public DownloadEntity(@androidx.annotation.NonNull String lessonId, String courseId, String title, String downloadUrl, String localPath, String status, int progress, long totalBytes, long downloadedBytes, long createdAt) {
        this.lessonId = lessonId;
        this.courseId = courseId;
        this.title = title;
        this.downloadUrl = downloadUrl;
        this.localPath = localPath;
        this.status = status;
        this.progress = progress;
        this.totalBytes = totalBytes;
        this.downloadedBytes = downloadedBytes;
        this.createdAt = createdAt;
    }

    @androidx.annotation.NonNull
    public String getLessonId() { return lessonId; }
    public void setLessonId(@androidx.annotation.NonNull String lessonId) { this.lessonId = lessonId; }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDownloadUrl() { return downloadUrl; }
    public void setDownloadUrl(String downloadUrl) { this.downloadUrl = downloadUrl; }

    public String getLocalPath() { return localPath; }
    public void setLocalPath(String localPath) { this.localPath = localPath; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }

    public long getTotalBytes() { return totalBytes; }
    public void setTotalBytes(long totalBytes) { this.totalBytes = totalBytes; }

    public long getDownloadedBytes() { return downloadedBytes; }
    public void setDownloadedBytes(long downloadedBytes) { this.downloadedBytes = downloadedBytes; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
