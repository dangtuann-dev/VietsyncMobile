package com.app.learning.ui.learning;

import android.view.View;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;
import com.app.learning.data.model.User;
import com.app.learning.ui.base.BaseFragment;
import com.app.learning.utils.UserPreference;
import com.example.vietsyncmobile.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MyLearningFragment extends BaseFragment {

    private MyLearningViewModel viewModel;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_my_learning;
    }

    @Override
    protected void initViews(View view) {
        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.view_pager);

        // Prevent reloading the pages when swipe
        viewPager.setOffscreenPageLimit(2);

        MyLearningPagerAdapter pagerAdapter = new MyLearningPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("Đang học");
                    break;
                case 1:
                    tab.setText("Hoàn thành");
                    break;
                case 2:
                    tab.setText("Đã lưu");
                    break;
            }
        }).attach();
    }

    @Override
    protected void initObservers() {
        viewModel = new ViewModelProvider(this).get(MyLearningViewModel.class);
        
        // Pass the logged-in user's ID to the view model
        User user = UserPreference.getInstance(requireContext()).getUserProfile();
        if (user != null) {
            viewModel.setUserId(user.getId());
        }
    }
}

