package com.app.learning.data.repository;

import android.content.Context;

import com.app.learning.data.local.AppDatabase;
import com.app.learning.data.local.NoteDao;
import com.app.learning.data.local.NoteEntity;
import com.app.learning.utils.AppExecutors;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class NoteRepository {

    public interface ExportCallback {
        void onSuccess(File exportedFile);
        void onError(String error);
    }

    private final NoteDao noteDao;
    private final Context context;

    public NoteRepository(Context context) {
        this.context = context.getApplicationContext();
        this.noteDao = AppDatabase.getInstance(context).noteDao();
    }

    public void addNote(NoteEntity note) {
        AppExecutors.getInstance().diskIO().execute(() -> noteDao.insert(note));
    }

    public void deleteNote(NoteEntity note) {
        AppExecutors.getInstance().diskIO().execute(() -> noteDao.delete(note));
    }

    public void exportNotesToText(String courseId, String courseTitle, ExportCallback callback) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            try {
                List<NoteEntity> notes = noteDao.getNotesByCourseSync(courseId);
                StringBuilder sb = new StringBuilder();
                sb.append("GHI CHÚ KHÓA HỌC: ").append(courseTitle).append("\n");
                sb.append("=========================================\n\n");

                for (NoteEntity note : notes) {
                    long minutes = note.getTimestampSeconds() / 60;
                    long seconds = note.getTimestampSeconds() % 60;
                    String timeStr = String.format("%02d:%02d", minutes, seconds);

                    sb.append("[").append(timeStr).append("] ").append(note.getLessonTitle() != null ? note.getLessonTitle() : "Bài học").append("\n");
                    sb.append("Nội dung: ").append(note.getNoteText()).append("\n");
                    sb.append("-----------------------------------------\n");
                }

                File exportDir = context.getExternalFilesDir(null);
                if (exportDir == null) exportDir = context.getCacheDir();

                File textFile = new File(exportDir, "Ghi_chu_" + courseId.substring(0, 8) + ".txt");
                FileOutputStream fos = new FileOutputStream(textFile);
                OutputStreamWriter osw = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
                osw.write(sb.toString());
                osw.close();
                fos.close();

                AppExecutors.getInstance().mainThread().execute(() -> callback.onSuccess(textFile));

            } catch (Exception e) {
                AppExecutors.getInstance().mainThread().execute(() -> callback.onError(e.getMessage()));
            }
        });
    }
}
