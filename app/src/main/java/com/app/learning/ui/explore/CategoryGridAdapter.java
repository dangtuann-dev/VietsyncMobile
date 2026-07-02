package com.app.learning.ui.explore;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vietsyncmobile.R;
import com.app.learning.data.model.Category;
import com.google.android.material.card.MaterialCardView;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class CategoryGridAdapter extends RecyclerView.Adapter<CategoryGridAdapter.CategoryViewHolder> {

    private final List<Category> categories;
    private final Map<Long, Integer> courseCounts = new HashMap<>();
    private final OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }

    public CategoryGridAdapter(List<Category> categories, OnCategoryClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    public void setCategories(List<Category> newCategories) {
        this.categories.clear();
        this.categories.addAll(newCategories);
        notifyDataSetChanged();
    }

    public void setCourseCounts(Map<Long, Integer> counts) {
        this.courseCounts.clear();
        if (counts != null) {
            this.courseCounts.putAll(counts);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_grid, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        int count = courseCounts.getOrDefault(category.getId(), 0);
        holder.bind(category, count, listener);
    }

    @Override
    public int getItemCount() {
        return categories != null ? categories.size() : 0;
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardView;
        private final ImageView ivIcon;
        private final TextView tvName;
        private final TextView tvCount;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = (MaterialCardView) itemView;
            ivIcon = itemView.findViewById(R.id.category_icon);
            tvName = itemView.findViewById(R.id.category_name);
            tvCount = itemView.findViewById(R.id.course_count);
        }

        public void bind(Category category, int count, OnCategoryClickListener listener) {
            tvName.setText(category.getName());
            tvCount.setText(itemView.getContext().getString(R.string.category_course_count, count));

            if (category.getIconResId() != 0) {
                ivIcon.setImageResource(category.getIconResId());
            } else {
                ivIcon.setImageResource(R.drawable.ic_explore);
            }

            if (category.getColorLightHex() != null) {
                try {
                    cardView.setCardBackgroundColor(Color.parseColor(category.getColorLightHex()));
                } catch (IllegalArgumentException e) {
                    cardView.setCardBackgroundColor(Color.parseColor("#F5F5F5"));
                }
            } else {
                cardView.setCardBackgroundColor(Color.parseColor("#F5F5F5"));
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCategoryClick(category);
                }
            });
        }
    }
}
