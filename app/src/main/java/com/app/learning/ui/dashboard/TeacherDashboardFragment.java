package com.app.learning.ui.dashboard;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.learning.data.model.RecentEnrollment;
import com.app.learning.ui.base.BaseFragment;
import com.app.learning.utils.RequireRole;
import com.app.learning.utils.RoleManager;
import com.example.vietsyncmobile.R;

import java.util.ArrayList;
import java.util.List;

/**
 * TeacherDashboardFragment is the control center for teachers,
 * displaying quick enrollment stats and a list of new course registrations.
 */
@RequireRole(RoleManager.Role.TEACHER)
public class TeacherDashboardFragment extends BaseFragment {

    private TextView txtTotalStudentsCount;
    private TextView txtTotalCoursesCount;
    private TextView txtTotalRevenue;
    private RecyclerView rvRecentEnrollments;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_teacher_dashboard;
    }

    @Override
    protected void initViews(View view) {
        txtTotalStudentsCount = view.findViewById(R.id.txtTotalStudentsCount);
        txtTotalCoursesCount = view.findViewById(R.id.txtTotalCoursesCount);
        txtTotalRevenue = view.findViewById(R.id.txtTotalRevenue);
        rvRecentEnrollments = view.findViewById(R.id.rvRecentEnrollments);

        // Configure RecyclerView
        rvRecentEnrollments.setLayoutManager(new LinearLayoutManager(getContext()));

        // Populate Dashboard UI components
        loadStats();
        loadRecentEnrollments();
    }

    @Override
    protected void initObservers() {
        // Observers for ViewModel state could be added here
    }

    /**
     * Load teaching analytics stats.
     */
    private void loadStats() {
        // High quality mock values for presentation
        txtTotalStudentsCount.setText("1,452");
        txtTotalCoursesCount.setText("18");
        txtTotalRevenue.setText("256Mđ");
    }

    /**
     * Load recent enrollments feed.
     */
    private void loadRecentEnrollments() {
        List<RecentEnrollment> list = new ArrayList<>();
        list.add(new RecentEnrollment(
                "Nguyễn Minh Hằng",
                "hang.nguyen@gmail.com",
                "Lập trình Android căn bản",
                "5 phút trước",
                ""
        ));
        list.add(new RecentEnrollment(
                "Trần Hoàng Long",
                "long.tran@yahoo.com",
                "Thiết kế UI/UX nâng cao",
                "15 phút trước",
                ""
        ));
        list.add(new RecentEnrollment(
                "Lê Quốc Anh",
                "anh.le@outlook.com",
                "Node.js Backend Master",
                "1 giờ trước",
                ""
        ));
        list.add(new RecentEnrollment(
                "Phạm Thúy Vi",
                "vi.pham@gmail.com",
                "Kotlin Multiplatform căn bản",
                "3 giờ trước",
                ""
        ));
        list.add(new RecentEnrollment(
                "Đỗ Minh Quân",
                "quan.do@gmail.com",
                "Lập trình Android căn bản",
                "Hôm qua",
                ""
        ));

        RecentEnrollmentAdapter adapter = new RecentEnrollmentAdapter(list);
        rvRecentEnrollments.setAdapter(adapter);

        // Subtle animation entrance for list
        rvRecentEnrollments.setAlpha(0f);
        rvRecentEnrollments.animate().alpha(1f).setDuration(500).start();
    }
}
