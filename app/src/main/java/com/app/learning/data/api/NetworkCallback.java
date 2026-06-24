package com.app.learning.data.api;








public abstract class NetworkCallback<T> {







    public void onLoading(boolean isLoading) {

    }






    public abstract void onSuccess(T data);






    public abstract void onError(ApiError error);
}
