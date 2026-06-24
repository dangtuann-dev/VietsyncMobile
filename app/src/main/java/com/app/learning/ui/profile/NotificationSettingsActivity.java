package com.app.learning.ui.profile;

import com.app.learning.ui.base.BaseActivity;
import com.example.vietsyncmobile.R;




public class NotificationSettingsActivity extends BaseActivity {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_notification_settings;
    }

    @Override
    protected void initViews() {
        if (getSupportFragmentManager().findFragmentById(R.id.container) == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new NotificationSettingsFragment())
                    .commit();
        }
    }

    @Override
    protected void initObservers() {

    }
}
