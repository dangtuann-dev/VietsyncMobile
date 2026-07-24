package com.app.learning.ui.discussion;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.app.learning.data.model.DiscussionPostModel;
import com.app.learning.data.model.DiscussionReplyModel;
import com.app.learning.data.repository.DiscussionRepository;

import java.util.ArrayList;
import java.util.List;

public class DiscussionViewModel extends AndroidViewModel {

    private final DiscussionRepository repository;
    private final MutableLiveData<List<DiscussionPostModel>> posts = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<DiscussionReplyModel>> replies = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public DiscussionViewModel(@NonNull Application application) {
        super(application);
        this.repository = new DiscussionRepository(application);
    }

    public LiveData<List<DiscussionPostModel>> getPosts() { return posts; }
    public LiveData<List<DiscussionReplyModel>> getReplies() { return replies; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }

    public void loadPosts(String courseId) {
        isLoading.setValue(true);
        repository.loadPosts(courseId, new DiscussionRepository.PostListCallback() {
            @Override
            public void onSuccess(List<DiscussionPostModel> list) {
                isLoading.setValue(false);
                posts.setValue(list);
            }

            @Override
            public void onError(String error) {
                isLoading.setValue(false);
            }
        });
    }

    public void loadReplies(String postId) {
        repository.loadReplies(postId, new DiscussionRepository.ReplyListCallback() {
            @Override
            public void onSuccess(List<DiscussionReplyModel> list) {
                replies.setValue(list);
            }

            @Override
            public void onError(String error) {}
        });
    }

    public void createPost(String courseId, String title, String body, String tags) {
        repository.createPost(courseId, title, body, tags, new DiscussionRepository.ActionCallback() {
            @Override
            public void onSuccess() {
                loadPosts(courseId);
            }

            @Override
            public void onError(String error) {}
        });
    }

    public void createReply(String postId, String replyText) {
        repository.createReply(postId, replyText, new DiscussionRepository.ActionCallback() {
            @Override
            public void onSuccess() {
                loadReplies(postId);
            }

            @Override
            public void onError(String error) {}
        });
    }
}
