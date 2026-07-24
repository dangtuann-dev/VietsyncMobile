package com.app.learning.data.api;

import com.app.learning.data.model.DiscussionPostModel;
import com.app.learning.data.model.DiscussionReplyModel;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface DiscussionApi {

    @GET("rest/v1/discussions")
    Call<List<DiscussionPostModel>> getPosts(@Query("course_id") String courseId, @Query("select") String select);

    @POST("rest/v1/discussions")
    Call<List<DiscussionPostModel>> createPost(@Body JsonObject postBody, @Query("select") String select);

    @PATCH("rest/v1/discussions")
    Call<Void> updatePost(@Query("id") String postId, @Body JsonObject updateBody);

    @GET("rest/v1/discussion_replies")
    Call<List<DiscussionReplyModel>> getReplies(@Query("post_id") String postId, @Query("select") String select);

    @POST("rest/v1/discussion_replies")
    Call<List<DiscussionReplyModel>> createReply(@Body JsonObject replyBody, @Query("select") String select);
}
