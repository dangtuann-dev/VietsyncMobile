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






public abstract class BaseFragment extends Fragment {

    @Nullable
    protected BaseActivity baseActivity;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof BaseActivity) {
            baseActivity = (BaseActivity) context;
        }


        if (getClass().isAnnotationPresent(com.app.learning.utils.RequireRole.class)) {
            com.app.learning.utils.RequireRole annotation = getClass().getAnnotation(com.app.learning.utils.RequireRole.class);
            if (annotation != null) {
                com.app.learning.utils.RoleManager.Role requiredRole = annotation.value();
                com.app.learning.utils.RoleManager.Role currentRole = com.app.learning.utils.RoleManager.getInstance(context).getCurrentRole();
                if (currentRole != requiredRole && currentRole != com.app.learning.utils.RoleManager.Role.ADMIN) {
                    showToast("Bạn không có quyền truy cập chức năng này!");
                    if (getParentFragmentManager() != null) {
                        getParentFragmentManager().beginTransaction().remove(this).commitAllowingStateLoss();
                    }
                }
            }
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







    @LayoutRes
    protected abstract int getLayoutId();






    protected abstract void initViews(View view);




    protected abstract void initObservers();




    public void showLoading() {
        if (baseActivity != null) {
            baseActivity.showLoading();
        }
    }






    public void showLoading(String message) {
        if (baseActivity != null) {
            baseActivity.showLoading(message);
        }
    }




    public void hideLoading() {
        if (baseActivity != null) {
            baseActivity.hideLoading();
        }
    }







    public void showError(String message) {
        if (baseActivity != null) {
            baseActivity.showError(message);
        } else if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        }
    }






    public void showToast(String message) {
        if (baseActivity != null) {
            baseActivity.showToast(message);
        } else if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }







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
        baseActivity = null;
        super.onDetach();
    }
}
