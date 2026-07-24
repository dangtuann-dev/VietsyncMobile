package com.app.learning.ui.gamification;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vietsyncmobile.R;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.LeaderboardViewHolder> {

    public static class LearnerRank {
        private int rank;
        private String name;
        private String avatarUrl;
        private int xp;
        private String rankChange;

        public LearnerRank(int rank, String name, String avatarUrl, int xp, String rankChange) {
            this.rank = rank;
            this.name = name;
            this.avatarUrl = avatarUrl;
            this.xp = xp;
            this.rankChange = rankChange;
        }
    }

    private final List<LearnerRank> learnerList = new ArrayList<>();

    public void setLearners(List<LearnerRank> list) {
        this.learnerList.clear();
        if (list != null) {
            this.learnerList.addAll(list);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LeaderboardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_leaderboard, parent, false);
        return new LeaderboardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardViewHolder holder, int position) {
        LearnerRank item = learnerList.get(position);
        holder.tvRank.setText(String.valueOf(item.rank));
        holder.tvUserName.setText(item.name);
        holder.tvXpPoints.setText(item.xp + " XP");
        holder.tvRankChange.setText(item.rankChange);

        if (item.avatarUrl != null && !item.avatarUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext()).load(item.avatarUrl).into(holder.imgAvatar);
        }
    }

    @Override
    public int getItemCount() {
        return learnerList.size();
    }

    static class LeaderboardViewHolder extends RecyclerView.ViewHolder {
        TextView tvRank, tvUserName, tvXpPoints, tvRankChange;
        CircleImageView imgAvatar;

        public LeaderboardViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRank = itemView.findViewById(R.id.tvRank);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvXpPoints = itemView.findViewById(R.id.tvXpPoints);
            tvRankChange = itemView.findViewById(R.id.tvRankChange);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
        }
    }
}
