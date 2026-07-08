package com.app.learning.ui.course;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.app.learning.data.model.Course;
import com.app.learning.ui.widget.TrangThaiTrongView;
import com.example.vietsyncmobile.R;
import java.util.ArrayList;
import java.util.List;

public class DanhSachKhoaHocFragment extends Fragment {

    private Toolbar thanhCongCu;
    private Spinner boLocSapXep;
    private RecyclerView rvKhoaHoc;
    private SwipeRefreshLayout boLamMoi;
    private TrangThaiTrongView viewTrangThaiTrong;

    private KhoaHocGridAdapter adapterGrid;
    private KhoaHocListAdapter adapterList;
    private DanhSachKhoaHocViewModel viewModel;

    private String categoryId;
    private String categoryName;
    private String currentSortBy = "Phổ biến";
    private boolean laGiaoDienGrid = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.course_list_fragment, container, false);

        thanhCongCu = view.findViewById(R.id.toolbar);
        boLocSapXep = view.findViewById(R.id.sort_spinner);
        rvKhoaHoc = view.findViewById(R.id.courses_recycler_view);
        boLamMoi = view.findViewById(R.id.swipe_refresh);
        viewTrangThaiTrong = view.findViewById(R.id.empty_state);

        viewModel = new ViewModelProvider(this).get(DanhSachKhoaHocViewModel.class);

        if (getArguments() != null) {
            categoryId = getArguments().getString("categoryId", "");
            categoryName = getArguments().getString("categoryName", getString(R.string.home_section_categories));
            thanhCongCu.setTitle(categoryName);
        }

        thietLapThanhCongCu();
        thietLapBoLocSapXep();
        thietLapDanhSachHienThi();
        theoDoiDuLieu();

        return view;
    }

    private void thietLapThanhCongCu() {
        thanhCongCu.setNavigationOnClickListener(v -> requireActivity().onBackPressed());
        thanhCongCu.inflateMenu(R.menu.menu_course_list);

        MenuItem layoutToggleMenuItem = thanhCongCu.getMenu().findItem(R.id.action_toggle_layout);
        capNhatIconChuyenDoi(layoutToggleMenuItem);

        thanhCongCu.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_toggle_layout) {
                laGiaoDienGrid = !laGiaoDienGrid;
                capNhatIconChuyenDoi(item);
                thietLapBoCucDanhSach();
                return true;
            }
            return false;
        });
    }

    private void capNhatIconChuyenDoi(MenuItem item) {
        if (item != null) {
            item.setIcon(laGiaoDienGrid ? R.drawable.ic_list : R.drawable.ic_grid);
        }
    }

    private void thietLapBoLocSapXep() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.sort_options,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        boLocSapXep.setAdapter(adapter);

        boLocSapXep.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String sortBy = parent.getItemAtPosition(position).toString();
                currentSortBy = sortBy;
                taiDuLieuBanDau(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void thietLapDanhSachHienThi() {
        adapterGrid = new KhoaHocGridAdapter(this::moChiTietKhoaHoc);
        adapterList = new KhoaHocListAdapter(this::moChiTietKhoaHoc);

        thietLapBoCucDanhSach();

        rvKhoaHoc.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                    int visibleItemCount = layoutManager.getChildCount();
                    int totalItemCount = layoutManager.getItemCount();
                    int firstVisibleItemPosition = 0;

                    if (layoutManager instanceof GridLayoutManager) {
                        firstVisibleItemPosition = ((GridLayoutManager) layoutManager).findFirstVisibleItemPosition();
                    } else if (layoutManager instanceof LinearLayoutManager) {
                        firstVisibleItemPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                    }

                    if (viewModel.layTrangThaiDangTai().getValue() != null && !viewModel.layTrangThaiDangTai().getValue()) {
                        if (!viewModel.laTrangCuoi()) {
                            if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                                    && firstVisibleItemPosition >= 0) {
                                taiThemDuLieu();
                            }
                        }
                    }
                }
            }
        });

        boLamMoi.setOnRefreshListener(() -> taiDuLieuBanDau(false));

        viewTrangThaiTrong.datSuKienClickThuLai(v -> taiDuLieuBanDau(true));
    }

    private void thietLapBoCucDanhSach() {
        List<Course> currentCourses = new ArrayList<>();
        if (rvKhoaHoc.getAdapter() instanceof KhoaHocGridAdapter) {
            currentCourses = adapterGrid.layDanhSach();
        } else if (rvKhoaHoc.getAdapter() instanceof KhoaHocListAdapter) {
            currentCourses = adapterList.layDanhSach();
        }

        if (laGiaoDienGrid) {
            rvKhoaHoc.setLayoutManager(new GridLayoutManager(requireContext(), 2));
            rvKhoaHoc.setAdapter(adapterGrid);
            adapterGrid.capNhatDanhSach(currentCourses);
        } else {
            rvKhoaHoc.setLayoutManager(new LinearLayoutManager(requireContext()));
            rvKhoaHoc.setAdapter(adapterList);
            adapterList.capNhatDanhSach(currentCourses);
        }
    }

    private void theoDoiDuLieu() {
        viewModel.layDanhSachKhoaHoc().observe(getViewLifecycleOwner(), coursesList -> {
            if (coursesList != null) {
                if (coursesList.isEmpty()) {
                    if (viewModel.layTrangThaiDangTai().getValue() != null && !viewModel.layTrangThaiDangTai().getValue()) {
                        viewTrangThaiTrong.setVisibility(View.VISIBLE);
                        viewTrangThaiTrong.datThongBao("Không tìm thấy khóa học nào.");
                        viewTrangThaiTrong.hienThiNutThuLai(true);
                        rvKhoaHoc.setVisibility(View.GONE);
                    }
                } else {
                    viewTrangThaiTrong.setVisibility(View.GONE);
                    rvKhoaHoc.setVisibility(View.VISIBLE);

                    if (rvKhoaHoc.getAdapter() instanceof KhungTaiAdapter) {
                        thietLapBoCucDanhSach();
                    }

                    if (laGiaoDienGrid) {
                        adapterGrid.capNhatDanhSach(coursesList);
                    } else {
                        adapterList.capNhatDanhSach(coursesList);
                    }
                }
            }
        });

        viewModel.layTrangThaiDangTai().observe(getViewLifecycleOwner(), loading -> {
            if (loading) {
                if (adapterGrid.getItemCount() == 0 && adapterList.getItemCount() == 0) {
                    hienThiKhungTai(true);
                }
            } else {
                hienThiKhungTai(false);
            }
        });

        viewModel.layThongBaoLoi().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                if (adapterGrid.getItemCount() == 0 && adapterList.getItemCount() == 0) {
                    viewTrangThaiTrong.setVisibility(View.VISIBLE);
                    viewTrangThaiTrong.datThongBao("Đã xảy ra lỗi: " + error);
                    viewTrangThaiTrong.hienThiNutThuLai(true);
                    rvKhoaHoc.setVisibility(View.GONE);
                } else {
                    Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void hienThiKhungTai(boolean show) {
        if (show) {
            boLamMoi.setEnabled(false);
            KhungTaiAdapter skeletonAdapter = new KhungTaiAdapter(6, laGiaoDienGrid);
            rvKhoaHoc.setLayoutManager(laGiaoDienGrid
                    ? new GridLayoutManager(requireContext(), 2)
                    : new LinearLayoutManager(requireContext()));
            rvKhoaHoc.setAdapter(skeletonAdapter);
        } else {
            boLamMoi.setEnabled(true);
            boLamMoi.setRefreshing(false);
        }
    }

    private void taiDuLieuBanDau(boolean showSkeleton) {
        if (showSkeleton) {
            hienThiKhungTai(true);
        }
        viewModel.taiKhoaHoc(categoryId, currentSortBy, true);
    }

    private void taiThemDuLieu() {
        viewModel.taiKhoaHoc(categoryId, currentSortBy, false);
    }

    private void moChiTietKhoaHoc(Course course) {
        Intent intent = new Intent(requireActivity(), CourseDetailActivity.class);
        intent.putExtra("course", course);
        intent.putExtra("course_id", course.getId());
        startActivity(intent);
    }
}
