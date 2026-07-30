package com.app.learning.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

public class RatingManager {

    private static final String PREF_NAME = "rating_prefs";
    private static final String KEY_LAST_PROMPT = "last_prompt_timestamp";

    public static void checkAndPromptRating(Activity activity, String triggerEvent) {
        Context context = activity.getApplicationContext();
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        long lastPrompt = prefs.getLong(KEY_LAST_PROMPT, 0);
        long oneMonthMs = 30L * 24 * 60 * 60 * 1000L;

        if (System.currentTimeMillis() - lastPrompt < oneMonthMs) {
            return; // Already prompted within last month
        }

        prefs.edit().putLong(KEY_LAST_PROMPT, System.currentTimeMillis()).apply();

        // Display In-App Rating prompt
        Toast.makeText(activity, "Cảm ơn bạn đã đồng hành! Đánh giá VietsyncMobile 5★ trên CH Play nhé!", Toast.LENGTH_LONG).show();
    }
}
