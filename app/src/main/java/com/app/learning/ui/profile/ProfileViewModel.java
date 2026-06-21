package com.app.learning.ui.profile;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.app.learning.data.api.Resource;
import com.app.learning.data.model.User;
import com.app.learning.data.model.UserStats;
import com.app.learning.data.repository.UserRepository;
import com.app.learning.utils.UserPreference;

/**
 * ProfileViewModel maintains UI states for profile modifications, learning statistics,
 * password adjustments, and photo uploads.
 */
public class ProfileViewModel extends AndroidViewModel {

    private final UserRepository userRepository;
    private final UserPreference userPreference;

    private final MutableLiveData<String> userIdTrigger = new MutableLiveData<>();
    private final LiveData<Resource<User>> profileData;
    private final LiveData<Resource<UserStats>> statsData;

    private LiveData<Resource<User>> updateStatus;
    private LiveData<Resource<String>> uploadStatus;
    private LiveData<Resource<Void>> passwordStatus;

    public ProfileViewModel(@NonNull Application application) {
        super(application);
        this.userRepository = new UserRepository(application);
        this.userPreference = UserPreference.getInstance(application);

        // Fetch user profile remotely whenever the userId trigger changes
        profileData = Transformations.switchMap(userIdTrigger, userRepository::getUserProfile);
        
        // Fetch user stats remotely whenever the userId trigger changes
        statsData = Transformations.switchMap(userIdTrigger, userRepository::getUserStats);
    }

    /**
     * Retrieve local cache user details (sync/instant).
     */
    public User getLocalProfile() {
        return userPreference.getUserProfile();
    }

    /**
     * Triggers remote loading of profile and stats details.
     */
    public void loadUserProfile(String userId) {
        userIdTrigger.setValue(userId);
    }

    /**
     * Exposes remote loaded profile LiveData.
     */
    public LiveData<Resource<User>> getProfileData() {
        return profileData;
    }

    /**
     * Exposes remote stats counts LiveData.
     */
    public LiveData<Resource<UserStats>> getStatsData() {
        return statsData;
    }

    /**
     * Modifies profile full name and bio.
     */
    public LiveData<Resource<User>> updateProfile(String userId, String fullName, String bio, String avatarUrl) {
        updateStatus = userRepository.updateProfile(userId, fullName, bio, avatarUrl);
        return updateStatus;
    }

    /**
     * Uploads chosen avatar to storage.
     */
    public LiveData<Resource<String>> uploadAvatar(String userId, byte[] imageBytes, String mimeType) {
        uploadStatus = userRepository.uploadAvatar(userId, imageBytes, mimeType);
        return uploadStatus;
    }

    /**
     * Performs password updating.
     */
    public LiveData<Resource<Void>> changePassword(String newPassword) {
        passwordStatus = userRepository.changePassword(newPassword);
        return passwordStatus;
    }

    /**
     * Signs out the user by deleting all local sessions.
     */
    public void logout() {
        userRepository.logout();
    }
}
