package com.app.learning.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.os.Build;
import android.os.Environment;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.webkit.WebView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PdfGenerator {

    public interface PdfCallback {
        void onSuccess(File pdfFile);
        void onError(String message);
    }

    public static void generatePdfFromWebView(Context context, WebView webView, String fileName, PdfCallback callback) {
        try {
            int width = webView.getWidth();
            int height = webView.getHeight();

            if (width <= 0 || height <= 0) {
                width = 1200;
                height = 800;
            }

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            webView.draw(canvas);

            PdfDocument document = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(width, height, 1).create();
            PdfDocument.Page page = document.startPage(pageInfo);

            page.getCanvas().drawBitmap(bitmap, 0, 0, null);
            document.finishPage(page);

            File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
            if (storageDir == null) {
                storageDir = context.getCacheDir();
            }
            if (!storageDir.exists()) {
                storageDir.mkdirs();
            }

            File pdfFile = new File(storageDir, fileName + ".pdf");
            FileOutputStream fos = new FileOutputStream(pdfFile);
            document.writeTo(fos);
            document.close();
            fos.close();

            callback.onSuccess(pdfFile);
        } catch (Exception e) {
            callback.onError("Lỗi tạo file PDF: " + e.getMessage());
        }
    }

    public static void printWebView(Context context, WebView webView, String jobName) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            PrintManager printManager = (PrintManager) context.getSystemService(Context.PRINT_SERVICE);
            PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter(jobName);
            if (printManager != null) {
                printManager.print(jobName, printAdapter, new PrintAttributes.Builder().build());
            }
        }
    }
}
