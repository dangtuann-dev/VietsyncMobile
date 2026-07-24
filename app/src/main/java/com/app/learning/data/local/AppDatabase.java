package com.app.learning.data.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(
    entities = {
        SearchHistory.class,
        NoteEntity.class,
        DownloadEntity.class,
        PdfBookmarkEntity.class,
        VideoPositionEntity.class
    },
    version = 2,
    exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase instance;

    public abstract SearchHistoryDao searchHistoryDao();
    public abstract NoteDao noteDao();
    public abstract DownloadDao downloadDao();
    public abstract PdfBookmarkDao pdfBookmarkDao();
    public abstract VideoPositionDao videoPositionDao();

    public static AppDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (AppDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "learning_app_database"
                    )
                    .fallbackToDestructiveMigration()
                    .build();
                }
            }
        }
        return instance;
    }
}
