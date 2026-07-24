package com.app.learning.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface VideoPositionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void savePosition(VideoPositionEntity positionEntity);

    @Query("SELECT * FROM video_positions WHERE lessonId = :lessonId LIMIT 1")
    VideoPositionEntity getPositionSync(String lessonId);
}
