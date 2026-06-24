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


        profileData = Transformations.switchMap(userIdTrigger, userRepository::getUserProfile);


        statsData = Transformations.switchMap(userIdTrigger, userRepository::getUserStats);
    }




    public User getLocalProfile() {
        return userPreference.getUserProfile();
    }




    public void loadUserProfile(String userId) {
        userIdTrigger.setValue(userId);
    }




    public LiveData<Resource<User>> getProfileData() {
        return profileData;
    }




    public LiveData<Resource<UserStats>> getStatsData() {
        return statsData;
    }




    public LiveData<Resource<User>> updateProfile(String userId, String fullName, String bio, String avatarUrl) {
        updateStatus = userRepository.updateProfile(userId, fullName, bio, avatarUrl);
        return updateStatus;
    }




    public LiveData<Resource<String>> uploadAvatar(String userId, byte[] imageBytes, String mimeType) {
        uploadStatus = userRepository.uploadAvatar(userId, imageBytes, mimeType);
        return uploadStatus;
    }




    public LiveData<Resource<Void>> changePassword(String newPassword) {
        passwordStatus = userRepository.changePassword(newPassword);
        return passwordStatus;
    }




    public void logout() {
        userRepository.logout();
    }
}
