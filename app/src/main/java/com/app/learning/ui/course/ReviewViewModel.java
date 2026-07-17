package com.app.learning.ui.course;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.app.learning.data.api.Resource;
import com.app.learning.data.model.Review;
import com.app.learning.data.repository.ReviewRepository;
import com.app.learning.ui.base.BaseViewModel;

import java.util.List;

public class ReviewViewModel extends BaseViewModel {

    private final ReviewRepository reviewRepository;

    private final MutableLiveData<Resource<List<Review>>> reviewsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Resource<ReviewRepository.RatingSummary>> ratingSummaryLiveData = new MutableLiveData<>();
    private final MutableLiveData<Resource<Void>> submitResultLiveData = new MutableLiveData<>();
    private final MutableLiveData<Resource<Void>> voteHelpfulResultLiveData = new MutableLiveData<>();
    private final MutableLiveData<Resource<Boolean>> completionStatusLiveData = new MutableLiveData<>();

    public ReviewViewModel() {
        this.reviewRepository = new ReviewRepository();
    }

    public LiveData<Resource<List<Review>>> getReviewsLiveData() {
        return reviewsLiveData;
    }

    public LiveData<Resource<ReviewRepository.RatingSummary>> getRatingSummaryLiveData() {
        return ratingSummaryLiveData;
    }

    public LiveData<Resource<Void>> getSubmitResultLiveData() {
        return submitResultLiveData;
    }

    public LiveData<Resource<Void>> getVoteHelpfulResultLiveData() {
        return voteHelpfulResultLiveData;
    }

    public LiveData<Resource<Boolean>> getCompletionStatusLiveData() {
        return completionStatusLiveData;
    }

    public void loadReviews(String courseId, int page) {
        reviewRepository.loadReviews(courseId, page).observeForever(resource -> {
            if (resource != null) {
                reviewsLiveData.setValue(resource);
            }
        });
    }

    public void getRatingSummary(String courseId) {
        reviewRepository.getRatingSummary(courseId).observeForever(resource -> {
            if (resource != null) {
                ratingSummaryLiveData.setValue(resource);
            }
        });
    }

    public void submitReview(String courseId, String userId, float rating, String comment) {
        reviewRepository.submitReview(courseId, userId, rating, comment).observeForever(resource -> {
            if (resource != null) {
                submitResultLiveData.setValue(resource);
            }
        });
    }

    public void voteHelpful(String reviewId, int newCount) {
        reviewRepository.voteHelpful(reviewId, newCount).observeForever(resource -> {
            if (resource != null) {
                voteHelpfulResultLiveData.setValue(resource);
            }
        });
    }

    public void checkCourseCompletion(String userId, String courseId) {
        if (userId == null) {
            completionStatusLiveData.setValue(Resource.success(false));
            return;
        }
        reviewRepository.checkCourseCompleted(userId, courseId).observeForever(resource -> {
            if (resource != null) {
                completionStatusLiveData.setValue(resource);
            }
        });
    }
}
