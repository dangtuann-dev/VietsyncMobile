package com.app.learning.ui.course;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.learning.data.api.Resource;
import com.app.learning.data.model.Review;
import com.app.learning.data.repository.ReviewRepository;
import com.app.learning.ui.base.BaseFragment;
import com.app.learning.ui.widget.RatingBarView;
import com.app.learning.utils.UserPreference;
import com.example.vietsyncmobile.R;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class ReviewsFragment extends BaseFragment {

    private TextView tvReviewsRating, tvReviewsCount;
    private RatingBar rbReviewsStars;
    private RatingBarView ratingBarView;
    private RecyclerView rvReviews;
    private FloatingActionButton fabWriteReview;
    
    private ReviewsAdapter adapter;
    private CourseDetailViewModel courseDetailViewModel;
    private ReviewViewModel reviewViewModel;
    
    private String courseId;
    private String userId;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_reviews;
    }

    @Override
    protected void initViews(View view) {
        tvReviewsRating = view.findViewById(R.id.tv_reviews_rating);
        tvReviewsCount = view.findViewById(R.id.tv_reviews_count);
        rbReviewsStars = view.findViewById(R.id.rb_reviews_stars);
        ratingBarView = view.findViewById(R.id.rating_bar_view);
        rvReviews = view.findViewById(R.id.rv_reviews);
        fabWriteReview = view.findViewById(R.id.fab_write_review);

        rvReviews.setLayoutManager(new LinearLayoutManager(getContext()));
        
        UserPreference userPreference = UserPreference.getInstance(requireContext());
        userId = userPreference.getUserProfile() != null ? userPreference.getUserProfile().getId() : null;

        adapter = new ReviewsAdapter(review -> {
            if (userId == null) {
                Toast.makeText(requireContext(), "Vui lòng đăng nhập để bình chọn!", Toast.LENGTH_SHORT).show();
                return;
            }
            // Optimistic update is handled inside adapter's click listener
            reviewViewModel.voteHelpful(review.getId(), review.getHelpfulCount());
        });
        rvReviews.setAdapter(adapter);

        fabWriteReview.setOnClickListener(v -> {
            if (courseId != null) {
                WriteReviewBottomSheet sheet = WriteReviewBottomSheet.newInstance(courseId);
                sheet.setOnReviewSubmitListener(() -> {
                    reviewViewModel.loadReviews(courseId, 1);
                    reviewViewModel.getRatingSummary(courseId);
                });
                sheet.show(getChildFragmentManager(), "WriteReviewBottomSheet");
            }
        });
    }

    @Override
    protected void initObservers() {
        courseDetailViewModel = new ViewModelProvider(requireActivity()).get(CourseDetailViewModel.class);
        reviewViewModel = new ViewModelProvider(this).get(ReviewViewModel.class);

        courseDetailViewModel.getCourseDetail().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.status == Resource.Status.SUCCESS && resource.data != null) {
                courseId = resource.data.getId();
                
                // Fetch distribution, reviews list, and completion status
                reviewViewModel.checkCourseCompletion(userId, courseId);
                reviewViewModel.getRatingSummary(courseId);
                reviewViewModel.loadReviews(courseId, 1);
            }
        });

        reviewViewModel.getReviewsLiveData().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.status == Resource.Status.SUCCESS && resource.data != null) {
                adapter.setReviews(resource.data);
            }
        });

        reviewViewModel.getRatingSummaryLiveData().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.status == Resource.Status.SUCCESS && resource.data != null) {
                displayRatingSummary(resource.data);
            }
        });

        reviewViewModel.getCompletionStatusLiveData().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.status == Resource.Status.SUCCESS) {
                boolean completed = resource.data != null && resource.data;
                fabWriteReview.setVisibility(completed ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void displayRatingSummary(ReviewRepository.RatingSummary summary) {
        tvReviewsRating.setText(String.format("%.1f", summary.getAverageRating()));
        rbReviewsStars.setRating(summary.getAverageRating());
        tvReviewsCount.setText("(" + summary.getTotalCount() + " đánh giá)");
        ratingBarView.setDistribution(summary.getStarDistribution());
    }

    private static class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ViewHolder> {

        public interface OnHelpfulClickListener {
            void onHelpfulClicked(Review review);
        }

        private final List<Review> reviews = new ArrayList<>();
        private final OnHelpfulClickListener helpfulClickListener;

        public ReviewsAdapter(OnHelpfulClickListener listener) {
            this.helpfulClickListener = listener;
        }

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
            holder.tvHelpfulCount.setText(String.valueOf(review.getHelpfulCount()));

            Glide.with(holder.itemView.getContext())
                    .load(review.getUserAvatar())
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .error(R.drawable.ic_profile_placeholder)
                    .into(holder.ivAvatar);

            holder.btnHelpful.setOnClickListener(v -> {
                if (helpfulClickListener != null) {
                    // Update count locally first
                    review.setHelpfulCount(review.getHelpfulCount() + 1);
                    notifyItemChanged(position);
                    
                    // Call API helper
                    helpfulClickListener.onHelpfulClicked(review);
                }
            });
        }

        @Override
        public int getItemCount() {
            return reviews.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            ImageView ivAvatar;
            TextView tvUser, tvDate, tvComment, tvHelpfulCount;
            RatingBar rbStars;
            View btnHelpful;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                ivAvatar = itemView.findViewById(R.id.iv_review_avatar);
                tvUser = itemView.findViewById(R.id.tv_review_user);
                tvDate = itemView.findViewById(R.id.tv_review_date);
                tvComment = itemView.findViewById(R.id.tv_review_comment);
                rbStars = itemView.findViewById(R.id.rb_review_stars);
                tvHelpfulCount = itemView.findViewById(R.id.tv_helpful_count);
                btnHelpful = itemView.findViewById(R.id.btn_helpful);
            }
        }
    }
}
