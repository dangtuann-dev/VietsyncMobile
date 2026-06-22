package com.app.learning.ui.profile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.app.learning.data.model.Certificate;
import com.bumptech.glide.Glide;
import com.example.vietsyncmobile.R;

import java.util.List;

public class CertificateAdapter extends RecyclerView.Adapter<CertificateAdapter.CertificateViewHolder> {

    private final List<Certificate> items;
    private final OnCertificateClickListener viewListener;

    public interface OnCertificateClickListener {
        void onViewClick(Certificate certificate);
    }

    public CertificateAdapter(List<Certificate> items, OnCertificateClickListener viewListener) {
        this.items = items;
        this.viewListener = viewListener;
    }

    @NonNull
    @Override
    public CertificateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_certificate, parent, false);
        return new CertificateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CertificateViewHolder holder, int position) {
        Certificate item = items.get(position);

        if (item.getCourse() != null) {
            holder.tvCourseTitle.setText(item.getCourse().getTitle());
            Glide.with(holder.itemView.getContext())
                    .load(item.getCourse().getThumbnail())
                    .placeholder(R.drawable.ic_logo_placeholder)
                    .error(R.drawable.ic_logo_placeholder)
                    .into(holder.imgCourseThumbnail);
        } else {
            holder.tvCourseTitle.setText("Khóa học");
            holder.imgCourseThumbnail.setImageResource(R.drawable.ic_logo_placeholder);
        }

        holder.tvIssuedDate.setText("Ngày cấp: " + formatDate(item.getIssuedAt()));

        holder.btnViewCertificate.setOnClickListener(v -> {
            if (viewListener != null) {
                viewListener.onViewClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private String formatDate(String rawDate) {
        if (rawDate == null) return "";
        try {
            if (rawDate.contains("T")) {
                String datePart = rawDate.split("T")[0]; // "2026-06-22"
                String[] parts = datePart.split("-");
                if (parts.length == 3) {
                    return parts[2] + "/" + parts[1] + "/" + parts[0];
                }
            }
            return rawDate;
        } catch (Exception e) {
            return rawDate;
        }
    }

    static class CertificateViewHolder extends RecyclerView.ViewHolder {
        ImageView imgCourseThumbnail;
        TextView tvCourseTitle;
        TextView tvIssuedDate;
        Button btnViewCertificate;

        public CertificateViewHolder(@NonNull View itemView) {
            super(itemView);
            imgCourseThumbnail = itemView.findViewById(R.id.imgCourseThumbnail);
            tvCourseTitle = itemView.findViewById(R.id.tvCourseTitle);
            tvIssuedDate = itemView.findViewById(R.id.tvIssuedDate);
            btnViewCertificate = itemView.findViewById(R.id.btnViewCertificate);
        }
    }
}
