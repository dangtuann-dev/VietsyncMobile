package com.app.learning.ui.search;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.app.learning.data.api.Resource;
import com.app.learning.data.local.AppDatabase;
import com.app.learning.data.local.SearchHistory;
import com.app.learning.data.local.SearchHistoryDao;
import com.app.learning.data.model.Category;
import com.app.learning.data.model.Course;
import com.app.learning.data.repository.CourseRepository;
import com.app.learning.ui.base.BaseViewModel;
import com.app.learning.utils.AppExecutors;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchViewModel extends BaseViewModel {

    private final CourseRepository courseRepository;
    private final SearchHistoryDao searchHistoryDao;
    private final AppExecutors executors;

    private final MediatorLiveData<Resource<List<Course>>> searchResults = new MediatorLiveData<>();
    private final MediatorLiveData<Resource<List<Category>>> categories = new MediatorLiveData<>();

    // Current query and filters
    private String currentQuery = "";
    private Long selectedCategoryId = null;
    private String selectedLevel = null;
    private double minPrice = 0.0;
    private double maxPrice = 5000000.0;
    private String selectedDuration = null; // "short", "medium", "long" or null

    // Debounce properties
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    private LiveData<Resource<List<Course>>> searchSource;
    private LiveData<Resource<List<Category>>> categoriesSource;

    public SearchViewModel(@NonNull CourseRepository courseRepository, @NonNull SearchHistoryDao searchHistoryDao) {
        this.courseRepository = courseRepository;
        this.searchHistoryDao = searchHistoryDao;
        this.executors = AppExecutors.getInstance();

        loadCategories();
    }

    public LiveData<Resource<List<Course>>> getSearchResults() {
        return searchResults;
    }

    public LiveData<Resource<List<Category>>> getCategories() {
        return categories;
    }

    public LiveData<List<SearchHistory>> getSearchHistory() {
        return searchHistoryDao.getHistoryLimit(10);
    }

    // --- Search Logic ---

    public void search(String query) {
        this.currentQuery = query;
        debounceSearch();
    }

    public void searchInstantly(String query) {
        this.currentQuery = query;
        if (searchRunnable != null) {
            handler.removeCallbacks(searchRunnable);
        }
        executeSearch();
    }

    private void debounceSearch() {
        if (searchRunnable != null) {
            handler.removeCallbacks(searchRunnable);
        }

        searchRunnable = this::executeSearch;
        handler.postDelayed(searchRunnable, 300);
    }

    public void executeSearch() {
        if (searchSource != null) {
            searchResults.removeSource(searchSource);
        }

        Map<String, String> options = new HashMap<>();
        options.put("select", "*,instructor:users(full_name)");

        if (currentQuery != null && !currentQuery.trim().isEmpty()) {
            options.put("title", "ilike.*" + currentQuery.trim() + "*");
        }

        if (selectedCategoryId != null) {
            options.put("category_id", "eq." + selectedCategoryId);
        }

        if (selectedLevel != null) {
            options.put("level", "eq." + selectedLevel);
        }

        // Apply Price filter in Supabase: price=and(gte.min,lte.max)
        options.put("price", "and(gte." + minPrice + ",lte." + maxPrice + ")");

        if (selectedDuration != null) {
            if ("short".equals(selectedDuration)) {
                options.put("duration", "lt.10");
            } else if ("medium".equals(selectedDuration)) {
                options.put("duration", "and(gte.10,lte.30)");
            } else if ("long".equals(selectedDuration)) {
                options.put("duration", "gt.30");
            }
        }

        searchResults.setValue(Resource.loading());

        searchSource = courseRepository.searchCourses(options);
        searchResults.addSource(searchSource, resource -> {
            if (resource != null) {
                searchResults.setValue(resource);
                if (!resource.isLoading()) {
                    searchResults.removeSource(searchSource);
                    searchSource = null;
                }
            }
        });
    }

    // --- Filters ---

    public void setFilters(Long categoryId, String level, double minPrice, double maxPrice, String duration) {
        this.selectedCategoryId = categoryId;
        this.selectedLevel = level;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.selectedDuration = duration;

        // Apply immediately
        executeSearch();
    }

    public void resetFilters() {
        this.selectedCategoryId = null;
        this.selectedLevel = null;
        this.minPrice = 0.0;
        this.maxPrice = 5000000.0;
        this.selectedDuration = null;

        // Apply immediately
        executeSearch();
    }

    public Long getSelectedCategoryId() {
        return selectedCategoryId;
    }

    public String getSelectedLevel() {
        return selectedLevel;
    }

    public double getMinPrice() {
        return minPrice;
    }

    public double getMaxPrice() {
        return maxPrice;
    }

    public String getSelectedDuration() {
        return selectedDuration;
    }

    public String getCurrentQuery() {
        return currentQuery;
    }

    // --- Categories loading ---

    private void loadCategories() {
        if (categoriesSource != null) {
            categories.removeSource(categoriesSource);
        }

        categories.setValue(Resource.loading());
        categoriesSource = courseRepository.getCategories();
        categories.addSource(categoriesSource, resource -> {
            if (resource != null) {
                categories.setValue(resource);
                if (!resource.isLoading()) {
                    categories.removeSource(categoriesSource);
                    categoriesSource = null;
                }
            }
        });
    }

    // --- Local DB operations ---

    public void saveQueryToHistory(String query) {
        if (query == null || query.trim().isEmpty()) return;

        executors.diskIO().execute(() -> {
            SearchHistory history = new SearchHistory(query.trim(), System.currentTimeMillis());
            searchHistoryDao.insert(history);
        });
    }

    public void deleteHistoryItem(SearchHistory item) {
        if (item == null) return;
        executors.diskIO().execute(() -> searchHistoryDao.delete(item));
    }

    public void clearHistory() {
        executors.diskIO().execute(searchHistoryDao::clearAll);
    }

    @Override
    protected void onCleared() {
        if (searchRunnable != null) {
            handler.removeCallbacks(searchRunnable);
        }
        super.onCleared();
    }

    // --- Factory ---

    public static class Factory implements ViewModelProvider.Factory {
        private final Context context;

        public Factory(@NonNull Context context) {
            this.context = context.getApplicationContext();
        }

        @NonNull
        @Override
        @SuppressWarnings("unchecked")
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            if (modelClass.isAssignableFrom(SearchViewModel.class)) {
                CourseRepository repo = new CourseRepository(context);
                SearchHistoryDao dao = AppDatabase.getInstance(context).searchHistoryDao();
                return (T) new SearchViewModel(repo, dao);
            }
            throw new IllegalArgumentException("Unknown ViewModel class: " + modelClass.getName());
        }
    }
}
