package com.app.learning.data.api;

import com.app.learning.data.model.PeerReviewModel;
import com.app.learning.data.model.PeerSubmissionModel;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface PeerReviewApi {

    @POST("rest/v1/peer_submissions")
    Call<PeerSubmissionModel> submitAssignment(@Body PeerSubmissionModel submission);

    @GET("rest/v1/peer_submissions")
    Call<List<PeerSubmissionModel>> getSubmissionsToReview(
            @Query("assignment_id") String assignmentIdFilter,
            @Query("user_id") String notUserIdFilter
    );

    @POST("rest/v1/peer_reviews")
    Call<PeerReviewModel> submitReview(@Body PeerReviewModel review);

    @GET("rest/v1/peer_reviews")
    Call<List<PeerReviewModel>> getReceivedReviews(
            @Query("submission_id") String submissionIdFilter
    );
}
