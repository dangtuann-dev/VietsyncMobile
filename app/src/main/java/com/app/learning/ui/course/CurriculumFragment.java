package com.app.learning.ui.course;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.lifecycle.ViewModelProvider;
import com.app.learning.data.api.Resource;
import com.app.learning.data.model.Lesson;
import com.app.learning.ui.base.BaseFragment;
import com.example.vietsyncmobile.R;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CurriculumFragment extends BaseFragment {

    private ExpandableListView elvCurriculum;
    private ProgressBar pbLoading;
    private TextView tvEmpty;
    
    private CourseDetailViewModel viewModel;
    private final List<String> listSections = new ArrayList<>();
    private final Map<String, List<Lesson>> sectionLessons = new HashMap<>();
    private CurriculumAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_curriculum;
    }

    @Override
    protected void initViews(View view) {
        elvCurriculum = view.findViewById(R.id.elv_curriculum);
        pbLoading = view.findViewById(R.id.pb_loading);
        tvEmpty = view.findViewById(R.id.tv_empty);
        
        adapter = new CurriculumAdapter();
        elvCurriculum.setAdapter(adapter);
    }

    @Override
    protected void initObservers() {
        viewModel = new ViewModelProvider(requireActivity()).get(CourseDetailViewModel.class);
        
        viewModel.getCourseLessons().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case LOADING:
                        pbLoading.setVisibility(View.VISIBLE);
                        elvCurriculum.setVisibility(View.GONE);
                        tvEmpty.setVisibility(View.GONE);
                        break;
                    case SUCCESS:
                        pbLoading.setVisibility(View.GONE);
                        if (resource.data != null && !resource.data.isEmpty()) {
                            elvCurriculum.setVisibility(View.VISIBLE);
                            tvEmpty.setVisibility(View.GONE);
                            groupLessons(resource.data);
                        } else {
                            elvCurriculum.setVisibility(View.GONE);
                            tvEmpty.setVisibility(View.VISIBLE);
                        }
                        break;
                    case ERROR:
                        pbLoading.setVisibility(View.GONE);
                        elvCurriculum.setVisibility(View.GONE);
                        tvEmpty.setVisibility(View.VISIBLE);
                        tvEmpty.setText("Lỗi khi tải chương trình học");
                        break;
                }
            }
        });
    }

    private void groupLessons(List<Lesson> lessons) {
        listSections.clear();
        sectionLessons.clear();

        List<Lesson> section1 = new ArrayList<>();
        List<Lesson> section2 = new ArrayList<>();
        List<Lesson> section3 = new ArrayList<>();

        for (Lesson lesson : lessons) {
            if (lesson.getOrderIndex() == 1) {
                lesson.setFreePreview(true);
            }
            
            if (lesson.getOrderIndex() <= 2) {
                section1.add(lesson);
            } else if (lesson.getOrderIndex() <= 5) {
                section2.add(lesson);
            } else {
                section3.add(lesson);
            }
        }

        if (!section1.isEmpty()) {
            String title = "Phần 1: Giới thiệu & Cài đặt cơ bản (" + section1.size() + " bài)";
            listSections.add(title);
            sectionLessons.put(title, section1);
        }
        if (!section2.isEmpty()) {
            String title = "Phần 2: Kiến thức trọng tâm & Thực hành (" + section2.size() + " bài)";
            listSections.add(title);
            sectionLessons.put(title, section2);
        }
        if (!section3.isEmpty()) {
            String title = "Phần 3: Kỹ thuật nâng cao & Tổng kết (" + section3.size() + " bài)";
            listSections.add(title);
            sectionLessons.put(title, section3);
        }

        adapter.notifyDataSetChanged();
        
        for (int i = 0; i < listSections.size(); i++) {
            elvCurriculum.expandGroup(i);
        }
    }

    private class CurriculumAdapter extends BaseExpandableListAdapter {

        @Override
        public int getGroupCount() {
            return listSections.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            String section = listSections.get(groupPosition);
            List<Lesson> lessons = sectionLessons.get(section);
            return lessons != null ? lessons.size() : 0;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return listSections.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            String section = listSections.get(groupPosition);
            List<Lesson> lessons = sectionLessons.get(section);
            return lessons != null ? lessons.get(childPosition) : null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            String sectionTitle = (String) getGroup(groupPosition);
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_curriculum_group, parent, false);
            }
            TextView tvTitle = convertView.findViewById(R.id.tv_section_title);
            ImageView ivIndicator = convertView.findViewById(R.id.iv_indicator);
            
            tvTitle.setText(sectionTitle);
            ivIndicator.setImageResource(R.drawable.ic_chevron_right);
            ivIndicator.setRotation(isExpanded ? 90f : 0f);
            
            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            Lesson lesson = (Lesson) getChild(groupPosition, childPosition);
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_curriculum_child, parent, false);
            }
            
            ImageView ivIcon = convertView.findViewById(R.id.iv_lesson_icon);
            TextView tvTitle = convertView.findViewById(R.id.tv_lesson_title);
            TextView tvDuration = convertView.findViewById(R.id.tv_lesson_duration);
            TextView tvFreePreview = convertView.findViewById(R.id.tv_free_preview);

            if (lesson != null) {
                tvTitle.setText(lesson.getTitle());
                tvDuration.setText(lesson.getDuration() + " phút");
                
                if ("video".equals(lesson.getType())) {
                    ivIcon.setImageResource(R.drawable.ic_play_circle_filled);
                    ivIcon.setColorFilter(parent.getContext().getResources().getColor(R.color.primary));
                } else if ("quiz".equals(lesson.getType())) {
                    ivIcon.setImageResource(R.drawable.ic_edit);
                    ivIcon.setColorFilter(parent.getContext().getResources().getColor(R.color.category_green));
                } else {
                    ivIcon.setImageResource(R.drawable.ic_book);
                    ivIcon.setColorFilter(parent.getContext().getResources().getColor(R.color.category_blue));
                }

                if (lesson.isFreePreview()) {
                    tvFreePreview.setVisibility(View.VISIBLE);
                } else {
                    tvFreePreview.setVisibility(View.GONE);
                }
            }

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }
}
