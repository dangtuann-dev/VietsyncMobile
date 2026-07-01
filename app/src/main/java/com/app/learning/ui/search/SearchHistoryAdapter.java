package com.app.learning.ui.search;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.learning.data.local.SearchHistory;
import com.example.vietsyncmobile.R;

import java.util.ArrayList;
import java.util.List;

public class SearchHistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_ITEM   = 1;

    private List<SearchHistory> historyList = new ArrayList<>();
    private final OnHistoryClickListener clickListener;

    public interface OnHistoryClickListener {
        void onItemClick(String query);
        void onDeleteClick(SearchHistory searchHistory);
        void onClearAllClick();
    }

    public SearchHistoryAdapter(OnHistoryClickListener clickListener) {
        this.clickListener = clickListener;
    }

    public void setHistoryList(List<SearchHistory> list) {
        this.historyList = list != null ? list : new ArrayList<>();
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? VIEW_TYPE_HEADER : VIEW_TYPE_ITEM;
    }

    @Override
    public int getItemCount() {
        // Header + items (only show header when there is at least 1 item)
        return historyList.isEmpty() ? 0 : historyList.size() + 1;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_HEADER) {
            View view = inflater.inflate(R.layout.item_search_history_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_search_history, parent, false);
            return new ItemViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).bind();
        } else if (holder instanceof ItemViewHolder) {
            SearchHistory item = historyList.get(position - 1); // offset by 1 for header
            ((ItemViewHolder) holder).bind(item);
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvClearAll;

        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvClearAll = itemView.findViewById(R.id.tv_clear_all);
        }

        void bind() {
            tvClearAll.setOnClickListener(v -> {
                if (clickListener != null) clickListener.onClearAllClick();
            });
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvQuery;
        private final ImageView ivDelete;

        ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuery = itemView.findViewById(R.id.tv_query);
            ivDelete = itemView.findViewById(R.id.iv_delete);
        }

        void bind(SearchHistory item) {
            tvQuery.setText(item.getQuery());

            itemView.setOnClickListener(v -> {
                if (clickListener != null) clickListener.onItemClick(item.getQuery());
            });

            ivDelete.setOnClickListener(v -> {
                if (clickListener != null) clickListener.onDeleteClick(item);
            });
        }
    }
}
