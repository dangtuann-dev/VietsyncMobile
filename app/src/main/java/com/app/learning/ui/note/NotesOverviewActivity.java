package com.app.learning.ui.note;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vietsyncmobile.R;
import com.app.learning.data.local.AppDatabase;
import com.app.learning.data.local.NoteDao;
import com.app.learning.data.repository.NoteRepository;
import com.app.learning.utils.ShareHelper;

import com.google.android.material.appbar.MaterialToolbar;

import java.io.File;

public class NotesOverviewActivity extends AppCompatActivity {

    public static final String EXTRA_COURSE_ID = "extra_course_id";
    public static final String EXTRA_COURSE_TITLE = "extra_course_title";

    private MaterialToolbar toolbar;
    private ImageButton btnExportNotes;
    private RecyclerView rvAllNotes;

    private NoteAdapter adapter;
    private NoteDao noteDao;
    private NoteRepository noteRepository;

    private String courseId;
    private String courseTitle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_overview);

        toolbar = findViewById(R.id.toolbar);
        btnExportNotes = findViewById(R.id.btnExportNotes);
        rvAllNotes = findViewById(R.id.rvAllNotes);

        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        courseId = getIntent().getStringExtra(EXTRA_COURSE_ID);
        courseTitle = getIntent().getStringExtra(EXTRA_COURSE_TITLE);

        if (courseId == null) courseId = "c0eebc99-9c0b-4ef8-bb6d-6bb9bd380001";
        if (courseTitle == null) courseTitle = "Khóa học Android MVVM";

        noteDao = AppDatabase.getInstance(this).noteDao();
        noteRepository = new NoteRepository(this);

        adapter = new NoteAdapter(new NoteAdapter.OnNoteClickListener() {
            @Override
            public void onSeekTo(long timestampSeconds) {}

            @Override
            public void onDelete(com.app.learning.data.local.NoteEntity note) {
                noteRepository.deleteNote(note);
                Toast.makeText(NotesOverviewActivity.this, "Đã xóa ghi chú", Toast.LENGTH_SHORT).show();
            }
        });

        rvAllNotes.setLayoutManager(new LinearLayoutManager(this));
        rvAllNotes.setAdapter(adapter);

        btnExportNotes.setOnClickListener(v -> {
            noteRepository.exportNotesToText(courseId, courseTitle, new NoteRepository.ExportCallback() {
                @Override
                public void onSuccess(File exportedFile) {
                    Toast.makeText(NotesOverviewActivity.this, "Đã xuất ghi chú ra: " + exportedFile.getName(), Toast.LENGTH_LONG).show();
                    ShareHelper.shareText(NotesOverviewActivity.this, "Ghi chú " + courseTitle, "File ghi chú đã được tạo thành công tại: " + exportedFile.getAbsolutePath());
                }

                @Override
                public void onError(String error) {
                    Toast.makeText(NotesOverviewActivity.this, "Lỗi xuất ghi chú: " + error, Toast.LENGTH_SHORT).show();
                }
            });
        });

        noteDao.getNotesByCourse(courseId).observe(this, notes -> adapter.setNotes(notes));
    }
}
