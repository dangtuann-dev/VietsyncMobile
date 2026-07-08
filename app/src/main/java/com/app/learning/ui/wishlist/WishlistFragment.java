package com.app.learning.ui.wishlist;

import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.app.learning.data.api.Resource;
import com.app.learning.data.model.Course;
import com.app.learning.data.model.WishlistModel;
import com.app.learning.ui.base.BaseFragment;
import com.example.vietsyncmobile.R;
import java.util.List;

public class WishlistFragment extends BaseFragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView rvWishlist;
    private View layoutEmptyState;
    private ProgressBar progressBar;
    private WishlistAdapter adapter;
    private WishlistViewModel viewModel;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_wishlist;
    }

    @Override
    protected void initViews(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        rvWishlist = view.findViewById(R.id.rv_wishlist);
        layoutEmptyState = view.findViewById(R.id.layout_empty_state);
        progressBar = view.findViewById(R.id.progress_bar);

        androidx.appcompat.widget.Toolbar toolbar = view.findViewById(R.id.toolbar);
        if (toolbar != null && baseActivity != null) {
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
            toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());
        }

        rvWishlist.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new WishlistAdapter(new WishlistAdapter.OnWishlistItemClickListener() {
            @Override
            public void onItemClick(Course course) {
                openCourseDetail(course);
            }

            @Override
            public void onEnrollClick(Course course) {
                viewModel.enrollFromWishlist(course.getId());
            }
        });
        rvWishlist.setAdapter(adapter);

        setupSwipeToDismiss();

        swipeRefreshLayout.setOnRefreshListener(() -> viewModel.loadWishlists());
    }

    @Override
    protected void initObservers() {
        viewModel = new ViewModelProvider(requireActivity()).get(WishlistViewModel.class);

        viewModel.getWishlistLiveData().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case LOADING:
                        if (!swipeRefreshLayout.isRefreshing()) {
                            progressBar.setVisibility(View.VISIBLE);
                        }
                        layoutEmptyState.setVisibility(View.GONE);
                        break;
                    case SUCCESS:
                        progressBar.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                        List<WishlistModel> data = resource.data;
                        if (data == null || data.isEmpty()) {
                            adapter.setItems(null);
                            layoutEmptyState.setVisibility(View.VISIBLE);
                            rvWishlist.setVisibility(View.GONE);
                        } else {
                            adapter.setItems(data);
                            layoutEmptyState.setVisibility(View.GONE);
                            rvWishlist.setVisibility(View.VISIBLE);
                        }
                        break;
                    case ERROR:
                        progressBar.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                        showError(resource.error != null ? resource.error.getMessage() : "Lỗi không xác định");
                        break;
                }
            }
        });

        viewModel.getActionResultLiveData().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case LOADING:
                        showLoading();
                        break;
                    case SUCCESS:
                        hideLoading();
                        showToast("Đã cập nhật!");
                        break;
                    case ERROR:
                        hideLoading();
                        showError(resource.error != null ? resource.error.getMessage() : "Thao tác thất bại");
                        break;
                }
            }
        });

        viewModel.loadWishlists();
    }

    private void setupSwipeToDismiss() {
        ItemTouchHelper.SimpleCallback swipeCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            private final ColorDrawable background = new ColorDrawable(ContextCompat.getColor(requireContext(), R.color.error));

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                List<WishlistModel> items = adapter.getItems();
                if (position >= 0 && position < items.size()) {
                    WishlistModel item = items.get(position);
                    viewModel.removeFromWishlist(item.getCourseId());
                }
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;

                if (dX < 0) {
                    background.setBounds(
                            itemView.getRight() + (int) dX,
                            itemView.getTop(),
                            itemView.getRight(),
                            itemView.getBottom()
                    );
                    background.draw(c);
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        new ItemTouchHelper(swipeCallback).attachToRecyclerView(rvWishlist);
    }

    private void openCourseDetail(Course course) {
        try {
            android.content.Intent intent = new android.content.Intent(requireActivity(), Class.forName("com.app.learning.ui.course.CourseDetailActivity"));
            intent.putExtra("course", course);
            intent.putExtra("course_id", course.getId());
            startActivity(intent);
        } catch (ClassNotFoundException e) {
            Toast.makeText(requireContext(), "Đang mở khóa học: " + course.getTitle(), Toast.LENGTH_SHORT).show();
        }
    }
}
