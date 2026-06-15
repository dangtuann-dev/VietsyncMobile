package com.app.learning.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * BaseFragment is the base class for all Fragments in the application.
 * It coordinates with the parent {@link BaseActivity} to manage loading, toast,
 * and snackbar display actions. It also supports automated lifecycle-aware ViewModel observation.
 */
public abstract class BaseFragment extends Fragment {

    @Nullable
    protected BaseActivity baseActivity;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof BaseActivity) {
            baseActivity = (BaseActivity) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int layoutId = getLayoutId();
        if (layoutId != 0) {
            return inflater.inflate(layoutId, container, false);
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        initObservers();
    }

    /**
     * Define the layout resource ID for the fragment.
     * Return 0 if you are inflating binding objects manually in onCreateView.
     *
     * @return layout resource ID (e.g., R.layout.fragment_detail)
     */
    @LayoutRes
    protected abstract int getLayoutId();

    /**
     * Initialize UI widgets and setup listeners. Called in onViewCreated.
     *
     * @param view The root view of the fragment layout
     */
    protected abstract void initViews(View view);

    /**
     * Set up LiveData observations. Called in onViewCreated.
     */
    protected abstract void initObservers();

    /**
     * Requests the parent Activity to display the loading dialog.
     */
    public void showLoading() {
        if (baseActivity != null) {
            baseActivity.showLoading();
        }
    }

    /**
     * Requests the parent Activity to display the loading dialog with a custom message.
     *
     * @param message Custom message to show
     */
    public void showLoading(String message) {
        if (baseActivity != null) {
            baseActivity.showLoading(message);
        }
    }

    /**
     * Requests the parent Activity to hide the loading dialog.
     */
    public void hideLoading() {
        if (baseActivity != null) {
            baseActivity.hideLoading();
        }
    }

    /**
     * Shows error messages utilizing the parent Activity's styling (e.g. Snackbar).
     * Falls back to a standard Toast if the parent Activity is unavailable.
     *
     * @param message Error content
     */
    public void showError(String message) {
        if (baseActivity != null) {
            baseActivity.showError(message);
        } else if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Shows a message via Toast.
     *
     * @param message Message content
     */
    public void showToast(String message) {
        if (baseActivity != null) {
            baseActivity.showToast(message);
        } else if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Observes the provided ViewModel's loading and error state.
     * Uses the ViewLifecycleOwner to prevent memory leaks in Fragments.
     *
     * @param viewModel BaseViewModel instance
     */
    protected void observeViewModel(BaseViewModel viewModel) {
        if (viewModel != null && getViewLifecycleOwner() != null) {
            viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
                if (isLoading != null) {
                    if (isLoading) {
                        showLoading();
                    } else {
                        hideLoading();
                    }
                }
            });

            viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> {
                if (error != null) {
                    showError(error);
                }
            });
        }
    }

    @Override
    public void onDetach() {
        baseActivity = null; // Prevent leaks
        super.onDetach();
    }
}
