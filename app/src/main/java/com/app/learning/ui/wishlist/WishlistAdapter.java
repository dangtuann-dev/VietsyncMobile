package com.app.learning.ui.wishlist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.app.learning.data.model.Course;
import com.app.learning.data.model.WishlistModel;
import com.bumptech.glide.Glide;
import com.example.vietsyncmobile.R;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.WishlistViewHolder> {

    private final List<WishlistModel> items = new ArrayList<>();
    private final OnWishlistItemClickListener clickListener;

    public interface OnWishlistItemClickListener {
        void onItemClick(Course course);
        void onEnrollClick(Course course);
    }

    public WishlistAdapter(OnWishlistItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setItems(List<WishlistModel> newItems) {
        this.items.clear();
        if (newItems != null) {
            this.items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    public List<WishlistModel> getItems() {
        return items;
    }

    @NonNull
    @Override
    public WishlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wishlist, parent, false);
        return new WishlistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WishlistViewHolder holder, int position) {
        WishlistModel item = items.get(position);
        Course course = item.getCourse();
        if (course == null) return;

        holder.tvTitle.setText(course.getTitle());
        holder.tvLevel.setText(course.getLevel() != null ? course.getLevel() : "Cơ bản");
        
        String instructorName = (course.getInstructor() != null && course.getInstructor().getFullName() != null)
                ? course.getInstructor().getFullName()
                : "Giảng viên";
        holder.tvInstructor.setText(instructorName);

        holder.tvPrice.setText(course.getPrice() == 0
                ? "Miễn phí"
                : String.format("%,.0fđ", course.getPrice()));

        Glide.with(holder.itemView.getContext())
                .load(course.getThumbnail())
                .placeholder(R.drawable.ic_logo_placeholder)
                .error(R.drawable.ic_logo_placeholder)
                .into(holder.ivThumbnail);

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onItemClick(course);
            }
        });

        holder.btnEnroll.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onEnrollClick(course);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class WishlistViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumbnail;
        TextView tvLevel;
        TextView tvInstructor;
        TextView tvTitle;
        TextView tvPrice;
        MaterialButton btnEnroll;

        WishlistViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumbnail = itemView.findViewById(R.id.iv_thumbnail);
            tvLevel = itemView.findViewById(R.id.tv_level);
            tvInstructor = itemView.findViewById(R.id.tv_instructor);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvPrice = itemView.findViewById(R.id.tv_price);
            btnEnroll = itemView.findViewById(R.id.btn_enroll);
        }
    }
}
