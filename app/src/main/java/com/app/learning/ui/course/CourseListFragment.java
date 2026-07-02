package com.app.learning.ui.course;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.vietsyncmobile.R;
import com.app.learning.ui.home.CourseAdapter;

import java.util.ArrayList;

public class CourseListFragment extends Fragment {

    private Toolbar toolbar;
    private Spinner sortSpinner;
    private RecyclerView coursesRecyclerView;
    private CourseAdapter courseAdapter;
    private CourseListViewModel viewModel;

    // Both received from ExploreFragment navigation bundle
    private String categoryId;
    private String categoryName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.course_list_fragment, container, false);

        toolbar             = view.findViewById(R.id.toolbar);
        sortSpinner         = view.findViewById(R.id.sort_spinner);
        coursesRecyclerView = view.findViewById(R.id.courses_recycler_view);

        viewModel = new ViewModelProvider(this).get(CourseListViewModel.class);

        if (getArguments() != null) {
            categoryId   = getArguments().getString("categoryId", "");
            categoryName = getArguments().getString("categoryName", getString(R.string.home_section_categories));
            toolbar.setTitle(categoryName);
        }

        setupToolbar();
        setupSpinner();
        setupRecyclerView();
        observeCourses();

        return view;
    }

    private void setupToolbar() {
        toolbar.setNavigationOnClickListener(v -> requireActivity().onBackPressed());
    }

    private void setupSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.sort_options,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(adapter);

        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String sortBy = parent.getItemAtPosition(position).toString();
                // Pass categoryId (numeric string) to the ViewModel — not the display name
                viewModel.loadCourses(categoryId, sortBy, 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void setupRecyclerView() {
        coursesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // isHorizontal = false → uses item_course_card layout
        courseAdapter = new CourseAdapter(new ArrayList<>(), false);
        coursesRecyclerView.setAdapter(courseAdapter);
    }

    private void observeCourses() {
        viewModel.getCourses().observe(getViewLifecycleOwner(), courses -> {
            if (courses != null) {
                courseAdapter.setCourses(courses);
            }
        });

        // Default initial sort — triggers Spinner's onItemSelected which calls loadCourses
        // but also kick off explicitly in case spinner fires before observer is attached
        viewModel.loadCourses(categoryId, "Phổ biến", 1);
    }
}
