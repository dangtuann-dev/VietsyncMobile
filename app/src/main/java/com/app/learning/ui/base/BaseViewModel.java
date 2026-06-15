package com.app.learning.ui.base;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * BaseViewModel serves as a base class for all ViewModels in the application.
 * It provides core LiveData objects for tracking loading states and displaying
 * error messages in a consistent manner across all screens.
 */
public abstract class BaseViewModel extends ViewModel {

    // LiveData for tracking whether a background process/network call is in progress
    protected final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    // LiveData for capturing error messages from API or repository operations
    protected final MutableLiveData<String> errorMessage = new MutableLiveData<>(null);

    /**
     * Get the live status of the loading state.
     *
     * @return LiveData containing Boolean loading state
     */
    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    /**
     * Get the live error messages to be displayed to the user.
     *
     * @return LiveData containing error message string
     */
    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    /**
     * Sets the loading state to true.
     */
    protected void showLoading() {
        isLoading.postValue(true);
    }

    /**
     * Sets the loading state to false.
     */
    protected void hideLoading() {
        isLoading.postValue(false);
    }

    /**
     * Sets the current error message.
     *
     * @param message The error message to be broadcasted to UI observers
     */
    protected void setError(String message) {
        errorMessage.postValue(message);
    }

    /**
     * Resets the current error state.
     */
    protected void clearError() {
        errorMessage.postValue(null);
    }

    /**
     * Helper to run an operation with automated loading state management.
     *
     * @param operation A runnable task representing the work to execute
     */
    protected void executeWithLoading(Runnable operation) {
        showLoading();
        try {
            operation.run();
        } catch (Exception e) {
            setError(e.getLocalizedMessage());
        } finally {
            hideLoading();
        }
    }
}
