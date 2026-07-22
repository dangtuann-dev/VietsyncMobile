package com.app.learning.ui.teacher.course;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vietsyncmobile.R;
import com.app.learning.data.model.Lesson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TeacherLessonAdapter extends RecyclerView.Adapter<TeacherLessonAdapter.ViewHolder> {

    private List<Lesson> lessons = new ArrayList<>();
    private final OnStartDragListener dragStartListener;

    public interface OnStartDragListener {
        void onStartDrag(RecyclerView.ViewHolder viewHolder);
    }

    public TeacherLessonAdapter(OnStartDragListener dragStartListener) {
        this.dragStartListener = dragStartListener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
        // Sort by order_index just in case
        Collections.sort(this.lessons, (o1, o2) -> Integer.compare(o1.getOrderIndex(), o2.getOrderIndex()));
        notifyDataSetChanged();
    }

    public void addLesson(Lesson lesson) {
        lessons.add(lesson);
        notifyItemInserted(lessons.size() - 1);
    }

    public void removeLesson(int position) {
        lessons.remove(position);
        notifyItemRemoved(position);
    }

    public void moveLesson(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(lessons, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(lessons, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    public List<Lesson> getLessons() {
        return lessons;
    }

    public Lesson getLesson(int position) {
        return lessons.get(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_teacher_lesson, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Lesson lesson = lessons.get(position);
        holder.tvTitle.setText((position + 1) + ". " + lesson.getTitle());
        holder.tvDuration.setText(lesson.getDuration() + " phút");
        holder.tvType.setText(lesson.getType().toUpperCase());

        holder.ivDragHandle.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                dragStartListener.onStartDrag(holder);
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return lessons.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivDragHandle;
        TextView tvTitle, tvDuration, tvType;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivDragHandle = itemView.findViewById(R.id.ivDragHandle);
            tvTitle = itemView.findViewById(R.id.tvLessonTitle);
            tvDuration = itemView.findViewById(R.id.tvLessonDuration);
            tvType = itemView.findViewById(R.id.tvLessonType);
        }
    }
}
