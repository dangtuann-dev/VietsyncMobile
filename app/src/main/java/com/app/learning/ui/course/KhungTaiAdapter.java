package com.app.learning.ui.course;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.vietsyncmobile.R;

public class KhungTaiAdapter extends RecyclerView.Adapter<KhungTaiAdapter.KhungTaiViewHolder> {

    private final int soLuongItem;
    private final boolean laGrid;

    public KhungTaiAdapter(int soLuongItem, boolean laGrid) {
        this.soLuongItem = soLuongItem;
        this.laGrid = laGrid;
    }

    @NonNull
    @Override
    public KhungTaiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = laGrid ? R.layout.item_course_skeleton_grid : R.layout.item_course_skeleton_list;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new KhungTaiViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KhungTaiViewHolder holder, int position) {
        AlphaAnimation pulse = new AlphaAnimation(0.4f, 1.0f);
        pulse.setDuration(800);
        pulse.setRepeatMode(Animation.REVERSE);
        pulse.setRepeatCount(Animation.INFINITE);
        holder.itemView.startAnimation(pulse);
    }

    @Override
    public int getItemCount() {
        return soLuongItem;
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull KhungTaiViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.itemView.clearAnimation();
    }

    public static class KhungTaiViewHolder extends RecyclerView.ViewHolder {
        public KhungTaiViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
