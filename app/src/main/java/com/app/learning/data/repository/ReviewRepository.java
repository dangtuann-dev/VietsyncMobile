package com.app.learning.data.repository;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.app.learning.data.api.ApiClient;
import com.app.learning.data.api.ReviewApi;
import com.app.learning.data.api.Resource;
import com.app.learning.data.model.Review;
import com.app.learning.data.model.Enrollment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;

public class ReviewRepository extends BaseRepository {

    private final ReviewApi reviewApi;

    public ReviewRepository() {
        super();
        this.reviewApi = ApiClient.getInstance().createService(ReviewApi.class);
    }

    public ReviewRepository(@NonNull Context context) {
        this();
    }

    public static class RatingSummary {
        private final float averageRating;
        private final int totalCount;
        private final int[] starDistribution;

        public RatingSummary(float averageRating, int totalCount, int[] starDistribution) {
            this.averageRating = averageRating;
            this.totalCount = totalCount;
            this.starDistribution = starDistribution;
        }

        public float getAverageRating() {
            return averageRating;
        }

        public int getTotalCount() {
            return totalCount;
        }

        public int[] getStarDistribution() {
            return starDistribution;
        }
    }

    public LiveData<Resource<List<Review>>> loadReviews(String courseId, int page) {
        MutableLiveData<Resource<List<Review>>> resultLiveData = new MutableLiveData<>();
        resultLiveData.setValue(Resource.loading());

        Map<String, String> options = new HashMap<>();
        options.put("course_id", "eq." + courseId);
        options.put("select", "*,user:users(full_name,avatar_url)");
        options.put("order", "created_at.desc");

        int limit = 20;
        int offset = (page - 1) * limit;
        options.put("limit", String.valueOf(limit));
        options.put("offset", String.valueOf(offset));

        Call<List<Review>> call = reviewApi.getReviews(options);
        executeCall(call, resultLiveData);
        return resultLiveData;
    }

    public LiveData<Resource<Void>> submitReview(String courseId, String userId, float rating, String comment) {
        MutableLiveData<Resource<Void>> resultLiveData = new MutableLiveData<>();
        resultLiveData.setValue(Resource.loading());

        Map<String, Object> body = new HashMap<>();
        body.put("course_id", courseId);
        body.put("user_id", userId);
        body.put("rating", rating);
        body.put("comment", comment);
        body.put("helpful_count", 0);

        Call<Void> call = reviewApi.submitReview(body);
        executeCall(call, resultLiveData);
        return resultLiveData;
    }

    public LiveData<Resource<Void>> voteHelpful(String reviewId, int newCount) {
        MutableLiveData<Resource<Void>> resultLiveData = new MutableLiveData<>();
        resultLiveData.setValue(Resource.loading());

        Map<String, Object> body = new HashMap<>();
        body.put("helpful_count", newCount);

        Call<Void> call = reviewApi.updateReviewHelpfulCount("eq." + reviewId, body);
        executeCall(call, resultLiveData);
        return resultLiveData;
    }

    public LiveData<Resource<RatingSummary>> getRatingSummary(String courseId) {
        MutableLiveData<Resource<RatingSummary>> resultLiveData = new MutableLiveData<>();
        resultLiveData.setValue(Resource.loading());

        Map<String, String> options = new HashMap<>();
        options.put("course_id", "eq." + courseId);
        options.put("select", "rating");

        Call<List<Review>> call = reviewApi.getReviews(options);
        
        MutableLiveData<Resource<List<Review>>> rawLiveData = new MutableLiveData<>();
        executeCall(call, rawLiveData);

        rawLiveData.observeForever(resource -> {
            if (resource != null) {
                if (resource.status == Resource.Status.SUCCESS) {
                    List<Review> list = resource.data;
                    int totalCount = list != null ? list.size() : 0;
                    int[] dist = new int[5]; // Index 0 = 5 stars, Index 4 = 1 star
                    float totalRating = 0f;

                    if (list != null) {
                        for (Review r : list) {
                            float val = r.getRating();
                            totalRating += val;

                            int rounded = Math.round(val);
                            if (rounded >= 1 && rounded <= 5) {
                                dist[5 - rounded]++;
                            }
                        }
                    }

                    float avg = totalCount > 0 ? (totalRating / totalCount) : 0f;
                    resultLiveData.setValue(Resource.success(new RatingSummary(avg, totalCount, dist)));
                } else if (resource.status == Resource.Status.ERROR) {
                    resultLiveData.setValue(Resource.error(resource.error));
                } else if (resource.status == Resource.Status.LOADING) {
                    resultLiveData.setValue(Resource.loading());
                }
            }
        });

        return resultLiveData;
    }

    public LiveData<Resource<Boolean>> checkCourseCompleted(String userId, String courseId) {
        MutableLiveData<Resource<Boolean>> resultLiveData = new MutableLiveData<>();
        resultLiveData.setValue(Resource.loading());

        Call<List<Enrollment>> call = reviewApi.getEnrollment("eq." + userId, "eq." + courseId);
        MutableLiveData<Resource<List<Enrollment>>> rawLiveData = new MutableLiveData<>();
        executeCall(call, rawLiveData);

        rawLiveData.observeForever(resource -> {
            if (resource != null) {
                if (resource.status == Resource.Status.SUCCESS) {
                    boolean completed = false;
                    if (resource.data != null && !resource.data.isEmpty()) {
                        Enrollment e = resource.data.get(0);
                        completed = e.getProgressPercent() == 100;
                    }
                    resultLiveData.setValue(Resource.success(completed));
                } else if (resource.status == Resource.Status.ERROR) {
                    resultLiveData.setValue(Resource.error(resource.error));
                } else if (resource.status == Resource.Status.LOADING) {
                    resultLiveData.setValue(Resource.loading());
                }
            }
        });

        return resultLiveData;
    }
}
