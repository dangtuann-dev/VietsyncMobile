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
public interface DownloadDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(DownloadEntity download);

    @Update
    void update(DownloadEntity download);

    @Delete
    void delete(DownloadEntity download);

    @Query("DELETE FROM downloads WHERE lessonId = :lessonId")
    void deleteByLessonId(String lessonId);

    @Query("SELECT * FROM downloads WHERE lessonId = :lessonId LIMIT 1")
    DownloadEntity getDownloadByLessonIdSync(String lessonId);

    @Query("SELECT * FROM downloads ORDER BY created_at DESC")
    LiveData<List<DownloadEntity>> getAllDownloads();

    @Query("SELECT * FROM downloads WHERE status = 'COMPLETED'")
    List<DownloadEntity> getCompletedDownloadsSync();
}
