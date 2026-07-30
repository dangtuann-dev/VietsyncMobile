package com.app.learning.ui.gamification;

import android.content.Context;
import com.app.learning.data.model.AchievementModel;
import java.util.List;

public class AchievementChecker {

    public interface OnAchievementUnlockedListener {
        void onUnlocked(AchievementModel achievement);
    }

    public static void checkAction(Context context, String actionCode, OnAchievementUnlockedListener listener) {
        List<AchievementModel> all = AchievementManager.getInstance().getAllAchievements();
        for (AchievementModel item : all) {
            if (item.getCode().equalsIgnoreCase(actionCode)) {
                if (!item.isUnlocked()) {
                    item.setUnlocked(true);
                    if (listener != null) {
                        listener.onUnlocked(item);
                    }
                }
                break;
            }
        }
    }
}
