package com.app.learning.ui.notification;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.app.learning.data.model.NotificationModel;
import com.example.vietsyncmobile.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private final Context context;
    private final List<NotificationModel> notifications;
    private final OnNotificationClickListener listener;
    
    private boolean isSelectMode = false;
    private final Set<String> selectedIds = new HashSet<>();

    public interface OnNotificationClickListener {
        void onNotificationClick(NotificationModel notification);
        void onSelectionChanged(int selectedCount);
    }

    public NotificationAdapter(Context context, OnNotificationClickListener listener) {
        this.context = context;
        this.notifications = new ArrayList<>();
        this.listener = listener;
    }

    public void setNotifications(List<NotificationModel> notifications) {
        this.notifications.clear();
        if (notifications != null) {
            this.notifications.addAll(notifications);
        }
        notifyDataSetChanged();
    }
    
    public void setSelectMode(boolean selectMode) {
        this.isSelectMode = selectMode;
        if (!selectMode) {
            selectedIds.clear();
        }
        notifyDataSetChanged();
        if (listener != null) listener.onSelectionChanged(selectedIds.size());
    }

    public boolean isSelectMode() {
        return isSelectMode;
    }

    public void clearSelection() {
        selectedIds.clear();
        notifyDataSetChanged();
        if (listener != null) listener.onSelectionChanged(0);
    }

    public List<String> getSelectedIds() {
        return new ArrayList<>(selectedIds);
    }

    public NotificationModel getNotificationAt(int position) {
        return notifications.get(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotificationModel notification = notifications.get(position);
        holder.bind(notification);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvTitle, tvBody, tvTime;
        View vUnreadIndicator;
        CheckBox cbSelect;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_notification_icon);
            tvTitle = itemView.findViewById(R.id.tv_notification_title);
            tvBody = itemView.findViewById(R.id.tv_notification_body);
            tvTime = itemView.findViewById(R.id.tv_notification_time);
            vUnreadIndicator = itemView.findViewById(R.id.v_unread_indicator);
            cbSelect = itemView.findViewById(R.id.cb_select);

            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    NotificationModel notification = notifications.get(pos);
                    if (isSelectMode) {
                        if (selectedIds.contains(notification.getId())) {
                            selectedIds.remove(notification.getId());
                        } else {
                            selectedIds.add(notification.getId());
                        }
                        notifyItemChanged(pos);
                        if (listener != null) listener.onSelectionChanged(selectedIds.size());
                    } else {
                        if (listener != null) {
                            listener.onNotificationClick(notification);
                        }
                    }
                }
            });
            
            cbSelect.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    NotificationModel notification = notifications.get(pos);
                    if (cbSelect.isChecked()) {
                        selectedIds.add(notification.getId());
                    } else {
                        selectedIds.remove(notification.getId());
                    }
                    if (listener != null) listener.onSelectionChanged(selectedIds.size());
                }
            });
        }

        void bind(NotificationModel notification) {
            tvTitle.setText(notification.getTitle());
            tvBody.setText(notification.getBody());
            
            if (isSelectMode) {
                cbSelect.setVisibility(View.VISIBLE);
                cbSelect.setChecked(selectedIds.contains(notification.getId()));
            } else {
                cbSelect.setVisibility(View.GONE);
                cbSelect.setChecked(false);
            }

            // Handle type icon
            String type = notification.getType() != null ? notification.getType() : "";
            if (type.equalsIgnoreCase("course")) {
                ivIcon.setImageResource(R.drawable.ic_book);
            } else if (type.equalsIgnoreCase("quiz")) {
                ivIcon.setImageResource(R.drawable.ic_history);
            } else {
                ivIcon.setImageResource(R.drawable.ic_settings);
            }

            // Unread state
            if (!notification.isRead()) {
                vUnreadIndicator.setVisibility(View.VISIBLE);
                itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.primary_light));
                tvTitle.setTextColor(ContextCompat.getColor(context, R.color.text_primary));
            } else {
                vUnreadIndicator.setVisibility(View.GONE);
                itemView.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
                tvTitle.setTextColor(ContextCompat.getColor(context, R.color.text_secondary));
            }

            // Time format
            tvTime.setText(formatTime(notification.getCreatedAt()));
        }
        
        private String formatTime(String timeStr) {
            if (timeStr == null || timeStr.isEmpty()) return "";
            try {
                // Assuming Supabase timestamp format like 2026-07-04T05:24:43
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                Date date = sdf.parse(timeStr);
                if (date != null) {
                    long time = date.getTime();
                    long now = System.currentTimeMillis();
                    return DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS).toString();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return timeStr;
        }
    }
}
