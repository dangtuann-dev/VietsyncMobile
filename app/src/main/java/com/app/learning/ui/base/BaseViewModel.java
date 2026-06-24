package com.app.learning.ui.base;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;






public abstract class BaseViewModel extends ViewModel {


    protected final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);


    protected final MutableLiveData<String> errorMessage = new MutableLiveData<>(null);






    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }






    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }




    protected void showLoading() {
        isLoading.postValue(true);
    }




    protected void hideLoading() {
        isLoading.postValue(false);
    }






    protected void setError(String message) {
        errorMessage.postValue(message);
    }




    protected void clearError() {
        errorMessage.postValue(null);
    }






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
