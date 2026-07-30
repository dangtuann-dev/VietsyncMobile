package com.app.learning.ui.home;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;

import com.app.learning.data.model.Course;

import java.util.List;

public class CourseDiffCallback extends DiffUtil.Callback {

    private final List<Course> oldList;
    private final List<Course> newList;

    public CourseDiffCallback(List<Course> oldList, List<Course> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList != null ? oldList.size() : 0;
    }

    @Override
    public int getNewListSize() {
        return newList != null ? newList.size() : 0;
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        Course oldItem = oldList.get(oldItemPosition);
        Course newItem = newList.get(newItemPosition);
        if (oldItem.getId() != null && newItem.getId() != null) {
            return oldItem.getId().equals(newItem.getId());
        }
        return false;
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        Course oldItem = oldList.get(oldItemPosition);
        Course newItem = newList.get(newItemPosition);
        return oldItem.equals(newItem);
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
