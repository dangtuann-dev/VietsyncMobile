package com.app.learning.data.api;

/**
 * NetworkCallback provides a template handler for API network states.
 * ViewModel layers can subscribe to these hooks to automatically update loading indicators,
 * handle successful responses, or represent error states to the user.
 *
 * @param <T> Expected success response model type
 */
public abstract class NetworkCallback<T> {

    /**
     * Triggered when the operation starts or finishes to toggle progress bars.
     * Default implementation does nothing, allowing optional override.
     *
     * @param isLoading True if loading, false if completed
     */
    public void onLoading(boolean isLoading) {
        // Optional hook for loading indicator adjustments
    }

    /**
     * Triggered when the API request succeeds and returns valid data.
     *
     * @param data Successful deserialized data payload
     */
    public abstract void onSuccess(T data);

    /**
     * Triggered when the API request fails due to network, database, or validation issues.
     *
     * @param error Deserialized Supabase/PostgreSQL error model
     */
    public abstract void onError(ApiError error);
}
