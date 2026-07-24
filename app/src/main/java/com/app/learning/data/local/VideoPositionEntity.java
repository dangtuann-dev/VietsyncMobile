package com.app.learning.data.local;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "video_positions")
public class VideoPositionEntity {

    @PrimaryKey(autoGenerate = false)
    @androidx.annotation.NonNull
    private String lessonId;

    @ColumnInfo(name = "position_ms")
    private long positionMs;

    @ColumnInfo(name = "duration_ms")
    private long durationMs;

    @ColumnInfo(name = "updated_at")
    private long updatedAt;

    public VideoPositionEntity(@androidx.annotation.NonNull String lessonId, long positionMs, long durationMs, long updatedAt) {
        this.lessonId = lessonId;
        this.positionMs = positionMs;
        this.durationMs = durationMs;
        this.updatedAt = updatedAt;
    }

    @androidx.annotation.NonNull
    public String getLessonId() { return lessonId; }
    public void setLessonId(@androidx.annotation.NonNull String lessonId) { this.lessonId = lessonId; }

    public long getPositionMs() { return positionMs; }
    public void setPositionMs(long positionMs) { this.positionMs = positionMs; }

    public long getDurationMs() { return durationMs; }
    public void setDurationMs(long durationMs) { this.durationMs = durationMs; }

    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}
