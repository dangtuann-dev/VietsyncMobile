package com.app.learning.ui.dashboard;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.learning.data.model.RecentEnrollment;
import com.bumptech.glide.Glide;
import com.example.vietsyncmobile.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * RecentEnrollmentAdapter manages list rendering for course registrations in Teacher Dashboard.
 */
public class RecentEnrollmentAdapter extends RecyclerView.Adapter<RecentEnrollmentAdapter.ViewHolder> {

    private final List<RecentEnrollment> enrollmentList;

    public RecentEnrollmentAdapter(List<RecentEnrollment> enrollmentList) {
        this.enrollmentList = enrollmentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recent_enrollment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecentEnrollment enrollment = enrollmentList.get(position);

        holder.txtStudentName.setText(enrollment.getStudentName());
        holder.txtStudentEmail.setText(enrollment.getStudentEmail());
        holder.txtCourseName.setText("Khóa học: " + enrollment.getCourseName());
        holder.txtEnrollDate.setText(enrollment.getEnrollDate());

        // Load avatar using Glide, with profile placeholder fallback
        if (enrollment.getAvatarUrl() != null && !enrollment.getAvatarUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(enrollment.getAvatarUrl())
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .error(R.drawable.ic_profile_placeholder)
                    .into(holder.imgStudentAvatar);
        } else {
            holder.imgStudentAvatar.setImageResource(R.drawable.ic_profile_placeholder);
        }
    }

    @Override
    public int getItemCount() {
        return enrollmentList != null ? enrollmentList.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        final CircleImageView imgStudentAvatar;
        final TextView txtStudentName;
        final TextView txtStudentEmail;
        final TextView txtCourseName;
        final TextView txtEnrollDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgStudentAvatar = itemView.findViewById(R.id.imgStudentAvatar);
            txtStudentName = itemView.findViewById(R.id.txtStudentName);
            txtStudentEmail = itemView.findViewById(R.id.txtStudentEmail);
            txtCourseName = itemView.findViewById(R.id.txtCourseName);
            txtEnrollDate = itemView.findViewById(R.id.txtEnrollDate);
        }
    }
}
