package com.app.learning.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class PdfDownloadManager {

    public interface DownloadCallback {
        void onProgress(int percent);
        void onSuccess(File pdfFile);
        void onError(String error);
    }

    public static void downloadPdf(Context context, String pdfUrl, DownloadCallback callback) {
        String fileName = "pdf_" + Math.abs(pdfUrl.hashCode()) + ".pdf";
        File cacheDir = new File(context.getCacheDir(), "pdf_cache");
        if (!cacheDir.exists()) cacheDir.mkdirs();

        File localFile = new File(cacheDir, fileName);
        if (localFile.exists() && localFile.length() > 0) {
            callback.onSuccess(localFile);
            return;
        }

        AppExecutors.getInstance().networkIO().execute(() -> {
            try {
                URL url = new URL(pdfUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                int responseCode = connection.getResponseCode();
                if (responseCode != HttpURLConnection.HTTP_OK) {
                    new Handler(Looper.getMainLooper()).post(() -> callback.onError("Lỗi máy chủ HTTP: " + responseCode));
                    return;
                }

                int fileLength = connection.getContentLength();
                InputStream input = connection.getInputStream();
                FileOutputStream output = new FileOutputStream(localFile);

                byte[] data = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    if (fileLength > 0) {
                        int progress = (int) (total * 100 / fileLength);
                        new Handler(Looper.getMainLooper()).post(() -> callback.onProgress(progress));
                    }
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();

                new Handler(Looper.getMainLooper()).post(() -> callback.onSuccess(localFile));

            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(() -> callback.onError(e.getMessage()));
            }
        });
    }
}
