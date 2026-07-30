package com.app.learning.ui.discussion;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vietsyncmobile.R;
import com.app.learning.data.model.PeerReviewModel;

import java.util.ArrayList;
import java.util.List;

public class PeerReviewAdapter extends RecyclerView.Adapter<PeerReviewAdapter.ReviewViewHolder> {

    private List<PeerReviewModel> reviews = new ArrayList<>();

    public void setReviews(List<PeerReviewModel> list) {
        this.reviews = list != null ? list : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_peer_review, parent, false);
        return new ReviewViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        PeerReviewModel review = reviews.get(position);
        holder.tvReviewerAlias.setText(review.getReviewerAlias());
        holder.tvReviewComment.setText(review.getComment());
        holder.tvReviewDate.setText(review.getReviewedAt());
        holder.rbAverage.setRating(review.getAverageRating());
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView tvReviewerAlias, tvReviewComment, tvReviewDate;
        RatingBar rbAverage;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReviewerAlias = itemView.findViewById(R.id.tvReviewerAlias);
            tvReviewComment = itemView.findViewById(R.id.tvReviewComment);
            tvReviewDate = itemView.findViewById(R.id.tvReviewDate);
            rbAverage = itemView.findViewById(R.id.rbAverage);
        }
    }
}
