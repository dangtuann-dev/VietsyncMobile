package com.app.learning.data.repository;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.app.learning.data.api.ApiClient;
import com.app.learning.data.api.NotificationApi;
import com.app.learning.data.api.Resource;
import com.app.learning.data.model.NotificationModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationRepository extends BaseRepository {

    private final NotificationApi notificationApi;

    public NotificationRepository() {
        super();
        this.notificationApi = ApiClient.getInstance().createService(NotificationApi.class);
    }

    public NotificationRepository(@NonNull Context context) {
        this();
    }

    public LiveData<Resource<List<NotificationModel>>> getNotifications(String userId) {
        MutableLiveData<Resource<List<NotificationModel>>> resultLiveData = new MutableLiveData<>();
        Call<List<NotificationModel>> call = notificationApi.getNotifications("eq." + userId, "created_at.desc");
        executeCall(call, resultLiveData);
        return resultLiveData;
    }

    public LiveData<Resource<Void>> markAsRead(String notificationId) {
        MutableLiveData<Resource<Void>> resultLiveData = new MutableLiveData<>();
        Map<String, Object> body = new HashMap<>();
        body.put("is_read", true);
        
        Call<Void> call = notificationApi.updateNotification("eq." + notificationId, body);
        executeCall(call, resultLiveData);
        return resultLiveData;
    }

    public LiveData<Resource<Void>> markAllAsRead(String userId) {
        MutableLiveData<Resource<Void>> resultLiveData = new MutableLiveData<>();
        Map<String, Object> body = new HashMap<>();
        body.put("is_read", true);
        
        Call<Void> call = notificationApi.updateAllNotifications("eq." + userId, body);
        executeCall(call, resultLiveData);
        return resultLiveData;
    }

    public LiveData<Resource<Void>> deleteNotification(String notificationId) {
        MutableLiveData<Resource<Void>> resultLiveData = new MutableLiveData<>();
        Call<Void> call = notificationApi.deleteNotification("eq." + notificationId);
        executeCall(call, resultLiveData);
        return resultLiveData;
    }

    public LiveData<Resource<Void>> deleteAllNotifications(String userId) {
        MutableLiveData<Resource<Void>> resultLiveData = new MutableLiveData<>();
        Call<Void> call = notificationApi.deleteAllNotifications("eq." + userId);
        executeCall(call, resultLiveData);
        return resultLiveData;
    }

    public LiveData<Resource<Void>> deleteMultipleNotifications(List<String> notificationIds) {
        MutableLiveData<Resource<Void>> resultLiveData = new MutableLiveData<>();
        
        // Build the in.(id1,id2,...) string
        StringBuilder builder = new StringBuilder("in.(");
        for (int i = 0; i < notificationIds.size(); i++) {
            builder.append(notificationIds.get(i));
            if (i < notificationIds.size() - 1) {
                builder.append(",");
            }
        }
        builder.append(")");
        
        Call<Void> call = notificationApi.deleteMultipleNotifications(builder.toString());
        executeCall(call, resultLiveData);
        return resultLiveData;
    }
}
