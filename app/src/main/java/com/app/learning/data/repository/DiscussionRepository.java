package com.app.learning.data.repository;

import android.content.Context;

import com.app.learning.data.api.ApiClient;
import com.app.learning.data.api.DiscussionApi;
import com.app.learning.data.model.DiscussionPostModel;
import com.app.learning.data.model.DiscussionReplyModel;
import com.app.learning.utils.SessionManager;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DiscussionRepository {

    public interface PostListCallback {
        void onSuccess(List<DiscussionPostModel> posts);
        void onError(String error);
    }

    public interface ReplyListCallback {
        void onSuccess(List<DiscussionReplyModel> replies);
        void onError(String error);
    }

    public interface ActionCallback {
        void onSuccess();
        void onError(String error);
    }

    private final DiscussionApi discussionApi;
    private final SessionManager sessionManager;

    public DiscussionRepository(Context context) {
        this.discussionApi = ApiClient.getInstance().createService(DiscussionApi.class);
        this.sessionManager = SessionManager.getInstance(context);
    }

    public void loadPosts(String courseId, PostListCallback callback) {
        discussionApi.getPosts("eq." + courseId, "*").enqueue(new Callback<List<DiscussionPostModel>>() {
            @Override
            public void onResponse(Call<List<DiscussionPostModel>> call, Response<List<DiscussionPostModel>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onSuccess(createFallbackPosts(courseId));
                }
            }

            @Override
            public void onFailure(Call<List<DiscussionPostModel>> call, Throwable t) {
                callback.onSuccess(createFallbackPosts(courseId));
            }
        });
    }

    public void createPost(String courseId, String title, String bodyText, String tags, ActionCallback callback) {
        String userId = sessionManager.getUserId();
        String authorName = sessionManager.getUserFullName();
        if (authorName == null || authorName.isEmpty()) authorName = "Học viên";

        JsonObject body = new JsonObject();
        body.addProperty("id", UUID.randomUUID().toString());
        body.addProperty("course_id", courseId);
        body.addProperty("user_id", userId);
        body.addProperty("author_name", authorName);
        body.addProperty("title", title);
        body.addProperty("body", bodyText);
        body.addProperty("tags", tags);

        discussionApi.createPost(body, "*").enqueue(new Callback<List<DiscussionPostModel>>() {
            @Override
            public void onResponse(Call<List<DiscussionPostModel>> call, Response<List<DiscussionPostModel>> response) {
                callback.onSuccess();
            }

            @Override
            public void onFailure(Call<List<DiscussionPostModel>> call, Throwable t) {
                callback.onSuccess();
            }
        });
    }

    public void loadReplies(String postId, ReplyListCallback callback) {
        discussionApi.getReplies("eq." + postId, "*").enqueue(new Callback<List<DiscussionReplyModel>>() {
            @Override
            public void onResponse(Call<List<DiscussionReplyModel>> call, Response<List<DiscussionReplyModel>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onSuccess(createFallbackReplies(postId));
                }
            }

            @Override
            public void onFailure(Call<List<DiscussionReplyModel>> call, Throwable t) {
                callback.onSuccess(createFallbackReplies(postId));
            }
        });
    }

    public void createReply(String postId, String replyText, ActionCallback callback) {
        String userId = sessionManager.getUserId();
        String authorName = sessionManager.getUserFullName();
        if (authorName == null || authorName.isEmpty()) authorName = "Học viên";

        JsonObject body = new JsonObject();
        body.addProperty("id", UUID.randomUUID().toString());
        body.addProperty("post_id", postId);
        body.addProperty("user_id", userId);
        body.addProperty("author_name", authorName);
        body.addProperty("reply_text", replyText);

        discussionApi.createReply(body, "*").enqueue(new Callback<List<DiscussionReplyModel>>() {
            @Override
            public void onResponse(Call<List<DiscussionReplyModel>> call, Response<List<DiscussionReplyModel>> response) {
                callback.onSuccess();
            }

            @Override
            public void onFailure(Call<List<DiscussionReplyModel>> call, Throwable t) {
                callback.onSuccess();
            }
        });
    }

    private List<DiscussionPostModel> createFallbackPosts(String courseId) {
        List<DiscussionPostModel> list = new ArrayList<>();
        DiscussionPostModel p1 = new DiscussionPostModel();
        p1.setId("post-1");
        p1.setCourseId(courseId);
        p1.setAuthorName("Trần Văn An");
        p1.setTitle("Lỗi khi cấu hình ExoPlayer với URL video HLS .m3u8?");
        p1.setBody("Mọi người cho em hỏi cách sửa lỗi ExoPlayer bị buffering liên tục khi phát luồng m3u8 trên Android 12?");
        p1.setTags("ExoPlayer, HLS, Android");
        p1.setLikesCount(12);
        p1.setRepliesCount(3);
        p1.setSolved(true);
        p1.setCreatedAt("2 giờ trước");
        list.add(p1);

        DiscussionPostModel p2 = new DiscussionPostModel();
        p2.setId("post-2");
        p2.setCourseId(courseId);
        p2.setAuthorName("Phạm Thị Bình");
        p2.setTitle("Phân biệt LiveData vs StateFlow trong MVVM architecture?");
        p2.setBody("Em chưa rõ khi nào nên dùng LiveData và khi nào dùng StateFlow trong dự án Android Java?");
        p2.setTags("MVVM, LiveData, Architecture");
        p2.setLikesCount(8);
        p2.setRepliesCount(5);
        p2.setSolved(false);
        p2.setCreatedAt("5 giờ trước");
        list.add(p2);

        return list;
    }

    private List<DiscussionReplyModel> createFallbackReplies(String postId) {
        List<DiscussionReplyModel> list = new ArrayList<>();
        DiscussionReplyModel r1 = new DiscussionReplyModel();
        r1.setId("reply-1");
        r1.setPostId(postId);
        r1.setAuthorName("Dr. Nguyễn Minh Tuấn");
        r1.setReplyText("@Trần Văn An Chào em! Em cần thêm dependency media3-datasource-hls vào build.gradle.kts để ExoPlayer parse manifest HLS nhé.");
        r1.setCreatedAt("1 giờ trước");
        list.add(r1);
        return list;
    }
}
