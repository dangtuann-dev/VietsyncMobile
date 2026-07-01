package com.app.learning.ui.search;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.app.learning.data.model.Category;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.slider.RangeSlider;
import com.example.vietsyncmobile.R;

import java.util.ArrayList;
import java.util.List;

public class FilterBottomSheet extends BottomSheetDialogFragment {

    private ChipGroup cgCategories;
    private ChipGroup cgLevels;
    private RangeSlider sliderPrice;
    private ChipGroup cgDurations;
    private TextView tvPriceRange;
    private MaterialButton btnApply;
    private TextView tvReset;

    private SearchViewModel viewModel;
    private List<Category> availableCategories = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_filter, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Bind Views
        cgCategories = view.findViewById(R.id.cg_categories);
        cgLevels = view.findViewById(R.id.cg_levels);
        sliderPrice = view.findViewById(R.id.slider_price);
        cgDurations = view.findViewById(R.id.cg_durations);
        tvPriceRange = view.findViewById(R.id.tv_price_range);
        btnApply = view.findViewById(R.id.btn_apply);
        tvReset = view.findViewById(R.id.tv_reset);

        // Setup Shared ViewModel (share same instance as SearchActivity via activity scope + factory)
        viewModel = new ViewModelProvider(requireActivity(), new SearchViewModel.Factory(requireContext()))
                .get(SearchViewModel.class);

        setupPriceSlider();
        setupObservers();

        btnApply.setOnClickListener(v -> applyFilters());
        tvReset.setOnClickListener(v -> resetFilters());
    }

    private void setupPriceSlider() {
        sliderPrice.addOnChangeListener((slider, value, fromUser) -> {
            List<Float> values = slider.getValues();
            if (values.size() >= 2) {
                float min = values.get(0);
                float max = values.get(1);
                tvPriceRange.setText(String.format("%,.0fđ - %,.0fđ", min, max));
            }
        });
    }

    private void setupObservers() {
        // Observe categories list
        viewModel.getCategories().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null && resource.isSuccess() && resource.data != null) {
                availableCategories = resource.data;
                populateCategoryChips();
            }
        });

        // Initialize level chip
        String level = viewModel.getSelectedLevel();
        if ("beginner".equals(level)) {
            cgLevels.check(R.id.chip_level_beginner);
        } else if ("intermediate".equals(level)) {
            cgLevels.check(R.id.chip_level_intermediate);
        } else if ("advanced".equals(level)) {
            cgLevels.check(R.id.chip_level_advanced);
        }

        // Initialize price slider
        sliderPrice.setValues((float) viewModel.getMinPrice(), (float) viewModel.getMaxPrice());
        tvPriceRange.setText(String.format("%,.0fđ - %,.0fđ", viewModel.getMinPrice(), viewModel.getMaxPrice()));

        // Initialize duration chip
        String duration = viewModel.getSelectedDuration();
        if ("short".equals(duration)) {
            cgDurations.check(R.id.chip_duration_short);
        } else if ("medium".equals(duration)) {
            cgDurations.check(R.id.chip_duration_medium);
        } else if ("long".equals(duration)) {
            cgDurations.check(R.id.chip_duration_long);
        }
    }

    private void populateCategoryChips() {
        cgCategories.removeAllViews();
        Long selectedCategoryId = viewModel.getSelectedCategoryId();

        for (Category category : availableCategories) {
            Chip chip = new Chip(requireContext());
            chip.setId(category.getId().intValue());
            chip.setText(category.getName());
            chip.setCheckable(true);
            
            // Set material filter chip style programmatically or fallback to default
            chip.setChipBackgroundColorResource(android.R.color.transparent);
            chip.setChipStrokeColorResource(R.color.surface_variant);
            chip.setChipStrokeWidth(1.0f);
            
            if (category.getId().equals(selectedCategoryId)) {
                chip.setChecked(true);
            }
            cgCategories.addView(chip);
        }
    }

    private void applyFilters() {
        // 1. Get Category
        int checkedCategoryId = cgCategories.getCheckedChipId();
        Long categoryId = checkedCategoryId == View.NO_ID ? null : (long) checkedCategoryId;

        // 2. Get Level
        int checkedLevelId = cgLevels.getCheckedChipId();
        String level = null;
        if (checkedLevelId == R.id.chip_level_beginner) {
            level = "beginner";
        } else if (checkedLevelId == R.id.chip_level_intermediate) {
            level = "intermediate";
        } else if (checkedLevelId == R.id.chip_level_advanced) {
            level = "advanced";
        }

        // 3. Get Price
        List<Float> priceValues = sliderPrice.getValues();
        double min = priceValues.get(0);
        double max = priceValues.get(1);

        // 4. Get Duration
        int checkedDurationId = cgDurations.getCheckedChipId();
        String duration = null;
        if (checkedDurationId == R.id.chip_duration_short) {
            duration = "short";
        } else if (checkedDurationId == R.id.chip_duration_medium) {
            duration = "medium";
        } else if (checkedDurationId == R.id.chip_duration_long) {
            duration = "long";
        }

        viewModel.setFilters(categoryId, level, min, max, duration);
        dismiss();
    }

    private void resetFilters() {
        viewModel.resetFilters();
        dismiss();
    }
}
