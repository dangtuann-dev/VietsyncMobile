package com.app.learning.ui.profile;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.app.learning.data.api.Resource;
import com.example.vietsyncmobile.R;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class NotificationSettingsFragment extends Fragment {

    private NotificationSettingsViewModel viewModel;
    private final CompositeDisposable disposables = new CompositeDisposable();

    private SwitchCompat switchNewCourse;
    private SwitchCompat switchAssignment;
    private SwitchCompat switchDiscussion;
    private SwitchCompat switchWeeklyProgress;
    private SwitchCompat switchPromotional;
    private SwitchCompat switchQuietHours;

    private View layoutQuietHoursDetails;
    private View rowStartTime;
    private TextView tvStartTime;
    private View rowEndTime;
    private TextView tvEndTime;
    private View btnBack;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notification_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(NotificationSettingsViewModel.class);


        btnBack = view.findViewById(R.id.btnBack);
        switchNewCourse = view.findViewById(R.id.switchNewCourse);
        switchAssignment = view.findViewById(R.id.switchAssignment);
        switchDiscussion = view.findViewById(R.id.switchDiscussion);
        switchWeeklyProgress = view.findViewById(R.id.switchWeeklyProgress);
        switchPromotional = view.findViewById(R.id.switchPromotional);
        switchQuietHours = view.findViewById(R.id.switchQuietHours);

        layoutQuietHoursDetails = view.findViewById(R.id.layoutQuietHoursDetails);
        rowStartTime = view.findViewById(R.id.rowStartTime);
        tvStartTime = view.findViewById(R.id.tvStartTime);
        rowEndTime = view.findViewById(R.id.rowEndTime);
        tvEndTime = view.findViewById(R.id.tvEndTime);

        btnBack.setOnClickListener(v -> requireActivity().onBackPressed());

        setupListeners();
        observeLocalPreferences();
        fetchAndSyncRemotePreferences();
    }

    private void setupListeners() {

        switchNewCourse.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()) {
                disposables.add(viewModel.updateNewCourseAnnouncements(isChecked)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(prefs -> syncAllToSupabase(), throwable -> {}));
            }
        });

        switchAssignment.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()) {
                disposables.add(viewModel.updateAssignmentDeadlines(isChecked)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(prefs -> syncAllToSupabase(), throwable -> {}));
            }
        });

        switchDiscussion.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()) {
                disposables.add(viewModel.updateDiscussionReplies(isChecked)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(prefs -> syncAllToSupabase(), throwable -> {}));
            }
        });

        switchWeeklyProgress.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()) {
                disposables.add(viewModel.updateWeeklyProgressReport(isChecked)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(prefs -> syncAllToSupabase(), throwable -> {}));
            }
        });

        switchPromotional.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()) {
                disposables.add(viewModel.updatePromotionalOffers(isChecked)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(prefs -> syncAllToSupabase(), throwable -> {}));
            }
        });

        switchQuietHours.setOnCheckedChangeListener((buttonView, isChecked) -> {
            toggleQuietHoursLayout(isChecked);
            if (buttonView.isPressed()) {
                disposables.add(viewModel.updateQuietHoursEnabled(isChecked)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(prefs -> syncAllToSupabase(), throwable -> {}));
            }
        });


        rowStartTime.setOnClickListener(v -> showTimePickerDialog(true));
        rowEndTime.setOnClickListener(v -> showTimePickerDialog(false));
     }

     private void observeLocalPreferences() {

         disposables.add(viewModel.getNewCourseAnnouncements()
                 .subscribeOn(Schedulers.io())
                 .observeOn(AndroidSchedulers.mainThread())
                 .subscribe(switchNewCourse::setChecked, throwable -> {}));

         disposables.add(viewModel.getAssignmentDeadlines()
                 .subscribeOn(Schedulers.io())
                 .observeOn(AndroidSchedulers.mainThread())
                 .subscribe(switchAssignment::setChecked, throwable -> {}));

         disposables.add(viewModel.getDiscussionReplies()
                 .subscribeOn(Schedulers.io())
                 .observeOn(AndroidSchedulers.mainThread())
                 .subscribe(switchDiscussion::setChecked, throwable -> {}));

         disposables.add(viewModel.getWeeklyProgressReport()
                 .subscribeOn(Schedulers.io())
                 .observeOn(AndroidSchedulers.mainThread())
                 .subscribe(switchWeeklyProgress::setChecked, throwable -> {}));

         disposables.add(viewModel.getPromotionalOffers()
                 .subscribeOn(Schedulers.io())
                 .observeOn(AndroidSchedulers.mainThread())
                 .subscribe(switchPromotional::setChecked, throwable -> {}));

         disposables.add(viewModel.getQuietHoursStart()
                 .subscribeOn(Schedulers.io())
                 .observeOn(AndroidSchedulers.mainThread())
                 .subscribe(tvStartTime::setText, throwable -> {}));

         disposables.add(viewModel.getQuietHoursEnd()
                 .subscribeOn(Schedulers.io())
                 .observeOn(AndroidSchedulers.mainThread())
                 .subscribe(tvEndTime::setText, throwable -> {}));

         disposables.add(viewModel.getQuietHoursEnabled()
                 .subscribeOn(Schedulers.io())
                 .observeOn(AndroidSchedulers.mainThread())
                 .subscribe(enabled -> {
                     switchQuietHours.setChecked(enabled);
                     toggleQuietHoursLayout(enabled);
                 }, throwable -> {}));
     }

     private void fetchAndSyncRemotePreferences() {
         String userId = viewModel.getUserId();
         if (userId == null) return;

         viewModel.fetchRemoteSettings(userId).observe(getViewLifecycleOwner(), resource -> {
             if (resource.isSuccess() && resource.data != null && !resource.data.isEmpty()) {

                 viewModel.saveToLocalDataStore(resource.data);
             }
         });
     }

    private void toggleQuietHoursLayout(boolean show) {
        ChangeBounds transition = new ChangeBounds();
        transition.setDuration(250);
        TransitionManager.beginDelayedTransition((ViewGroup) layoutQuietHoursDetails.getParent(), transition);
        layoutQuietHoursDetails.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void showTimePickerDialog(boolean isStart) {
        String timeStr = isStart ? tvStartTime.getText().toString() : tvEndTime.getText().toString();
        int hour = 22;
        int minute = 0;

        try {
            String[] parts = timeStr.split(":");
            hour = Integer.parseInt(parts[0]);
            minute = Integer.parseInt(parts[1]);
        } catch (Exception ignored) {}

        TimePickerDialog dialog = new TimePickerDialog(requireContext(), (view, selectedHour, selectedMinute) -> {
            String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute);
            if (isStart) {
                tvStartTime.setText(formattedTime);
                disposables.add(viewModel.updateQuietHoursStart(formattedTime)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(prefs -> syncAllToSupabase(), throwable -> {}));
            } else {
                tvEndTime.setText(formattedTime);
                disposables.add(viewModel.updateQuietHoursEnd(formattedTime)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(prefs -> syncAllToSupabase(), throwable -> {}));
            }
        }, hour, minute, true);

        dialog.show();
    }

    private void syncAllToSupabase() {
        String userId = viewModel.getUserId();
        if (userId == null) return;

        Map<String, Object> settings = new HashMap<>();
        settings.put("new_course_announcements", switchNewCourse.isChecked());
        settings.put("assignment_deadlines", switchAssignment.isChecked());
        settings.put("discussion_replies", switchDiscussion.isChecked());
        settings.put("weekly_progress_report", switchWeeklyProgress.isChecked());
        settings.put("promotional_offers", switchPromotional.isChecked());
        settings.put("quiet_hours_enabled", switchQuietHours.isChecked());
        settings.put("quiet_hours_start", tvStartTime.getText().toString());
        settings.put("quiet_hours_end", tvEndTime.getText().toString());

        viewModel.syncSettingsToRemote(userId, settings).observe(getViewLifecycleOwner(), resource -> {
            if (resource.isError()) {
                Toast.makeText(requireContext(), "Không thể đồng bộ cài đặt với máy chủ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        disposables.clear();
    }
}
