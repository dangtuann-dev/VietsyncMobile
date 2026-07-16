package com.app.learning.ui.course;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.app.learning.data.api.Resource;
import com.app.learning.data.model.Course;
import com.app.learning.data.model.Review;
import com.app.learning.ui.base.BaseFragment;
import com.example.vietsyncmobile.R;
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;

public class ReviewsFragment extends BaseFragment {

    private TextView tvReviewsRating, tvReviewsCount;
    private RatingBar rbReviewsStars;
    private RecyclerView rvReviews;
    private ReviewsAdapter adapter;
    private CourseDetailViewModel viewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_reviews;
    }

    @Override
    protected void initViews(View view) {
        tvReviewsRating = view.findViewById(R.id.tv_reviews_rating);
        tvReviewsCount = view.findViewById(R.id.tv_reviews_count);
        rbReviewsStars = view.findViewById(R.id.rb_reviews_stars);
        rvReviews = view.findViewById(R.id.rv_reviews);

        rvReviews.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ReviewsAdapter();
        rvReviews.setAdapter(adapter);
    }

    @Override
    protected void initObservers() {
        viewModel = new ViewModelProvider(requireActivity()).get(CourseDetailViewModel.class);

        viewModel.getCourseDetail().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.status == Resource.Status.SUCCESS && resource.data != null) {
                displayAggregateRating(resource.data);
            }
        });

        viewModel.getCourseReviews().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.status == Resource.Status.SUCCESS && resource.data != null) {
                adapter.setReviews(resource.data);
            }
        });
    }

    private void displayAggregateRating(Course course) {
        double rating = course.getRating() > 0 ? course.getRating() : 4.8;
        tvReviewsRating.setText(String.format("%.2f", rating));
        rbReviewsStars.setRating((float) rating);
        
        int reviewCount = (course.getEnrolledCount() > 0) ? (int) (course.getEnrolledCount() * 0.15) + 3 : 15;
        tvReviewsCount.setText("(" + reviewCount + " đánh giá)");
    }

    private static class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {

        private final List<Review> reviews = new ArrayList<>();

        public void setReviews(List<Review> newReviews) {
            reviews.clear();
            if (newReviews != null) {
                reviews.addAll(newReviews);
            }
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Review review = reviews.get(position);
            holder.tvUser.setText(review.getUserName());
            holder.tvDate.setText(review.getDate());
            holder.tvComment.setText(review.getComment());
            holder.rbStars.setRating(review.getRating());

            Glide.with(holder.itemView.getContext())
                    .load(review.getUserAvatar())
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .error(R.drawable.ic_profile_placeholder)
                    .into(holder.ivAvatar);
        }

        @Override
        public int getItemCount() {
            return reviews.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            ImageView ivAvatar;
            TextView tvUser, tvDate, tvComment;
            RatingBar rbStars;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                ivAvatar = itemView.findViewById(R.id.iv_review_avatar);
                tvUser = itemView.findViewById(R.id.tv_review_user);
                tvDate = itemView.findViewById(R.id.tv_review_date);
                tvComment = itemView.findViewById(R.id.tv_review_comment);
                rbStars = itemView.findViewById(R.id.rb_review_stars);
            }
        }
    }
}
