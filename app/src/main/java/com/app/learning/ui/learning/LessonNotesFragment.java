package com.app.learning.ui.learning;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vietsyncmobile.R;
import com.app.learning.data.local.AppDatabase;
import com.app.learning.data.local.NoteDao;
import com.app.learning.data.local.NoteEntity;
import com.app.learning.data.repository.NoteRepository;
import com.app.learning.ui.learning.player.PlayerManager;
import com.app.learning.ui.note.AddNoteDialog;
import com.app.learning.ui.note.NoteAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class LessonNotesFragment extends Fragment {

    private RecyclerView rvNotes;
    private TextView tvEmpty;
    private FloatingActionButton fabAddNote;

    private NoteAdapter adapter;
    private NoteDao noteDao;
    private NoteRepository noteRepository;

    private String courseId;
    private String lessonId;
    private String lessonTitle;

    public static LessonNotesFragment newInstance(String courseId, String lessonId, String lessonTitle) {
        LessonNotesFragment fragment = new LessonNotesFragment();
        Bundle args = new Bundle();
        args.putString("course_id", courseId);
        args.putString("lesson_id", lessonId);
        args.putString("lesson_title", lessonTitle);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lesson_notes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvNotes = view.findViewById(R.id.rvNotes);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        fabAddNote = view.findViewById(R.id.fabAddNote);

        courseId = getArguments() != null ? getArguments().getString("course_id") : "c0eebc99-9c0b-4ef8-bb6d-6bb9bd380001";
        lessonId = getArguments() != null ? getArguments().getString("lesson_id") : "d0eebc99-9c0b-4ef8-bb6d-6bb9bd380011";
        lessonTitle = getArguments() != null ? getArguments().getString("lesson_title") : "Bài học";

        noteDao = AppDatabase.getInstance(requireContext()).noteDao();
        noteRepository = new NoteRepository(requireContext());

        adapter = new NoteAdapter(new NoteAdapter.OnNoteClickListener() {
            @Override
            public void onSeekTo(long timestampSeconds) {
                PlayerManager.getInstance(requireContext()).seekTo(timestampSeconds * 1000);
            }

            @Override
            public void onDelete(NoteEntity note) {
                noteRepository.deleteNote(note);
                Toast.makeText(requireContext(), "Đã xóa ghi chú", Toast.LENGTH_SHORT).show();
            }
        });

        rvNotes.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvNotes.setAdapter(adapter);

        fabAddNote.setOnClickListener(v -> {
            long currentPosSec = PlayerManager.getInstance(requireContext()).getCurrentPosition() / 1000;
            AddNoteDialog dialog = new AddNoteDialog(currentPosSec, (noteText, color) -> {
                NoteEntity note = new NoteEntity(courseId, lessonId, lessonTitle, noteText, currentPosSec, color, System.currentTimeMillis());
                noteRepository.addNote(note);
                Toast.makeText(requireContext(), "Đã lưu ghi chú!", Toast.LENGTH_SHORT).show();
            });
            dialog.show(getChildFragmentManager(), "add_note");
        });

        noteDao.getNotesByLesson(lessonId).observe(getViewLifecycleOwner(), notes -> {
            if (notes == null || notes.isEmpty()) {
                tvEmpty.setVisibility(View.VISIBLE);
                adapter.setNotes(notes);
            } else {
                tvEmpty.setVisibility(View.GONE);
                adapter.setNotes(notes);
            }
        });
    }
}
