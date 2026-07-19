package com.app.learning.ui.learning;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.app.learning.data.model.Course;
import com.app.learning.data.model.Enrollment;
import com.bumptech.glide.Glide;
import com.example.vietsyncmobile.R;
import com.google.android.material.button.MaterialButton;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SavedAdapter extends RecyclerView.Adapter<SavedAdapter.ViewHolder> {

    private final List<Enrollment> items = new ArrayList<>();
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Enrollment enrollment);
        void onRemoveClick(Enrollment enrollment);
        void onEnrollClick(Enrollment enrollment);
    }

    public SavedAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<Enrollment> newItems) {
        this.items.clear();
        if (newItems != null) {
            this.items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_saved_course, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Enrollment item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivThumbnail;
        TextView tvTitle;
        TextView tvInstructor;
        TextView tvPrice;
        MaterialButton btnRemove;
        MaterialButton btnEnroll;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivThumbnail = itemView.findViewById(R.id.iv_thumbnail);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvInstructor = itemView.findViewById(R.id.tv_instructor);
            tvPrice = itemView.findViewById(R.id.tv_price);
            btnRemove = itemView.findViewById(R.id.btn_remove);
            btnEnroll = itemView.findViewById(R.id.btn_enroll);

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(items.get(pos));
                }
            });

            btnRemove.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && listener != null) {
                    listener.onRemoveClick(items.get(pos));
                }
            });

            btnEnroll.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && listener != null) {
                    listener.onEnrollClick(items.get(pos));
                }
            });
        }

        public void bind(Enrollment enrollment) {
            Course course = enrollment.getCourse();
            if (course == null) return;

            tvTitle.setText(course.getTitle());
            tvInstructor.setText(course.getInstructor() != null ? course.getInstructor().getFullName() : "Giảng viên");

            double price = course.getPrice();
            if (price == 0) {
                tvPrice.setText("Miễn phí");
            } else {
                NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
                tvPrice.setText(formatter.format(price));
            }

            if (course.getThumbnail() != null && !course.getThumbnail().isEmpty()) {
                Glide.with(itemView.getContext())
                        .load(course.getThumbnail())
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_background)
                        .into(ivThumbnail);
            } else {
                ivThumbnail.setImageResource(R.drawable.ic_launcher_background);
            }
        }
    }
}
