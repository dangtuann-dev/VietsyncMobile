package com.app.learning.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(NoteEntity note);

    @Update
    void update(NoteEntity note);

    @Delete
    void delete(NoteEntity note);

    @Query("SELECT * FROM lesson_notes WHERE lesson_id = :lessonId ORDER BY timestamp_seconds ASC")
    LiveData<List<NoteEntity>> getNotesByLesson(String lessonId);

    @Query("SELECT * FROM lesson_notes WHERE course_id = :courseId ORDER BY created_at DESC")
    LiveData<List<NoteEntity>> getNotesByCourse(String courseId);

    @Query("SELECT * FROM lesson_notes WHERE course_id = :courseId ORDER BY created_at DESC")
    List<NoteEntity> getNotesByCourseSync(String courseId);

    @Query("SELECT * FROM lesson_notes WHERE is_synced = 0")
    List<NoteEntity> getUnsyncedNotes();
}
