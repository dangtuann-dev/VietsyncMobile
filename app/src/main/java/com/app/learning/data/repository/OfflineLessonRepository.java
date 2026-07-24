package com.app.learning.data.repository;

import android.content.Context;

import com.app.learning.data.local.AppDatabase;
import com.app.learning.data.local.DownloadDao;
import com.app.learning.data.local.DownloadEntity;

import java.io.File;

public class OfflineLessonRepository {

    public interface LocalMediaCallback {
        void onResult(boolean isLocal, String pathOrUrl);
    }

    private final DownloadDao downloadDao;

    public OfflineLessonRepository(Context context) {
        this.downloadDao = AppDatabase.getInstance(context).downloadDao();
    }

    public void loadMediaSource(String lessonId, String remoteUrl, LocalMediaCallback callback) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            DownloadEntity entity = downloadDao.getDownloadByLessonIdSync(lessonId);
            if (entity != null && "COMPLETED".equals(entity.getStatus()) && entity.getLocalPath() != null) {
                File file = new File(entity.getLocalPath());
                if (file.exists() && file.length() > 0) {
                    AppExecutors.getInstance().mainThread().execute(() -> callback.onResult(true, entity.getLocalPath()));
                    return;
                }
            }
            AppExecutors.getInstance().mainThread().execute(() -> callback.onResult(false, remoteUrl));
        });
    }
}
