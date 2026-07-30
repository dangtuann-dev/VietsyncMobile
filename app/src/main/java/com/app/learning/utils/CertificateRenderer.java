package com.app.learning.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class CertificateRenderer {

    public interface RenderCallback {
        void onSuccess(File pdfFile);
        void onError(String error);
    }

    public static void renderCertificate(Context context, String userName, String courseTitle, String date, String instructorName, int hours, String certificateId, File outputFile, RenderCallback callback) {
        new Handler(Looper.getMainLooper()).post(() -> {
            try {
                // Generate QR Code bitmap
                String verifyUrl = "vietsync://verify-certificate?id=" + certificateId;
                Bitmap qrBitmap = QRCodeGenerator.generateQRCode(verifyUrl, 200, 200);
                if (qrBitmap == null) {
                    callback.onError("Failed to generate QR Code");
                    return;
                }

                // Convert QR Code bitmap to base64
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                qrBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                String qrBase64 = Base64.encodeToString(byteArray, Base64.DEFAULT);

                // Create WebView
                WebView webView = new WebView(context);
                webView.getSettings().setJavaScriptEnabled(true);
                webView.getSettings().setUseWideViewPort(true);
                webView.getSettings().setLoadWithOverviewMode(true);

                // Set layout parameters for landscape Certificate dimensions (800x560 approx)
                int width = 1200;
                int height = 840;
                webView.layout(0, 0, width, height);

                String htmlContent = CertificateTemplate.getHTMLTemplate(userName, courseTitle, date, instructorName, hours, qrBase64);

                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        
                        // Wait a tiny bit for layout styling to settle
                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            try {
                                Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                                Canvas canvas = new Canvas(bitmap);
                                view.draw(canvas);

                                // Write to PDF
                                PdfDocument document = new PdfDocument();
                                PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(width, height, 1).create();
                                PdfDocument.Page page = document.startPage(pageInfo);
                                
                                Canvas pdfCanvas = page.getCanvas();
                                pdfCanvas.drawBitmap(bitmap, 0, 0, null);
                                document.finishPage(page);

                                if (!outputFile.getParentFile().exists()) {
                                    outputFile.getParentFile().mkdirs();
                                }
                                FileOutputStream fos = new FileOutputStream(outputFile);
                                document.writeTo(fos);
                                fos.close();
                                document.close();

                                callback.onSuccess(outputFile);
                            } catch (Exception e) {
                                callback.onError("Failed to render PDF: " + e.getMessage());
                            }
                        }, 500);
                    }
                });

                webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null);

            } catch (Exception e) {
                callback.onError("Failed to setup WebView: " + e.getMessage());
            }
        });
    }
}
