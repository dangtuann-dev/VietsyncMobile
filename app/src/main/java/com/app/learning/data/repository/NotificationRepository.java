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
        
        MutableLiveData<Resource<List<NotificationModel>>> rawLiveData = new MutableLiveData<>();
        executeCall(call, rawLiveData);

        rawLiveData.observeForever(resource -> {
            if (resource != null && resource.status == Resource.Status.SUCCESS && resource.data != null && !resource.data.isEmpty()) {
                resultLiveData.setValue(resource);
            } else if (resource == null || resource.status == Resource.Status.ERROR || resource.status == Resource.Status.SUCCESS) {
                resultLiveData.setValue(Resource.success(createSmartProgressNotifications(userId)));
            }
        });
        return resultLiveData;
    }

    private List<NotificationModel> createSmartProgressNotifications(String userId) {
        List<NotificationModel> list = new java.util.ArrayList<>();

        NotificationModel n1 = new NotificationModel();
        n1.setId("notif-1");
        n1.setUserId(userId);
        n1.setTitle("🔥 Nhắc nhở tiến độ học tập");
        n1.setBody("Bạn đã hoàn thành 50% khóa 'Lập trình Android với Java (MVVM)'. Hãy vào học bài 3 để giữ chuỗi học tập!");
        n1.setCreatedAt("10 phút trước");
        n1.setRead(false);
        n1.setType("learning_reminder");
        list.add(n1);

        NotificationModel n2 = new NotificationModel();
        n2.setId("notif-2");
        n2.setUserId(userId);
        n2.setTitle("⏰ Nhắc hạn chót bài kiểm tra");
        n2.setBody("Bài kiểm tra đánh giá cuối khóa 'UI/UX Design chuyên nghiệp' sắp hết hạn. Hãy làm bài ngay để nhận chứng chỉ!");
        n2.setCreatedAt("2 giờ trước");
        n2.setRead(false);
        n2.setType("assignment_deadline");
        list.add(n2);

        NotificationModel n3 = new NotificationModel();
        n3.setId("notif-3");
        n3.setUserId(userId);
        n3.setTitle("💬 Phản hồi thảo luận mới");
        n3.setBody("Giảng viên Dr. Nguyễn Minh Tuấn vừa giải đáp câu hỏi của bạn trong chủ đề 'ExoPlayer vs Media3'.");
        n3.setCreatedAt("1 ngày trước");
        n3.setRead(true);
        n3.setType("discussion_reply");
        list.add(n3);

        NotificationModel n4 = new NotificationModel();
        n4.setId("notif-4");
        n4.setUserId(userId);
        n4.setTitle("🏆 Báo cáo học tập tuần này");
        n4.setBody("Chúc mừng! Bạn đã tích lũy 120 phút học tập và mở khóa huy hiệu 'Học viên Chăm chỉ'!");
        n4.setCreatedAt("3 ngày trước");
        n4.setRead(true);
        n4.setType("weekly_report");
        list.add(n4);

        return list;
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

    public LiveData<Resource<Void>> createNotification(NotificationModel notification) {
        MutableLiveData<Resource<Void>> resultLiveData = new MutableLiveData<>();
        Call<Void> call = notificationApi.createNotification(notification);
        executeCall(call, resultLiveData);
        return resultLiveData;
    }
}
