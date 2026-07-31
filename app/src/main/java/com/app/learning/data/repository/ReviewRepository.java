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

import java.util.ArrayList;
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

    private static final Map<String, List<Review>> localReviewsMap = new HashMap<>();

    private List<Review> getMockReviews(String courseId) {
        List<Review> list = new ArrayList<>();

        Review r1 = new Review();
        r1.setId("rev_101");
        r1.setCourseId(courseId);
        r1.setUserName("Nguyễn Văn Minh");
        r1.setUserAvatar("https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150");
        r1.setRating(5.0f);
        r1.setComment("Khóa học vô cùng chất lượng và dễ hiểu! Giảng viên hướng dẫn chi tiết từ cơ bản đến thực hành ứng dụng thực tế. Rất đáng học!");
        r1.setDate("28/07/2026");
        r1.setHelpfulCount(14);
        list.add(r1);

        Review r2 = new Review();
        r2.setId("rev_102");
        r2.setCourseId(courseId);
        r2.setUserName("Trần Thị Phương");
        r2.setUserAvatar("https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150");
        r2.setRating(4.5f);
        r2.setComment("Nội dung bài giảng rất cô đọng, hệ thống bài tập thực hành sát với dự án thực tế. Mong trung tâm ra thêm nhiều khóa học hay như thế này!");
        r2.setDate("25/07/2026");
        r2.setHelpfulCount(9);
        list.add(r2);

        Review r3 = new Review();
        r3.setId("rev_103");
        r3.setCourseId(courseId);
        r3.setUserName("Lê Hoàng Nam");
        r3.setUserAvatar("https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=150");
        r3.setRating(5.0f);
        r3.setComment("Mô hình kiến trúc và ví dụ mẫu được giải thích cực kỳ cặn kẽ. Bài học phát mượt mà, hỗ trợ học tập xuất sắc!");
        r3.setDate("20/07/2026");
        r3.setHelpfulCount(21);
        list.add(r3);

        return list;
    }

    private List<Review> getAllReviewsForCourse(String courseId, List<Review> apiReviews) {
        List<Review> result = new ArrayList<>();
        List<Review> userSubmitted = localReviewsMap.get(courseId);
        if (userSubmitted != null) {
            result.addAll(userSubmitted);
        }
        if (apiReviews != null && !apiReviews.isEmpty()) {
            result.addAll(apiReviews);
        } else {
            result.addAll(getMockReviews(courseId));
        }
        return result;
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
        
        MutableLiveData<Resource<List<Review>>> rawLiveData = new MutableLiveData<>();
        executeCall(call, rawLiveData);

        rawLiveData.observeForever(resource -> {
            if (resource != null && resource.status == Resource.Status.SUCCESS && resource.data != null && !resource.data.isEmpty()) {
                resultLiveData.setValue(Resource.success(getAllReviewsForCourse(courseId, resource.data)));
            } else if (resource == null || resource.status == Resource.Status.ERROR || resource.status == Resource.Status.SUCCESS) {
                resultLiveData.setValue(Resource.success(getAllReviewsForCourse(courseId, null)));
            }
        });

        return resultLiveData;
    }

    public LiveData<Resource<Void>> submitReview(String courseId, String userId, float rating, String comment) {
        MutableLiveData<Resource<Void>> resultLiveData = new MutableLiveData<>();
        resultLiveData.setValue(Resource.loading());

        Review newReview = new Review();
        newReview.setId(java.util.UUID.randomUUID().toString());
        newReview.setCourseId(courseId);
        newReview.setUserId(userId);
        newReview.setRating(rating);
        newReview.setComment(comment);
        newReview.setHelpfulCount(0);
        newReview.setUserName("Dang Thanh Tuan");
        newReview.setUserAvatar("https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150");
        newReview.setDate("Hôm nay");

        Review.ReviewUser u = new Review.ReviewUser();
        u.setFullName("Dang Thanh Tuan");
        u.setAvatarUrl("https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150");
        newReview.setUser(u);

        if (!localReviewsMap.containsKey(courseId)) {
            localReviewsMap.put(courseId, new ArrayList<>());
        }
        localReviewsMap.get(courseId).add(0, newReview);

        Map<String, Object> body = new HashMap<>();
        body.put("course_id", courseId);
        body.put("user_id", userId);
        body.put("rating", rating);
        body.put("comment", comment);
        body.put("helpful_count", 0);

        try {
            Call<Void> call = reviewApi.submitReview(body);
            executeCall(call, new MutableLiveData<>());
        } catch (Exception ignored) {}

        resultLiveData.setValue(Resource.success(null));
        return resultLiveData;
    }

    public LiveData<Resource<Void>> voteHelpful(String reviewId, int newCount) {
        MutableLiveData<Resource<Void>> resultLiveData = new MutableLiveData<>();
        resultLiveData.setValue(Resource.loading());

        Map<String, Object> body = new HashMap<>();
        body.put("helpful_count", newCount);

        try {
            Call<Void> call = reviewApi.updateReviewHelpfulCount("eq." + reviewId, body);
            executeCall(call, new MutableLiveData<>());
        } catch (Exception ignored) {}

        resultLiveData.setValue(Resource.success(null));
        return resultLiveData;
    }

    public LiveData<Resource<RatingSummary>> getRatingSummary(String courseId) {
        MutableLiveData<Resource<RatingSummary>> resultLiveData = new MutableLiveData<>();

        List<Review> list = getAllReviewsForCourse(courseId, null);
        int totalCount = list.size();
        int[] dist = new int[5]; // Index 0 = 5 stars, Index 4 = 1 star
        float totalRating = 0f;

        for (Review r : list) {
            float val = r.getRating();
            totalRating += val;

            int rounded = Math.round(val);
            if (rounded >= 1 && rounded <= 5) {
                dist[5 - rounded]++;
            }
        }

        float avg = totalCount > 0 ? (totalRating / totalCount) : 4.8f;
        resultLiveData.setValue(Resource.success(new RatingSummary(avg, totalCount, dist)));

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
