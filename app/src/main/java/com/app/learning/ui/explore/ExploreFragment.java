package com.app.learning.ui.explore;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.learning.data.api.Resource;
import com.app.learning.data.model.Category;
import com.app.learning.data.model.Course;
import com.app.learning.data.repository.CourseRepository;
import com.example.vietsyncmobile.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExploreFragment extends Fragment implements CategoryGridAdapter.OnCategoryClickListener {

    private TabLayout tabLayout;
    private RecyclerView categoriesRecyclerView;
    private CategoryGridAdapter categoryAdapter;

    // Full list loaded from the database; filtered list shown in the adapter
    private final List<Category> allCategories = new ArrayList<>();
    private String activeTabFilter = "All";

    private CourseRepository courseRepository;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.explore_fragment, container, false);

        tabLayout = view.findViewById(R.id.tabLayout);
        categoriesRecyclerView = view.findViewById(R.id.categories_recycler_view);

        courseRepository = new CourseRepository();

        setupTabLayout();
        setupRecyclerView();
        loadCategoriesFromDb();
        loadCourseCountsFromDb();

        return view;
    }

    private void setupTabLayout() {
        tabLayout.addTab(tabLayout.newTab().setText("Tất cả"));
        tabLayout.addTab(tabLayout.newTab().setText("Công nghệ"));
        tabLayout.addTab(tabLayout.newTab().setText("Kinh doanh"));
        tabLayout.addTab(tabLayout.newTab().setText("Thiết kế"));
        tabLayout.addTab(tabLayout.newTab().setText("Ngôn ngữ"));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                activeTabFilter = tab.getText() != null ? tab.getText().toString() : "All";
                filterCategories(activeTabFilter);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
    }

    private void setupRecyclerView() {
        categoriesRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        categoryAdapter = new CategoryGridAdapter(new ArrayList<>(), this);
        categoriesRecyclerView.setAdapter(categoryAdapter);
    }

    /** Fetch categories from Supabase via CourseRepository and populate the adapter. */
    private void loadCategoriesFromDb() {
        courseRepository.getCategories().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.isSuccess() && resource.data != null) {
                allCategories.clear();
                // Assign icon res-ids based on known category names (DB has no iconResId field)
                for (Category cat : resource.data) {
                    cat.setIconResId(iconForCategory(cat.getName()));
                    if (cat.getColorHex() == null) cat.setColorHex("#3B82F6");
                    if (cat.getColorLightHex() == null) cat.setColorLightHex("#EFF6FF");
                    allCategories.add(cat);
                }
                filterCategories(activeTabFilter);
            }
        });
    }

    /**
     * Fetch all courses with only category_id selected, count occurrences per category,
     * then push the counts map to the adapter so it displays real numbers.
     */
    private void loadCourseCountsFromDb() {
        Map<String, String> options = new HashMap<>();
        options.put("select", "category_id");
        courseRepository.searchCourses(options).observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.isSuccess() && resource.data != null) {
                Map<Long, Integer> counts = new HashMap<>();
                for (Course course : resource.data) {
                    if (course.getCategoryId() != null) {
                        counts.put(course.getCategoryId(),
                                counts.getOrDefault(course.getCategoryId(), 0) + 1);
                    }
                }
                categoryAdapter.setCourseCounts(counts);
            }
        });
    }

    private void filterCategories(String filter) {
        if ("All".equals(filter)) {
            categoryAdapter.setCategories(new ArrayList<>(allCategories));
            return;
        }
        List<Category> filtered = new ArrayList<>();
        for (Category cat : allCategories) {
            if (cat.getName() != null && cat.getName().equalsIgnoreCase(filter)) {
                filtered.add(cat);
            }
        }
        categoryAdapter.setCategories(filtered);
    }

    /** Map known Supabase category names to drawable resource IDs. */
    private int iconForCategory(String name) {
        if (name == null) return R.drawable.ic_explore;
        switch (name.toLowerCase()) {
            case "công nghệ thông tin":  return R.drawable.ic_courses;
            case "kinh doanh & khởi nghiệp": return R.drawable.ic_explore;
            case "thiết kế đồ họa":     return R.drawable.ic_filter;
            case "ngoại ngữ":           return R.drawable.ic_history;
            default:                    return R.drawable.ic_explore;
        }
    }

    @Override
    public void onCategoryClick(Category category) {
        NavController navController = Navigation.findNavController(requireView());
        Bundle bundle = new Bundle();
        bundle.putString("categoryId", category.getId() != null ? String.valueOf(category.getId()) : "");
        bundle.putString("categoryName", category.getName());
        navController.navigate(R.id.action_exploreFragment_to_courseListFragment, bundle);
    }
}
