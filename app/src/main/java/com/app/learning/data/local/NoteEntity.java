package com.app.learning.data.local;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "lesson_notes")
public class NoteEntity {

    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "supabase_id")
    private String supabaseId;

    @ColumnInfo(name = "course_id")
    private String courseId;

    @ColumnInfo(name = "lesson_id")
    private String lessonId;

    @ColumnInfo(name = "lesson_title")
    private String lessonTitle;

    @ColumnInfo(name = "note_text")
    private String noteText;

    @ColumnInfo(name = "timestamp_seconds")
    private long timestampSeconds;

    @ColumnInfo(name = "color")
    private String color; // e.g. #FFEB3B (yellow), #81C784 (green), #64B5F6 (blue), #F48FB1 (pink)

    @ColumnInfo(name = "created_at")
    private long createdAt;

    @ColumnInfo(name = "is_synced")
    private boolean isSynced;

    public NoteEntity(String courseId, String lessonId, String lessonTitle, String noteText, long timestampSeconds, String color, long createdAt) {
        this.courseId = courseId;
        this.lessonId = lessonId;
        this.lessonTitle = lessonTitle;
        this.noteText = noteText;
        this.timestampSeconds = timestampSeconds;
        this.color = color;
        this.createdAt = createdAt;
        this.isSynced = false;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getSupabaseId() { return supabaseId; }
    public void setSupabaseId(String supabaseId) { this.supabaseId = supabaseId; }

    public String getCourseId() { return courseId; }
    public void setCourseId(String courseId) { this.courseId = courseId; }

    public String getLessonId() { return lessonId; }
    public void setLessonId(String lessonId) { this.lessonId = lessonId; }

    public String getLessonTitle() { return lessonTitle; }
    public void setLessonTitle(String lessonTitle) { this.lessonTitle = lessonTitle; }

    public String getNoteText() { return noteText; }
    public void setNoteText(String noteText) { this.noteText = noteText; }

    public long getTimestampSeconds() { return timestampSeconds; }
    public void setTimestampSeconds(long timestampSeconds) { this.timestampSeconds = timestampSeconds; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    public boolean isSynced() { return isSynced; }
    public void setSynced(boolean synced) { isSynced = synced; }
}
