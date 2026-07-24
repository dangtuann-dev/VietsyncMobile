package com.app.learning.utils;

import android.content.Context;

import com.app.learning.data.local.AppDatabase;
import com.app.learning.data.local.PdfBookmarkDao;
import com.app.learning.data.local.PdfBookmarkEntity;

public class PdfBookmarkManager {

    public interface BookmarkCallback {
        void onBookmarkLoaded(int page);
    }

    private final PdfBookmarkDao dao;

    public PdfBookmarkManager(Context context) {
        this.dao = AppDatabase.getInstance(context).pdfBookmarkDao();
    }

    public void saveBookmark(String pdfUrl, int currentPage, int totalPages) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            dao.saveBookmark(new PdfBookmarkEntity(pdfUrl, currentPage, totalPages, System.currentTimeMillis()));
        });
    }

    public void getBookmark(String pdfUrl, BookmarkCallback callback) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            PdfBookmarkEntity entity = dao.getBookmarkSync(pdfUrl);
            int page = entity != null ? entity.getLastPage() : 1;
            AppExecutors.getInstance().mainThread().execute(() -> callback.onBookmarkLoaded(page));
        });
    }
}
