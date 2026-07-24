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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vietsyncmobile.R;
import com.app.learning.utils.GamificationManager;
import com.app.learning.utils.StreakManager;

import java.util.ArrayList;
import java.util.List;

public class GamificationFragment extends Fragment {

    private TextView tvLevelNumber, tvLevelTitle, tvXpProgressText, tvStreakDays, tvLongestStreak;
    private ProgressBar progressBarXp;
    private RecyclerView rvLeaderboard;

    private GamificationManager gamificationManager;
    private StreakManager streakManager;
    private LeaderboardAdapter leaderboardAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gamification, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvLevelNumber = view.findViewById(R.id.tvLevelNumber);
        tvLevelTitle = view.findViewById(R.id.tvLevelTitle);
        tvXpProgressText = view.findViewById(R.id.tvXpProgressText);
        tvStreakDays = view.findViewById(R.id.tvStreakDays);
        tvLongestStreak = view.findViewById(R.id.tvLongestStreak);
        progressBarXp = view.findViewById(R.id.progressBarXp);
        rvLeaderboard = view.findViewById(R.id.rvLeaderboard);

        gamificationManager = new GamificationManager(requireContext());
        streakManager = new StreakManager(requireContext());

        leaderboardAdapter = new LeaderboardAdapter();
        rvLeaderboard.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvLeaderboard.setAdapter(leaderboardAdapter);

        updateUi();
        loadLeaderboard();
    }

    private void updateUi() {
        int xp = gamificationManager.getTotalXp();
        int level = gamificationManager.getCurrentLevel();
        String title = gamificationManager.getLevelTitle(level);

        tvLevelNumber.setText(String.valueOf(level));
        tvLevelTitle.setText(title);
        tvXpProgressText.setText(xp + " XP");
        progressBarXp.setProgress(xp);

        int currentStreak = streakManager.getCurrentStreak();
        int longestStreak = streakManager.getLongestStreak();

        tvStreakDays.setText("Chuỗi " + currentStreak + " ngày học liên tục!");
        tvLongestStreak.setText("Kỷ lục cao nhất: " + longestStreak + " ngày");
    }

    private void loadLeaderboard() {
        List<LeaderboardAdapter.LearnerRank> list = new ArrayList<>();
        list.add(new LeaderboardAdapter.LearnerRank(1, "Trần Văn An", "", 1250, "▲ 1"));
        list.add(new LeaderboardAdapter.LearnerRank(2, "Phạm Thị Bình", "", 1100, "▼ 1"));
        list.add(new LeaderboardAdapter.LearnerRank(3, "Nguyễn Hoàng Nam", "", 950, "▲ 2"));
        list.add(new LeaderboardAdapter.LearnerRank(4, "Lê Thu Hà", "", 820, "━ 0"));
        list.add(new LeaderboardAdapter.LearnerRank(5, "Đỗ Minh Quân", "", 740, "▲ 3"));

        leaderboardAdapter.setLearners(list);
    }
}
