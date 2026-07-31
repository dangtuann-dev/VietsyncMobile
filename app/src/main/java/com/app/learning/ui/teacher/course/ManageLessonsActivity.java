package com.app.learning.ui.teacher.course;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vietsyncmobile.R;
import com.app.learning.data.model.Lesson;
import com.app.learning.data.repository.TeacherCourseRepository;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class ManageLessonsActivity extends AppCompatActivity implements AddLessonBottomSheet.OnLessonAddedListener {

    private RecyclerView recyclerView;
    private TeacherLessonAdapter adapter;
    private TeacherCourseRepository repository;
    private String courseId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_lessons);

        courseId = getIntent().getStringExtra("COURSE_ID");
        if (courseId == null) {
            Toast.makeText(this, "Lỗi: Không tìm thấy Course ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        repository = new TeacherCourseRepository(this);

        initViews();
        setupRecyclerView();
        loadLessons();
    }

    private void initViews() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        String courseTitle = getIntent().getStringExtra("COURSE_TITLE");
        if (courseTitle != null && !courseTitle.isEmpty()) {
            toolbar.setTitle(courseTitle);
        }

        ExtendedFloatingActionButton fabAddLesson = findViewById(R.id.fabAddLesson);
        fabAddLesson.setOnClickListener(v -> showAddLessonBottomSheet());
    }

    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new TeacherLessonAdapter(viewHolder -> itemTouchHelper.startDrag(viewHolder));
        recyclerView.setAdapter(adapter);

        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void loadLessons() {
        repository.getLessons(courseId).observe(this, resource -> {
            if (resource.isSuccess() && resource.data != null) {
                adapter.setLessons(resource.data);
            } else if (resource.isError()) {
                Toast.makeText(this, "Lỗi tải bài giảng: " + resource.error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddLessonBottomSheet() {
        int maxOrder = 0;
        if (!adapter.getLessons().isEmpty()) {
            for (Lesson lesson : adapter.getLessons()) {
                if (lesson.getOrderIndex() > maxOrder) {
                    maxOrder = lesson.getOrderIndex();
                }
            }
        }
        
        AddLessonBottomSheet bottomSheet = new AddLessonBottomSheet(courseId, maxOrder, this);
        bottomSheet.show(getSupportFragmentManager(), "AddLessonBottomSheet");
    }

    @Override
    public void onLessonAdded(Lesson lesson) {
        repository.addLesson(lesson).observe(this, resource -> {
            if (resource.isSuccess() && resource.data != null) {
                adapter.addLesson(resource.data);
                Toast.makeText(this, "Thêm bài giảng thành công", Toast.LENGTH_SHORT).show();
            } else if (resource.isError()) {
                Toast.makeText(this, "Lỗi thêm bài giảng: " + resource.error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            adapter.moveLesson(viewHolder.getAdapterPosition(), target.getAdapterPosition());
            return true;
        }

        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
            // After drag is complete, update order in backend
            updateOrderInBackend();
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            Lesson lesson = adapter.getLesson(position);
            
            repository.deleteLesson(lesson.getId()).observe(ManageLessonsActivity.this, resource -> {
                if (resource.isSuccess()) {
                    adapter.removeLesson(position);
                    Toast.makeText(ManageLessonsActivity.this, "Đã xóa bài giảng", Toast.LENGTH_SHORT).show();
                } else if (resource.isError()) {
                    Toast.makeText(ManageLessonsActivity.this, "Lỗi xóa: " + resource.error.getMessage(), Toast.LENGTH_SHORT).show();
                    adapter.notifyItemChanged(position); // Restore item on UI
                }
            });
        }
        
        @Override
        public boolean isLongPressDragEnabled() {
            return false; // We use explicit drag handle
        }
    });

    private void updateOrderInBackend() {
        for (int i = 0; i < adapter.getLessons().size(); i++) {
            Lesson lesson = adapter.getLesson(i);
            if (lesson.getOrderIndex() != i + 1) {
                lesson.setOrderIndex(i + 1);
                repository.updateLessonOrder(lesson.getId(), i + 1);
            }
        }
    }
}
