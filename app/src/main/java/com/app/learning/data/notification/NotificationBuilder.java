package com.app.learning.data.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.app.learning.MainActivity;
import com.example.vietsyncmobile.R;

public class NotificationBuilder {

    public static final String CHANNEL_GENERAL = "channel_general";
    public static final String CHANNEL_COURSE = "channel_course";
    public static final String CHANNEL_QUIZ = "channel_quiz";
    public static final String CHANNEL_PROMO = "channel_promo";

    public static void createNotificationChannels(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (nm == null) return;

            NotificationChannel channelGeneral = new NotificationChannel(
                    CHANNEL_GENERAL, "Thông Báo Chung", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationChannel channelCourse = new NotificationChannel(
                    CHANNEL_COURSE, "Thông Báo Khóa Học", NotificationManager.IMPORTANCE_HIGH);
            NotificationChannel channelQuiz = new NotificationChannel(
                    CHANNEL_QUIZ, "Thông Báo Bài Kiểm Tra", NotificationManager.IMPORTANCE_HIGH);
            NotificationChannel channelPromo = new NotificationChannel(
                    CHANNEL_PROMO, "Khuyến Mãi & Ưu Đãi", NotificationManager.IMPORTANCE_LOW);

            nm.createNotificationChannel(channelGeneral);
            nm.createNotificationChannel(channelCourse);
            nm.createNotificationChannel(channelQuiz);
            nm.createNotificationChannel(channelPromo);
        }
    }

    public static void showNotification(Context context, String channelId, String title, String body, Intent targetIntent) {
        createNotificationChannels(context);

        if (targetIntent == null) {
            targetIntent = new Intent(context, MainActivity.class);
        }
        targetIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, (int) System.currentTimeMillis(), targetIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId != null ? channelId : CHANNEL_GENERAL)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(body))
                .setAutoCancel(true)
                .setVibrate(new long[]{0, 250, 250, 250})
                .setContentIntent(pendingIntent);

        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) {
            nm.notify((int) System.currentTimeMillis(), builder.build());
        }
    }
}
