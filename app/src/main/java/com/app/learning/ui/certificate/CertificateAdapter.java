package com.app.learning.ui.certificate;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vietsyncmobile.R;
import com.app.learning.data.model.CertificateModel;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class CertificateAdapter extends RecyclerView.Adapter<CertificateAdapter.CertificateViewHolder> {

    public interface OnCertificateClickListener {
        void onClick(CertificateModel certificate);
    }

    private final List<CertificateModel> certificateList = new ArrayList<>();
    private final OnCertificateClickListener listener;

    public CertificateAdapter(OnCertificateClickListener listener) {
        this.listener = listener;
    }

    public void setCertificates(List<CertificateModel> certificates) {
        this.certificateList.clear();
        if (certificates != null) {
            this.certificateList.addAll(certificates);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CertificateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_certificate, parent, false);
        return new CertificateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CertificateViewHolder holder, int position) {
        CertificateModel model = certificateList.get(position);
        holder.tvCourseTitle.setText(model.getCourseTitle() != null ? model.getCourseTitle() : "Khóa học đã hoàn thành");
        holder.tvIssuedDate.setText("Ngày cấp: " + (model.getIssuedAt() != null ? model.getIssuedAt() : "2026-07-22"));
        holder.btnViewCertificate.setOnClickListener(v -> {
            if (listener != null) listener.onClick(model);
        });
    }

    @Override
    public int getItemCount() {
        return certificateList.size();
    }

    static class CertificateViewHolder extends RecyclerView.ViewHolder {
        TextView tvCourseTitle, tvIssuedDate;
        MaterialButton btnViewCertificate;

        public CertificateViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCourseTitle = itemView.findViewById(R.id.tvCourseTitle);
            tvIssuedDate = itemView.findViewById(R.id.tvIssuedDate);
            btnViewCertificate = itemView.findViewById(R.id.btnViewCertificate);
        }
    }
}
