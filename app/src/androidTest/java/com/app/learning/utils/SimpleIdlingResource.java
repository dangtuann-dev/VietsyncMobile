package com.app.learning.utils;

import androidx.annotation.Nullable;
import androidx.test.espresso.IdlingResource;

import java.util.concurrent.atomic.AtomicBoolean;

public class SimpleIdlingResource implements IdlingResource {

    @Nullable
    private volatile ResourceCallback resourceCallback;

    private final AtomicBoolean isIdleState = new AtomicBoolean(true);

    @Override
    public String getName() {
        return this.getClass().getName();
    }

    @Override
    public boolean isIdleNow() {
        return isIdleState.get();
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        this.resourceCallback = callback;
    }

    public void setIdleState(boolean isIdleNow) {
        isIdleState.set(isIdleNow);
        if (isIdleNow && resourceCallback != null) {
            resourceCallback.onTransitionToIdle();
        }
    }
}
