package com.app.learning.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.core.content.FileProvider;

import java.io.File;

public class ShareHelper {

    public static void sharePdfFile(Context context, File pdfFile, String title) {
        if (pdfFile == null || !pdfFile.exists()) return;

        try {
            String authority = context.getPackageName() + ".fileprovider";
            Uri contentUri = FileProvider.getUriForFile(context, authority, pdfFile);

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("application/pdf");
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, title);
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Chúc mừng! Đây là chứng chỉ hoàn thành khóa học từ Vietsync Mobile.");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            context.startActivity(Intent.createChooser(shareIntent, "Chia sẻ chứng chỉ PDF"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void shareText(Context context, String title, String content) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, title);
        shareIntent.putExtra(Intent.EXTRA_TEXT, content);
        context.startActivity(Intent.createChooser(shareIntent, "Chia sẻ qua"));
    }
}
