package com.app.learning.data.api;

import com.app.learning.data.model.LearningSessionModel;
import com.app.learning.data.model.MilestoneModel;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface LearningHistoryApi {
    @GET("rest/v1/learning_sessions")
    Call<List<LearningSessionModel>> getLearningSessions(
            @Query("user_id") String userIdFilter,
            @Query("order") String order
    );

    @GET("rest/v1/milestones")
    Call<List<MilestoneModel>> getUserMilestones(
            @Query("user_id") String userIdFilter,
            @Query("order") String order
    );
}
