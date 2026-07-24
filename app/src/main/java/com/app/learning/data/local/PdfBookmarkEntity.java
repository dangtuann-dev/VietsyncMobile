package com.app.learning.data.local;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "pdf_bookmarks")
public class PdfBookmarkEntity {

    @PrimaryKey(autoGenerate = false)
    @androidx.annotation.NonNull
    private String pdfUrl;

    @ColumnInfo(name = "last_page")
    private int lastPage;

    @ColumnInfo(name = "total_pages")
    private int totalPages;

    @ColumnInfo(name = "updated_at")
    private long updatedAt;

    public PdfBookmarkEntity(@androidx.annotation.NonNull String pdfUrl, int lastPage, int totalPages, long updatedAt) {
        this.pdfUrl = pdfUrl;
        this.lastPage = lastPage;
        this.totalPages = totalPages;
        this.updatedAt = updatedAt;
    }

    @androidx.annotation.NonNull
    public String getPdfUrl() { return pdfUrl; }
    public void setPdfUrl(@androidx.annotation.NonNull String pdfUrl) { this.pdfUrl = pdfUrl; }

    public int getLastPage() { return lastPage; }
    public void setLastPage(int lastPage) { this.lastPage = lastPage; }

    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }

    public long getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(long updatedAt) { this.updatedAt = updatedAt; }
}
