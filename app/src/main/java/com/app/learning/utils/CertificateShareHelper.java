package com.app.learning.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class CertificateShareHelper {

    public static File savePdfToDownloads(Context context, File sourceFile, String courseTitle) {
        try {
            String sanitizedTitle = courseTitle.replaceAll("[^a-zA-Z0-9.-]", "_");
            String fileName = "Certificate_" + sanitizedTitle + "_" + System.currentTimeMillis() + ".pdf";
            
            // Get public Downloads folder
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs();
            }

            File targetFile = new File(downloadsDir, fileName);

            // Copy file content
            InputStream in = new FileInputStream(sourceFile);
            OutputStream out = new FileOutputStream(targetFile);

            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();

            Toast.makeText(context, "Đã lưu chứng chỉ vào thư mục Downloads!", Toast.LENGTH_LONG).show();
            return targetFile;
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Không thể lưu chứng chỉ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    public static void shareCertificatePdf(Context context, File pdfFile) {
        try {
            Uri pdfUri = FileProvider.getUriForFile(
                    context,
                    context.getPackageName() + ".fileprovider",
                    pdfFile
            );

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("application/pdf");
            shareIntent.putExtra(Intent.EXTRA_STREAM, pdfUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            
            context.startActivity(Intent.createChooser(shareIntent, "Chia sẻ chứng chỉ qua..."));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "Không thể chia sẻ chứng chỉ: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
