package com.app.learning.utils;

import android.content.Context;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;

public class HapticFeedbackHelper {

    public static void performHapticFeedback(View view) {
        if (view != null) {
            view.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);
        }
    }

    public static void vibrateSuccess(Context context) {
        vibrate(context, 100);
    }

    public static void vibrateError(Context context) {
        vibrate(context, 250);
    }

    private static void vibrate(Context context, long durationMs) {
        if (context == null) return;
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        if (v == null || !v.hasVibrator()) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(durationMs);
        }
    }
}
