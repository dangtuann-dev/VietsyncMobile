package com.app.learning.ui.course;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.app.learning.data.model.Course;
import com.app.learning.data.model.NotificationModel;
import com.app.learning.data.repository.NotificationRepository;
import com.example.vietsyncmobile.R;

public class EnrollmentNotificationHelper {

    private static final String CHANNEL_ID = "enrollment_channel";
    private static final String CHANNEL_NAME = "Đăng ký khóa học";
    private static final String CHANNEL_DESC = "Thông báo đăng ký khóa học thành công";

    public static void sendEnrollmentNotification(Context context, String userId, Course course) {
        if (context == null || course == null || userId == null) return;

        String title = "Đăng ký thành công!";
        String body = "Chúc mừng bạn đã đăng ký thành công khóa học: " + course.getTitle();

        NotificationModel model = new NotificationModel();
        model.setUserId(userId);
        model.setTitle(title);
        model.setBody(body);
        model.setType("enrollment");
        model.setRead(false);
        model.setActionUrl("/courses/" + course.getId());

        NotificationRepository notificationRepository = new NotificationRepository(context);
        notificationRepository.createNotification(model).observeForever(resource -> {
            // Observer kept alive briefly to trigger post in background
        });

        showSystemNotification(context, title, body, course.getId());
    }

    private static void showSystemNotification(Context context, String title, String body, String courseId) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager == null) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = notificationManager.getNotificationChannel(CHANNEL_ID);
            if (channel == null) {
                channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription(CHANNEL_DESC);
                notificationManager.createNotificationChannel(channel);
            }
        }

        Intent intent = new Intent(context, CourseDetailActivity.class);
        intent.putExtra("course_id", courseId);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 
                (int) System.currentTimeMillis(), 
                intent, 
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
