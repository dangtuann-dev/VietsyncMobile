package com.app.learning.utils;

import android.app.Activity;

public class UpdateChecker {

    public static void checkOnAppLaunch(Activity activity) {
        InAppUpdateManager manager = new InAppUpdateManager(activity);
        manager.checkForUpdates();
    }
}
