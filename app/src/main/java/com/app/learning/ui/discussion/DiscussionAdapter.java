package com.app.learning.ui.discussion;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vietsyncmobile.R;
import com.app.learning.data.model.DiscussionPostModel;
import com.bumptech.glide.Glide;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DiscussionAdapter extends RecyclerView.Adapter<DiscussionAdapter.PostViewHolder> {

    public interface OnPostClickListener {
        void onClick(DiscussionPostModel post);
    }

    private final List<DiscussionPostModel> postList = new ArrayList<>();
    private final OnPostClickListener listener;

    public DiscussionAdapter(OnPostClickListener listener) {
        this.listener = listener;
    }

    public void setPosts(List<DiscussionPostModel> posts) {
        this.postList.clear();
        if (posts != null) {
            this.postList.addAll(posts);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_discussion_post, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        DiscussionPostModel model = postList.get(position);
        holder.tvAuthorName.setText(model.getAuthorName() != null ? model.getAuthorName() : "Học viên");
        holder.tvTimeAgo.setText(model.getCreatedAt() != null ? model.getCreatedAt() : "Gần đây");
        holder.tvTitle.setText(model.getTitle());
        holder.tvBody.setText(model.getBody());
        holder.tvLikesCount.setText("👍 " + model.getLikesCount() + " Lượt thích");
        holder.tvRepliesCount.setText("💬 " + model.getRepliesCount() + " Phản hồi");

        if (model.isSolved()) {
            holder.tvSolvedBadge.setVisibility(View.VISIBLE);
        } else {
            holder.tvSolvedBadge.setVisibility(View.GONE);
        }

        if (model.getAuthorAvatar() != null && !model.getAuthorAvatar().isEmpty()) {
            Glide.with(holder.itemView.getContext()).load(model.getAuthorAvatar()).into(holder.imgAvatar);
        }

        holder.chipGroupTags.removeAllViews();
        if (model.getTags() != null && !model.getTags().isEmpty()) {
            String[] tags = model.getTags().split(",");
            for (String tag : tags) {
                Chip chip = new Chip(holder.itemView.getContext());
                chip.setText(tag.trim());
                chip.setChipBackgroundColorResource(R.color.black);
                chip.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.white));
                holder.chipGroupTags.addView(chip);
            }
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(model);
        });
    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imgAvatar;
        TextView tvAuthorName, tvTimeAgo, tvSolvedBadge, tvTitle, tvBody, tvLikesCount, tvRepliesCount;
        ChipGroup chipGroupTags;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            tvAuthorName = itemView.findViewById(R.id.tvAuthorName);
            tvTimeAgo = itemView.findViewById(R.id.tvTimeAgo);
            tvSolvedBadge = itemView.findViewById(R.id.tvSolvedBadge);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvBody = itemView.findViewById(R.id.tvBody);
            tvLikesCount = itemView.findViewById(R.id.tvLikesCount);
            tvRepliesCount = itemView.findViewById(R.id.tvRepliesCount);
            chipGroupTags = itemView.findViewById(R.id.chipGroupTags);
        }
    }
}
