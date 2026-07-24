package com.app.learning.ui.discussion;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vietsyncmobile.R;
import com.google.android.material.appbar.MaterialToolbar;

public class PostDetailActivity extends AppCompatActivity {

    private MaterialToolbar toolbar;
    private TextView tvPostTitle, tvAuthorName, tvPostBody;
    private RecyclerView rvReplies;
    private AutoCompleteTextView actvReply;
    private ImageButton btnSendReply;

    private DiscussionViewModel viewModel;
    private ReplyAdapter adapter;
    private String postId;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        toolbar = findViewById(R.id.toolbar);
        tvPostTitle = findViewById(R.id.tvPostTitle);
        tvAuthorName = findViewById(R.id.tvAuthorName);
        tvPostBody = findViewById(R.id.tvPostBody);
        rvReplies = findViewById(R.id.rvReplies);
        actvReply = findViewById(R.id.actvReply);
        btnSendReply = findViewById(R.id.btnSendReply);

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        postId = getIntent().getStringExtra("post_id");
        String title = getIntent().getStringExtra("post_title");
        String body = getIntent().getStringExtra("post_body");
        String author = getIntent().getStringExtra("author_name");

        if (postId == null) postId = "post-1";

        tvPostTitle.setText(title != null ? title : "Chi tiết thảo luận");
        tvAuthorName.setText("Đăng bởi " + (author != null ? author : "Học viên"));
        tvPostBody.setText(body != null ? body : "");

        viewModel = new ViewModelProvider(this).get(DiscussionViewModel.class);

        adapter = new ReplyAdapter();
        rvReplies.setLayoutManager(new LinearLayoutManager(this));
        rvReplies.setAdapter(adapter);

        // AutoComplete mention list
        String[] mentionUsers = new String[]{"@Trần Văn An", "@Phạm Thị Bình", "@Dr. Nguyễn Minh Tuấn", "@Prof. Lê Thị Hoa"};
        ArrayAdapter<String> mentionAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, mentionUsers);
        actvReply.setAdapter(mentionAdapter);
        actvReply.setThreshold(1);

        viewModel.getReplies().observe(this, replies -> adapter.setReplies(replies));

        btnSendReply.setOnClickListener(v -> {
            String text = actvReply.getText() != null ? actvReply.getText().toString().trim() : "";
            if (!text.isEmpty()) {
                viewModel.createReply(postId, text);
                actvReply.setText("");
            }
        });

        viewModel.loadReplies(postId);
    }
}
