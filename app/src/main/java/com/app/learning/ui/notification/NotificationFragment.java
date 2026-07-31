package com.app.learning.ui.notification;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.learning.data.api.Resource;
import com.app.learning.data.model.NotificationModel;
import com.example.vietsyncmobile.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NotificationFragment extends Fragment {

    private NotificationViewModel viewModel;
    private NotificationAdapter adapter;
    private List<NotificationModel> allNotifications = new ArrayList<>();
    
    private View root;
    private RecyclerView rvNotifications;
    private TabLayout tabLayout;
    private View layoutEmptyState;
    private androidx.swiperefreshlayout.widget.SwipeRefreshLayout swipeRefresh;
    private int currentTab = 0; // 0: All, 1: Unread, 2: Course, 3: System

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_notification, container, false);
        viewModel = new ViewModelProvider(this).get(NotificationViewModel.class);
        initViews();
        setupRecyclerView();
        setupTabLayout();
        observeViewModel();
        
        viewModel.loadNotifications();
        return root;
    }

    private View layoutNormalMode;
    private View layoutSelectMode;
    private android.widget.Button btnDeleteSelected;

    private void initViews() {
        Toolbar toolbar = root.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(v -> {
            NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment);
            navController.navigateUp();
        });

        rvNotifications = root.findViewById(R.id.rv_notifications);
        tabLayout = root.findViewById(R.id.tab_layout);
        layoutEmptyState = root.findViewById(R.id.layout_empty_state);
        swipeRefresh = root.findViewById(R.id.swipe_refresh);

        swipeRefresh.setOnRefreshListener(() -> viewModel.loadNotifications());

        layoutNormalMode = root.findViewById(R.id.layout_normal_mode);
        layoutSelectMode = root.findViewById(R.id.layout_select_mode);
        btnDeleteSelected = root.findViewById(R.id.btn_delete_selected);

        root.findViewById(R.id.btn_read_all).setOnClickListener(v -> viewModel.markAllRead());
        
        root.findViewById(R.id.btn_delete_all).setOnClickListener(v -> {
            new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Xóa tất cả")
                    .setMessage("Bạn có chắc chắn muốn xóa tất cả thông báo không?")
                    .setNegativeButton("Hủy", null)
                    .setPositiveButton("Xóa", (dialog, which) -> {
                        viewModel.deleteAllNotifications();
                    })
                    .show();
        });

        root.findViewById(R.id.btn_select_mode).setOnClickListener(v -> {
            adapter.setSelectMode(true);
            layoutNormalMode.setVisibility(View.GONE);
            layoutSelectMode.setVisibility(View.VISIBLE);
        });

        root.findViewById(R.id.btn_cancel_select).setOnClickListener(v -> {
            adapter.setSelectMode(false);
            layoutSelectMode.setVisibility(View.GONE);
            layoutNormalMode.setVisibility(View.VISIBLE);
        });

        btnDeleteSelected.setOnClickListener(v -> {
            List<String> selectedIds = adapter.getSelectedIds();
            if (!selectedIds.isEmpty()) {
                viewModel.deleteSelectedNotifications(selectedIds);
                adapter.setSelectMode(false);
                layoutSelectMode.setVisibility(View.GONE);
                layoutNormalMode.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setupRecyclerView() {
        adapter = new NotificationAdapter(requireContext(), new NotificationAdapter.OnNotificationClickListener() {
            @Override
            public void onNotificationClick(NotificationModel notification) {
                if (!notification.isRead()) {
                    viewModel.markAsRead(notification.getId());
                    notification.setRead(true);
                    adapter.notifyDataSetChanged();
                }
                Intent intent = new Intent(requireActivity(), com.app.learning.ui.course.CourseDetailActivity.class);
                String courseId = "c0eebc99-9c0b-4ef8-bb6d-6bb9bd380001";
                if (notification.getTitle() != null && notification.getTitle().contains("UI/UX")) {
                    courseId = "c0eebc99-9c0b-4ef8-bb6d-6bb9bd380002";
                }
                intent.putExtra("course_id", courseId);
                startActivity(intent);
            }

            @Override
            public void onSelectionChanged(int count) {
                if (btnDeleteSelected != null) {
                    if (count > 0) {
                        btnDeleteSelected.setText("Xóa đã chọn (" + count + ")");
                        btnDeleteSelected.setEnabled(true);
                    } else {
                        btnDeleteSelected.setText("Xóa đã chọn");
                        btnDeleteSelected.setEnabled(false);
                    }
                }
            }
        });
        
        rvNotifications.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvNotifications.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        rvNotifications.setAdapter(adapter);

        ItemTouchHelper.SimpleCallback touchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                NotificationModel notification = adapter.getNotificationAt(position);
                
                // Delete from DB
                viewModel.deleteNotification(notification.getId());
                
                // Remove locally
                allNotifications.remove(notification);
                filterNotifications(currentTab);
                
                Snackbar.make(root, "Đã xóa thông báo", Snackbar.LENGTH_SHORT).show();
            }
        };
        new ItemTouchHelper(touchHelperCallback).attachToRecyclerView(rvNotifications);
    }

    private void setupTabLayout() {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentTab = tab.getPosition();
                filterNotifications(currentTab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void observeViewModel() {
        viewModel.getNotificationsLiveData().observe(getViewLifecycleOwner(), resource -> {
            switch (resource.status) {
                case LOADING:
                    if (!swipeRefresh.isRefreshing()) {
                        swipeRefresh.setRefreshing(true);
                    }
                    break;
                case SUCCESS:
                    swipeRefresh.setRefreshing(false);
                    if (resource.data != null) {
                        allNotifications = resource.data;
                        filterNotifications(currentTab);
                    }
                    break;
                case ERROR:
                    swipeRefresh.setRefreshing(false);
                    Toast.makeText(requireContext(), resource.error.getMessage(), Toast.LENGTH_SHORT).show();
                    break;
            }
        });

        viewModel.getActionResultLiveData().observe(getViewLifecycleOwner(), resource -> {
            if (resource.status == Resource.Status.ERROR) {
                Toast.makeText(requireContext(), resource.error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterNotifications(int tabIndex) {
        List<NotificationModel> filtered = new ArrayList<>();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            switch (tabIndex) {
                case 0: // All
                    filtered = new ArrayList<>(allNotifications);
                    break;
                case 1: // Unread
                    filtered = allNotifications.stream().filter(n -> !n.isRead()).collect(Collectors.toList());
                    break;
                case 2: // Course
                    filtered = allNotifications.stream().filter(n -> "course".equalsIgnoreCase(n.getType())).collect(Collectors.toList());
                    break;
                case 3: // System
                    filtered = allNotifications.stream().filter(n -> "system".equalsIgnoreCase(n.getType())).collect(Collectors.toList());
                    break;
            }
        } else {
            for (NotificationModel n : allNotifications) {
                if (tabIndex == 0) {
                    filtered.add(n);
                } else if (tabIndex == 1 && !n.isRead()) {
                    filtered.add(n);
                } else if (tabIndex == 2 && "course".equalsIgnoreCase(n.getType())) {
                    filtered.add(n);
                } else if (tabIndex == 3 && "system".equalsIgnoreCase(n.getType())) {
                    filtered.add(n);
                }
            }
        }

        adapter.setNotifications(filtered);
        
        if (filtered.isEmpty()) {
            layoutEmptyState.setVisibility(View.VISIBLE);
            rvNotifications.setVisibility(View.GONE);
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            rvNotifications.setVisibility(View.VISIBLE);
        }
    }
}
