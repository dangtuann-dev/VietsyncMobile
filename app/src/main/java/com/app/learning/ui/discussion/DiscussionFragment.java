package com.app.learning.ui.discussion;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vietsyncmobile.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DiscussionFragment extends Fragment {

    private RecyclerView rvPosts;
    private ProgressBar progressBar;
    private Spinner spinnerSort;
    private FloatingActionButton fabNewPost;

    private DiscussionAdapter adapter;
    private DiscussionViewModel viewModel;
    private String courseId;

    public static DiscussionFragment newInstance(String courseId) {
        DiscussionFragment fragment = new DiscussionFragment();
        Bundle args = new Bundle();
        args.putString("course_id", courseId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_discussion, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvPosts = view.findViewById(R.id.rvPosts);
        progressBar = view.findViewById(R.id.progressBar);
        spinnerSort = view.findViewById(R.id.spinnerSort);
        fabNewPost = view.findViewById(R.id.fabNewPost);

        courseId = getArguments() != null ? getArguments().getString("course_id") : "c0eebc99-9c0b-4ef8-bb6d-6bb9bd380001";

        viewModel = new ViewModelProvider(this).get(DiscussionViewModel.class);

        adapter = new DiscussionAdapter(post -> {
            Intent intent = new Intent(requireContext(), PostDetailActivity.class);
            intent.putExtra("post_id", post.getId());
            intent.putExtra("post_title", post.getTitle());
            intent.putExtra("post_body", post.getBody());
            intent.putExtra("author_name", post.getAuthorName());
            startActivity(intent);
        });

        rvPosts.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvPosts.setAdapter(adapter);

        ArrayAdapter<String> sortAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, new String[]{"Mới nhất", "Nổi bật", "Chưa trả lời"});
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSort.setAdapter(sortAdapter);

        viewModel.getPosts().observe(getViewLifecycleOwner(), posts -> adapter.setPosts(posts));
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), loading -> progressBar.setVisibility(loading ? View.VISIBLE : View.GONE));

        fabNewPost.setOnClickListener(v -> {
            NewPostBottomSheet bottomSheet = new NewPostBottomSheet((title, body, tags) -> {
                viewModel.createPost(courseId, title, body, tags);
            });
            bottomSheet.show(getChildFragmentManager(), "new_post");
        });

        viewModel.loadPosts(courseId);
    }
}
