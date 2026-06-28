package com.app.learning.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.app.learning.data.model.Banner;
import com.bumptech.glide.Glide;
import com.example.vietsyncmobile.R;
import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {

    private final List<Banner> banners;

    public BannerAdapter(List<Banner> banners) {
        this.banners = banners;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_banner, parent, false);
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        Banner banner = banners.get(position);
        holder.tvTitle.setText(banner.getTitle());
        holder.tvDesc.setText(banner.getDescription());
        Glide.with(holder.itemView.getContext())
                .load(banner.getImageUrl())
                .placeholder(R.drawable.ic_logo_placeholder)
                .error(R.drawable.ic_logo_placeholder)
                .into(holder.ivBanner);
    }

    @Override
    public int getItemCount() {
        return banners != null ? banners.size() : 0;
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBanner;
        TextView tvTitle;
        TextView tvDesc;

        BannerViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBanner = itemView.findViewById(R.id.iv_banner);
            tvTitle = itemView.findViewById(R.id.tv_banner_title);
            tvDesc = itemView.findViewById(R.id.tv_banner_desc);
        }
    }
}
