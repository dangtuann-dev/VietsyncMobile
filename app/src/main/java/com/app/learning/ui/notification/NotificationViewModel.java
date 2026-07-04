package com.app.learning.ui.notification;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.app.learning.data.api.Resource;
import com.app.learning.data.model.NotificationModel;
import com.app.learning.data.repository.NotificationRepository;
import com.app.learning.utils.UserPreference;

import java.util.List;

public class NotificationViewModel extends AndroidViewModel {

    private final NotificationRepository repository;
    private final UserPreference userPreference;
    
    private final MediatorLiveData<Resource<List<NotificationModel>>> notificationsLiveData = new MediatorLiveData<>();
    private final MutableLiveData<Resource<Void>> actionResultLiveData = new MutableLiveData<>();

    public NotificationViewModel(@NonNull Application application) {
        super(application);
        this.repository = new NotificationRepository(application);
        this.userPreference = UserPreference.getInstance(application);
    }

    public void loadNotifications() {
        String userId = userPreference.getUserProfile() != null ? userPreference.getUserProfile().getId() : null;
        if (userId != null) {
            LiveData<Resource<List<NotificationModel>>> source = repository.getNotifications(userId);
            notificationsLiveData.addSource(source, resource -> {
                notificationsLiveData.setValue(resource);
                if (resource.status != Resource.Status.LOADING) {
                    notificationsLiveData.removeSource(source);
                }
            });
        }
    }

    public LiveData<Resource<List<NotificationModel>>> getNotificationsLiveData() {
        return notificationsLiveData;
    }

    public LiveData<Resource<Void>> getActionResultLiveData() {
        return actionResultLiveData;
    }

    public void markAsRead(String notificationId) {
        actionResultLiveData.setValue(Resource.loading());
        repository.markAsRead(notificationId).observeForever(result -> {
            actionResultLiveData.setValue(result);
            if (result.status == Resource.Status.SUCCESS) {
                // Refresh list if needed or let UI handle it locally
                loadNotifications();
            }
        });
    }

    public void markAllRead() {
        String userId = userPreference.getUserProfile() != null ? userPreference.getUserProfile().getId() : null;
        if (userId != null) {
            actionResultLiveData.setValue(Resource.loading());
            repository.markAllAsRead(userId).observeForever(result -> {
                actionResultLiveData.setValue(result);
                if (result.status == Resource.Status.SUCCESS) {
                    loadNotifications();
                }
            });
        }
    }

    public void deleteNotification(String notificationId) {
        actionResultLiveData.setValue(Resource.loading());
        repository.deleteNotification(notificationId).observeForever(result -> {
            actionResultLiveData.setValue(result);
        });
    }

    public void deleteAllNotifications() {
        String userId = userPreference.getUserProfile() != null ? userPreference.getUserProfile().getId() : null;
        if (userId != null) {
            actionResultLiveData.setValue(Resource.loading());
            repository.deleteAllNotifications(userId).observeForever(result -> {
                actionResultLiveData.setValue(result);
                if (result.status == Resource.Status.SUCCESS) {
                    loadNotifications();
                }
            });
        }
    }

    public void deleteSelectedNotifications(List<String> notificationIds) {
        if (notificationIds == null || notificationIds.isEmpty()) return;
        
        actionResultLiveData.setValue(Resource.loading());
        repository.deleteMultipleNotifications(notificationIds).observeForever(result -> {
            actionResultLiveData.setValue(result);
            if (result.status == Resource.Status.SUCCESS) {
                loadNotifications();
            }
        });
    }
}
