package com.app.learning.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface PdfBookmarkDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveBookmark(PdfBookmarkEntity bookmark);

    @Query("SELECT * FROM pdf_bookmarks WHERE pdfUrl = :pdfUrl LIMIT 1")
    PdfBookmarkEntity getBookmarkSync(String pdfUrl);
}
