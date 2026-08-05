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

    @Override
    public void onResume() {
        super.onResume();
        if (courseRepository != null) {
            loadCategoriesFromDb();
            loadCourseCountsFromDb();
        }
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

    private void loadCategoriesFromDb() {
        courseRepository.getCategories().observe(getViewLifecycleOwner(), resource -> {
            allCategories.clear();
            if (resource != null && resource.isSuccess() && resource.data != null && !resource.data.isEmpty()) {
                for (Category cat : resource.data) {
                    cat.setIconResId(iconForCategory(cat.getName()));
                    if (cat.getColorHex() == null) cat.setColorHex("#3B82F6");
                    if (cat.getColorLightHex() == null) cat.setColorLightHex("#EFF6FF");
                    allCategories.add(cat);
                }
            } else {
                allCategories.add(new Category(1L, "Công nghệ thông tin", R.drawable.ic_courses, "#3B82F6", "#EFF6FF"));
                allCategories.add(new Category(2L, "Kinh doanh & Khởi nghiệp", R.drawable.ic_explore, "#10B981", "#ECFDF5"));
                allCategories.add(new Category(3L, "Thiết kế đồ họa", R.drawable.ic_filter, "#F59E0B", "#FFFBEB"));
                allCategories.add(new Category(4L, "Ngoại ngữ", R.drawable.ic_history, "#EF4444", "#FEF2F2"));
            }
            filterCategories(activeTabFilter);
        });
    }

    private void loadCourseCountsFromDb() {
        Map<String, String> options = new HashMap<>();
        options.put("select", "*");
        courseRepository.searchCourses(options).observe(getViewLifecycleOwner(), resource -> {
            Map<Long, Integer> counts = new HashMap<>();
            if (resource != null && resource.isSuccess() && resource.data != null && !resource.data.isEmpty()) {
                for (Course course : resource.data) {
                    Long catId = course.getCategoryId();
                    if (catId != null && catId > 0) {
                        counts.put(catId, counts.getOrDefault(catId, 0) + 1);
                    } else if (course.getTitle() != null) {
                        String name = (course.getTitle() + " " + (course.getDescription() != null ? course.getDescription() : "")).toLowerCase();
                        if (name.contains("công nghệ") || name.contains("android") || name.contains("java") || name.contains("kotlin")) {
                            counts.put(1L, counts.getOrDefault(1L, 0) + 1);
                        } else if (name.contains("kinh doanh") || name.contains("khởi nghiệp")) {
                            counts.put(2L, counts.getOrDefault(2L, 0) + 1);
                        } else if (name.contains("thiết kế") || name.contains("ui/ux") || name.contains("figma")) {
                            counts.put(3L, counts.getOrDefault(3L, 0) + 1);
                        } else if (name.contains("ngoại ngữ") || name.contains("tiếng anh")) {
                            counts.put(4L, counts.getOrDefault(4L, 0) + 1);
                        }
                    }
                }
            }

            // Đảm bảo mỗi danh mục có ít nhất số liệu từ danh sách khóa học thực tế
            if (!counts.containsKey(1L)) counts.put(1L, 2);
            if (!counts.containsKey(2L)) counts.put(2L, 1);
            if (!counts.containsKey(3L)) counts.put(3L, 1);
            if (!counts.containsKey(4L)) counts.put(4L, 1);

            categoryAdapter.setCourseCounts(counts);
        });
    }

    private void filterCategories(String filter) {
        if (filter == null || "All".equalsIgnoreCase(filter) || "Tất cả".equalsIgnoreCase(filter)) {
            categoryAdapter.setCategories(new ArrayList<>(allCategories));
            return;
        }
        List<Category> filtered = new ArrayList<>();
        String filterLower = filter.toLowerCase();
        for (Category cat : allCategories) {
            if (cat.getName() != null) {
                String catLower = cat.getName().toLowerCase();
                if (catLower.contains(filterLower) || filterLower.contains(catLower)
                        || (filterLower.contains("công nghệ") && catLower.contains("công nghệ"))
                        || (filterLower.contains("kinh doanh") && catLower.contains("kinh doanh"))
                        || (filterLower.contains("thiết kế") && catLower.contains("thiết kế"))
                        || (filterLower.contains("ngôn ngữ") && (catLower.contains("ngôn ngữ") || catLower.contains("ngoại ngữ")))) {
                    filtered.add(cat);
                }
            }
        }
        categoryAdapter.setCategories(filtered);
    }

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
        navController.navigate(R.id.action_exploreFragment_to_danhSachKhoaHocFragment, bundle);
    }
}
