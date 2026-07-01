package com.app.learning.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface SearchHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(SearchHistory searchHistory);

    @Query("SELECT * FROM search_history ORDER BY timestamp DESC")
    LiveData<List<SearchHistory>> getAllHistory();

    @Query("SELECT * FROM search_history ORDER BY timestamp DESC LIMIT :limit")
    LiveData<List<SearchHistory>> getHistoryLimit(int limit);

    @Delete
    void delete(SearchHistory searchHistory);

    @Query("DELETE FROM search_history")
    void clearAll();
}
