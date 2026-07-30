package com.app.learning.ui.gamification;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vietsyncmobile.R;
import com.app.learning.data.model.AchievementModel;

import java.util.List;

public class AchievementsFragment extends Fragment {

    private TextView tvUnlockedRatio;
    private ProgressBar pbOverall;
    private RecyclerView rvAchievements;
    private AchievementAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_achievements, container, false);
        initViews(view);
        loadAchievements();
        return view;
    }

    private void initViews(View view) {
        tvUnlockedRatio = view.findViewById(R.id.tvUnlockedRatio);
        pbOverall = view.findViewById(R.id.pbOverallAchievements);
        rvAchievements = view.findViewById(R.id.rvAchievements);

        rvAchievements.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new AchievementAdapter(achievement -> {
            if (achievement.isUnlocked()) {
                AchievementUnlockDialog dialog = new AchievementUnlockDialog(requireContext(), achievement);
                dialog.show();
            }
        });
        rvAchievements.setAdapter(adapter);
    }

    private void loadAchievements() {
        List<AchievementModel> all = AchievementManager.getInstance().getAllAchievements();
        adapter.setAchievements(all);

        int unlockedCount = 0;
        for (AchievementModel m : all) {
            if (m.isUnlocked()) unlockedCount++;
        }

        int total = all.size();
        tvUnlockedRatio.setText(unlockedCount + " / " + total);
        pbOverall.setProgress(total > 0 ? (unlockedCount * 100 / total) : 0);
    }
}
