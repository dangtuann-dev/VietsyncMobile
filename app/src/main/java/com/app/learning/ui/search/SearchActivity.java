package com.app.learning.ui.search;

import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.learning.data.api.Resource;
import com.app.learning.data.local.SearchHistory;
import com.app.learning.data.model.Course;
import com.app.learning.ui.base.BaseActivity;
import com.example.vietsyncmobile.R;

import java.util.List;

public class SearchActivity extends BaseActivity {

    private ImageView ivBack;
    private SearchView searchView;
    private ImageView ivFilter;
    private RecyclerView rvSearchHistory;
    private RecyclerView rvSearchResults;
    private ProgressBar progressBar;
    private View layoutEmptyState;
    private TextView tvEmptyTitle;
    private TextView tvEmptyDesc;

    private SearchViewModel viewModel;
    private SearchHistoryAdapter historyAdapter;
    private SearchResultAdapter resultAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    protected void initViews() {
        ivBack = findViewById(R.id.iv_back);
        searchView = findViewById(R.id.search_view);
        ivFilter = findViewById(R.id.iv_filter);
        rvSearchHistory = findViewById(R.id.rv_search_history);
        rvSearchResults = findViewById(R.id.rv_search_results);
        progressBar = findViewById(R.id.progress_bar);
        layoutEmptyState = findViewById(R.id.layout_empty_state);
        tvEmptyTitle = findViewById(R.id.tv_empty_title);
        tvEmptyDesc = findViewById(R.id.tv_empty_desc);

        // Remove default search view underline
        View searchPlate = searchView.findViewById(androidx.appcompat.R.id.search_plate);
        if (searchPlate != null) {
            searchPlate.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        }

        setupRecyclerViews();

        // Instantiate ViewModel
        viewModel = new ViewModelProvider(this, new SearchViewModel.Factory(this)).get(SearchViewModel.class);

        // Click listeners
        ivBack.setOnClickListener(v -> finish());
        ivFilter.setOnClickListener(v -> openFilterBottomSheet());

        setupSearchView();
    }

    private void setupRecyclerViews() {
        // Search History / Suggestions
        rvSearchHistory.setLayoutManager(new LinearLayoutManager(this));
        historyAdapter = new SearchHistoryAdapter(new SearchHistoryAdapter.OnHistoryClickListener() {
            @Override
            public void onItemClick(String query) {
                searchView.setQuery(query, true);
            }

            @Override
            public void onDeleteClick(SearchHistory searchHistory) {
                viewModel.deleteHistoryItem(searchHistory);
            }

            @Override
            public void onClearAllClick() {
                viewModel.clearHistory();
            }
        });
        rvSearchHistory.setAdapter(historyAdapter);

        // Search Results
        rvSearchResults.setLayoutManager(new LinearLayoutManager(this));
        resultAdapter = new SearchResultAdapter(course -> {
            Toast.makeText(SearchActivity.this, "Đang mở khóa học: " + course.getTitle(), Toast.LENGTH_SHORT).show();
            // Implement detail navigation here if needed
        });
        rvSearchResults.setAdapter(resultAdapter);
    }

    private void setupSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query != null && !query.trim().isEmpty()) {
                    viewModel.saveQueryToHistory(query);
                    viewModel.searchInstantly(query);
                    searchView.clearFocus(); // Hide keyboard
                    return true;
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                viewModel.search(newText);
                updateLayoutState(newText, viewModel.getSearchResults().getValue());
                return true;
            }
        });
    }

    @Override
    protected void initObservers() {
        observeViewModel(viewModel);

        // Observe search results
        viewModel.getSearchResults().observe(this, resource -> {
            String query = searchView.getQuery().toString();
            updateLayoutState(query, resource);
        });

        // Observe local search history
        viewModel.getSearchHistory().observe(this, historyList -> {
            if (historyList != null) {
                historyAdapter.setHistoryList(historyList);
            }
        });
    }

    private void updateLayoutState(String query, Resource<List<Course>> resultResource) {
        if (query == null || query.trim().isEmpty()) {
            rvSearchResults.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);
            layoutEmptyState.setVisibility(View.GONE);
            rvSearchHistory.setVisibility(View.VISIBLE);
        } else {
            rvSearchHistory.setVisibility(View.GONE);
            if (resultResource == null) {
                progressBar.setVisibility(View.GONE);
                rvSearchResults.setVisibility(View.GONE);
                layoutEmptyState.setVisibility(View.GONE);
            } else if (resultResource.isLoading()) {
                progressBar.setVisibility(View.VISIBLE);
                rvSearchResults.setVisibility(View.GONE);
                layoutEmptyState.setVisibility(View.GONE);
            } else if (resultResource.isSuccess()) {
                progressBar.setVisibility(View.GONE);
                if (resultResource.data == null || resultResource.data.isEmpty()) {
                    tvEmptyTitle.setText("Không tìm thấy kết quả");
                    tvEmptyDesc.setText("Không tìm thấy khóa học nào phù hợp với từ khóa \"" + query + "\"");
                    layoutEmptyState.setVisibility(View.VISIBLE);
                    rvSearchResults.setVisibility(View.GONE);
                } else {
                    layoutEmptyState.setVisibility(View.GONE);
                    rvSearchResults.setVisibility(View.VISIBLE);
                    resultAdapter.setCourseList(resultResource.data);
                }
            } else if (resultResource.isError()) {
                progressBar.setVisibility(View.GONE);
                tvEmptyTitle.setText("Lỗi tìm kiếm");
                tvEmptyDesc.setText(resourceErrorText(resultResource));
                layoutEmptyState.setVisibility(View.VISIBLE);
                rvSearchResults.setVisibility(View.GONE);
            }
        }
    }

    private String resourceErrorText(Resource<?> resource) {
        if (resource != null && resource.error != null && resource.error.getMessage() != null) {
            return resource.error.getMessage();
        }
        return "Đã xảy ra lỗi khi tải kết quả tìm kiếm. Vui lòng thử lại.";
    }

    private void openFilterBottomSheet() {
        FilterBottomSheet bottomSheet = new FilterBottomSheet();
        bottomSheet.show(getSupportFragmentManager(), "FilterBottomSheet");
    }
}
