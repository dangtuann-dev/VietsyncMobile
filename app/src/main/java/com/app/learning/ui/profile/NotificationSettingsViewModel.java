package com.app.learning.ui.profile;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.app.learning.data.api.Resource;
import com.app.learning.data.repository.UserRepository;
import com.app.learning.utils.NotificationPreferences;
import com.app.learning.utils.UserPreference;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;





public class NotificationSettingsViewModel extends AndroidViewModel {

    private final UserRepository userRepository;
    private final NotificationPreferences notificationPreferences;
    private final UserPreference userPreference;

    public NotificationSettingsViewModel(@NonNull Application application) {
        super(application);
        this.userRepository = new UserRepository(application);
        this.notificationPreferences = NotificationPreferences.getInstance(application);
        this.userPreference = UserPreference.getInstance(application);
    }




    public String getUserId() {
        if (userPreference.getUserProfile() != null) {
            return userPreference.getUserProfile().getId();
        }
        return null;
    }



    public Flowable<Boolean> getNewCourseAnnouncements() {
        return notificationPreferences.getNewCourseAnnouncements(true);
    }

    public Flowable<Boolean> getAssignmentDeadlines() {
        return notificationPreferences.getAssignmentDeadlines(true);
    }

    public Flowable<Boolean> getDiscussionReplies() {
        return notificationPreferences.getDiscussionReplies(true);
    }

    public Flowable<Boolean> getWeeklyProgressReport() {
        return notificationPreferences.getWeeklyProgressReport(true);
    }

    public Flowable<Boolean> getPromotionalOffers() {
        return notificationPreferences.getPromotionalOffers(true);
    }

    public Flowable<String> getQuietHoursStart() {
        return notificationPreferences.getQuietHoursStart("22:00");
    }

    public Flowable<String> getQuietHoursEnd() {
        return notificationPreferences.getQuietHoursEnd("07:00");
    }

    public Flowable<Boolean> getQuietHoursEnabled() {
        return notificationPreferences.getQuietHoursEnabled(false);
    }



    public Single<?> updateNewCourseAnnouncements(boolean enabled) {
        return notificationPreferences.setNewCourseAnnouncements(enabled);
    }

    public Single<?> updateAssignmentDeadlines(boolean enabled) {
        return notificationPreferences.setAssignmentDeadlines(enabled);
    }

    public Single<?> updateDiscussionReplies(boolean enabled) {
        return notificationPreferences.setDiscussionReplies(enabled);
    }

    public Single<?> updateWeeklyProgressReport(boolean enabled) {
        return notificationPreferences.setWeeklyProgressReport(enabled);
    }

    public Single<?> updatePromotionalOffers(boolean enabled) {
        return notificationPreferences.setPromotionalOffers(enabled);
    }

    public Single<?> updateQuietHoursStart(String time) {
        return notificationPreferences.setQuietHoursStart(time);
    }

    public Single<?> updateQuietHoursEnd(String time) {
        return notificationPreferences.setQuietHoursEnd(time);
    }

    public Single<?> updateQuietHoursEnabled(boolean enabled) {
        return notificationPreferences.setQuietHoursEnabled(enabled);
    }






    public LiveData<Resource<Map<String, Object>>> fetchRemoteSettings(String userId) {
        return userRepository.getUserSettingsFromSupabase(userId);
    }




    public LiveData<Resource<Void>> syncSettingsToRemote(String userId, Map<String, Object> settings) {
        return userRepository.saveUserSettingsToSupabase(userId, settings);
     }




    public void saveToLocalDataStore(Map<String, Object> settings) {
        if (settings == null) return;

        if (settings.containsKey("new_course_announcements")) {
            Object val = settings.get("new_course_announcements");
            if (val instanceof Boolean) {
                updateNewCourseAnnouncements((Boolean) val).subscribe();
            }
        }
        if (settings.containsKey("assignment_deadlines")) {
            Object val = settings.get("assignment_deadlines");
            if (val instanceof Boolean) {
                updateAssignmentDeadlines((Boolean) val).subscribe();
            }
        }
        if (settings.containsKey("discussion_replies")) {
            Object val = settings.get("discussion_replies");
            if (val instanceof Boolean) {
                updateDiscussionReplies((Boolean) val).subscribe();
            }
        }
        if (settings.containsKey("weekly_progress_report")) {
            Object val = settings.get("weekly_progress_report");
            if (val instanceof Boolean) {
                updateWeeklyProgressReport((Boolean) val).subscribe();
            }
        }
        if (settings.containsKey("promotional_offers")) {
            Object val = settings.get("promotional_offers");
            if (val instanceof Boolean) {
                updatePromotionalOffers((Boolean) val).subscribe();
            }
        }
        if (settings.containsKey("quiet_hours_start")) {
            Object val = settings.get("quiet_hours_start");
            if (val instanceof String) {
                updateQuietHoursStart((String) val).subscribe();
            }
        }
        if (settings.containsKey("quiet_hours_end")) {
            Object val = settings.get("quiet_hours_end");
            if (val instanceof String) {
                updateQuietHoursEnd((String) val).subscribe();
            }
        }
        if (settings.containsKey("quiet_hours_enabled")) {
            Object val = settings.get("quiet_hours_enabled");
            if (val instanceof Boolean) {
                updateQuietHoursEnabled((Boolean) val).subscribe();
            }
        }
    }
}
