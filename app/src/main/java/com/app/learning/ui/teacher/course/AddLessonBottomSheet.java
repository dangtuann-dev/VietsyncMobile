package com.app.learning.ui.teacher.course;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.vietsyncmobile.R;
import com.app.learning.data.model.Lesson;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class AddLessonBottomSheet extends BottomSheetDialogFragment {

    private String courseId;
    private int currentMaxOrder;
    private OnLessonAddedListener listener;

    public interface OnLessonAddedListener {
        void onLessonAdded(Lesson lesson);
    }

    public AddLessonBottomSheet(String courseId, int currentMaxOrder, OnLessonAddedListener listener) {
        this.courseId = courseId;
        this.currentMaxOrder = currentMaxOrder;
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_add_lesson, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AutoCompleteTextView spinLessonType = view.findViewById(R.id.spinLessonType);
        TextInputEditText etLessonTitle = view.findViewById(R.id.etLessonTitle);
        TextInputEditText etLessonUrl = view.findViewById(R.id.etLessonUrl);
        TextInputEditText etLessonDuration = view.findViewById(R.id.etLessonDuration);
        MaterialButton btnSaveLesson = view.findViewById(R.id.btnSaveLesson);

        String[] types = {"Video", "Quiz", "PDF", "Text"};
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, types);
        spinLessonType.setAdapter(typeAdapter);

        btnSaveLesson.setOnClickListener(v -> {
            String title = etLessonTitle.getText().toString();
            String url = etLessonUrl.getText().toString();
            String durationStr = etLessonDuration.getText().toString();
            String type = spinLessonType.getText().toString();

            if (title.isEmpty() || type.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            int duration = durationStr.isEmpty() ? 0 : Integer.parseInt(durationStr);

            Lesson lesson = new Lesson();
            lesson.setCourseId(courseId);
            lesson.setTitle(title);
            lesson.setVideoUrl(url);
            lesson.setDuration(duration);
            lesson.setType(type.toLowerCase());
            lesson.setOrderIndex(currentMaxOrder + 1);

            if (listener != null) {
                listener.onLessonAdded(lesson);
            }
            dismiss();
        });
    }
}
