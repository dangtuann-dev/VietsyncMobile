package com.app.learning.ui.discussion;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vietsyncmobile.R;
import com.app.learning.data.model.DiscussionReplyModel;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReplyAdapter extends RecyclerView.Adapter<ReplyAdapter.ReplyViewHolder> {

    private final List<DiscussionReplyModel> replyList = new ArrayList<>();

    public void setReplies(List<DiscussionReplyModel> replies) {
        this.replyList.clear();
        if (replies != null) {
            this.replyList.addAll(replies);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReplyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_discussion_reply, parent, false);
        return new ReplyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReplyViewHolder holder, int position) {
        DiscussionReplyModel model = replyList.get(position);
        holder.tvAuthorName.setText(model.getAuthorName() != null ? model.getAuthorName() : "Học viên");
        holder.tvTimeAgo.setText(" • " + (model.getCreatedAt() != null ? model.getCreatedAt() : "Gần đây"));
        holder.tvReplyText.setText(model.getReplyText());

        if (model.getAuthorAvatar() != null && !model.getAuthorAvatar().isEmpty()) {
            Glide.with(holder.itemView.getContext()).load(model.getAuthorAvatar()).into(holder.imgAvatar);
        }
    }

    @Override
    public int getItemCount() {
        return replyList.size();
    }

    static class ReplyViewHolder extends RecyclerView.ViewHolder {
        CircleImageView imgAvatar;
        TextView tvAuthorName, tvTimeAgo, tvReplyText;

        public ReplyViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            tvAuthorName = itemView.findViewById(R.id.tvAuthorName);
            tvTimeAgo = itemView.findViewById(R.id.tvTimeAgo);
            tvReplyText = itemView.findViewById(R.id.tvReplyText);
        }
    }
}
