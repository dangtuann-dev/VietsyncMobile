package com.app.learning.ui.learning;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vietsyncmobile.R;
import com.app.learning.data.model.MilestoneModel;

import java.util.ArrayList;
import java.util.List;

public class MilestoneAdapter extends RecyclerView.Adapter<MilestoneAdapter.MilestoneViewHolder> {

    private List<MilestoneModel> milestoneList = new ArrayList<>();

    public void setMilestones(List<MilestoneModel> milestones) {
        this.milestoneList = milestones != null ? milestones : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MilestoneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_milestone, parent, false);
        return new MilestoneViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MilestoneViewHolder holder, int position) {
        MilestoneModel milestone = milestoneList.get(position);
        holder.tvTitle.setText(milestone.getTitle());
        holder.tvDesc.setText(milestone.getDescription());
        holder.tvDate.setText(milestone.getAchievedDate());
        holder.imgIcon.setImageResource(R.drawable.ic_certificate);
    }

    @Override
    public int getItemCount() {
        return milestoneList.size();
    }

    static class MilestoneViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDesc, tvDate;
        ImageView imgIcon;

        public MilestoneViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvMilestoneTitle);
            tvDesc = itemView.findViewById(R.id.tvMilestoneDesc);
            tvDate = itemView.findViewById(R.id.tvMilestoneDate);
            imgIcon = itemView.findViewById(R.id.imgMilestoneIcon);
        }
    }
}
