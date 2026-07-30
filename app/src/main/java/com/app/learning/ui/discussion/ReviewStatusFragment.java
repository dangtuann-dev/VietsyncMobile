package com.app.learning.ui.discussion;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vietsyncmobile.R;
import com.app.learning.data.repository.PeerReviewRepository;
import com.google.android.material.button.MaterialButton;

public class ReviewStatusFragment extends Fragment {

    private TextView tvStatusHeader, tvStatusDetail;
    private MaterialButton btnGoSubmit, btnGoReviewPeer;
    private RecyclerView rvReceivedReviews;
    private PeerReviewAdapter adapter;

    private PeerReviewRepository repository;
    private String submissionId = "sub_101";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_review_status, container, false);
        initViews(v);
        loadReviews();
        return v;
    }

    private void initViews(View v) {
        tvStatusHeader = v.findViewById(R.id.tvStatusHeader);
        tvStatusDetail = v.findViewById(R.id.tvStatusDetail);
        btnGoSubmit = v.findViewById(R.id.btnGoSubmit);
        btnGoReviewPeer = v.findViewById(R.id.btnGoReviewPeer);
        rvReceivedReviews = v.findViewById(R.id.rvReceivedReviews);

        repository = new PeerReviewRepository(requireContext());

        rvReceivedReviews.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new PeerReviewAdapter();
        rvReceivedReviews.setAdapter(adapter);

        btnGoSubmit.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), AssignmentSubmitActivity.class);
            startActivity(intent);
        });

        btnGoReviewPeer.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), PeerReviewActivity.class);
            startActivity(intent);
        });
    }

    private void loadReviews() {
        repository.getReceivedReviews(submissionId, new PeerReviewRepository.RepositoryCallback<java.util.List<com.app.learning.data.model.PeerReviewModel>>() {
            @Override
            public void onSuccess(java.util.List<com.app.learning.data.model.PeerReviewModel> data) {
                adapter.setReviews(data);
                int count = data != null ? data.size() : 0;
                tvStatusHeader.setText("Đã nhận " + count + "/3 Đánh Giá");
            }

            @Override
            public void onError(String message) {
                tvStatusDetail.setText("Lỗi tải nhận xét: " + message);
            }
        });
    }
}
