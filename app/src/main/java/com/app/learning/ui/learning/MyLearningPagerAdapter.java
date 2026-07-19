package com.app.learning.ui.learning;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class MyLearningPagerAdapter extends FragmentStateAdapter {

    public MyLearningPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new InProgressFragment();
            case 1:
                return new CompletedFragment();
            case 2:
                return new SavedFragment();
            default:
                return new InProgressFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
