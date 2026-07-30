package com.app.learning.ui.gamification;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vietsyncmobile.R;
import com.app.learning.data.model.AchievementModel;

import java.util.ArrayList;
import java.util.List;

public class AchievementAdapter extends RecyclerView.Adapter<AchievementAdapter.AchievementViewHolder> {

    private List<AchievementModel> list = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(AchievementModel model);
    }

    public AchievementAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setAchievements(List<AchievementModel> data) {
        this.list = data != null ? data : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AchievementViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_achievement, parent, false);
        return new AchievementViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AchievementViewHolder holder, int position) {
        AchievementModel item = list.get(position);
        holder.tvTitle.setText(item.getTitle());
        holder.tvDesc.setText(item.getDescription());

        int pct = item.getTargetCount() > 0 ? (item.getCurrentProgress() * 100 / item.getTargetCount()) : 0;
        holder.pbAchievement.setProgress(pct);

        if (item.isUnlocked()) {
            holder.imgBadgeIcon.clearColorFilter();
            holder.imgBadgeIcon.setAlpha(1.0f);
        } else {
            // Grayscale filter for locked badges
            ColorMatrix matrix = new ColorMatrix();
            matrix.setSaturation(0);
            ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
            holder.imgBadgeIcon.setColorFilter(filter);
            holder.imgBadgeIcon.setAlpha(0.5f);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class AchievementViewHolder extends RecyclerView.ViewHolder {
        ImageView imgBadgeIcon;
        TextView tvTitle, tvDesc;
        ProgressBar pbAchievement;

        public AchievementViewHolder(@NonNull View itemView) {
            super(itemView);
            imgBadgeIcon = itemView.findViewById(R.id.imgBadgeIcon);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDesc = itemView.findViewById(R.id.tvDesc);
            pbAchievement = itemView.findViewById(R.id.pbAchievement);
        }
    }
}
