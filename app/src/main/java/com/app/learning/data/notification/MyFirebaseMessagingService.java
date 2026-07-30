package com.app.learning.data.notification;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.app.learning.data.repository.FCMTokenRepository;
import com.app.learning.utils.DeepLinkHandler;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        // Save FCM token to Supabase
        FCMTokenRepository repository = new FCMTokenRepository(getApplicationContext());
        repository.saveFCMToken("current_user_id", token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String title = "Thông báo VietsyncMobile";
        String body = "";

        if (remoteMessage.getNotification() != null) {
            if (remoteMessage.getNotification().getTitle() != null) {
                title = remoteMessage.getNotification().getTitle();
            }
            if (remoteMessage.getNotification().getBody() != null) {
                body = remoteMessage.getNotification().getBody();
            }
        }

        Map<String, String> data = remoteMessage.getData();
        Bundle extras = new Bundle();
        if (data != null) {
            for (Map.Entry<String, String> entry : data.entrySet()) {
                extras.putString(entry.getKey(), entry.getValue());
            }
        }

        Intent targetIntent = DeepLinkHandler.createTargetIntent(getApplicationContext(), extras);
        String channelId = data.get("channel_id");

        NotificationBuilder.showNotification(
                getApplicationContext(),
                channelId != null ? channelId : NotificationBuilder.CHANNEL_GENERAL,
                title,
                body,
                targetIntent
        );
    }
}
