package com.app.learning.ui.note;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vietsyncmobile.R;
import com.app.learning.data.local.NoteEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    public interface OnNoteClickListener {
        void onSeekTo(long timestampSeconds);
        void onDelete(NoteEntity note);
    }

    private final List<NoteEntity> noteList = new ArrayList<>();
    private final OnNoteClickListener listener;

    public NoteAdapter(OnNoteClickListener listener) {
        this.listener = listener;
    }

    public void setNotes(List<NoteEntity> notes) {
        this.noteList.clear();
        if (notes != null) {
            this.noteList.addAll(notes);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        NoteEntity note = noteList.get(position);

        long min = note.getTimestampSeconds() / 60;
        long sec = note.getTimestampSeconds() % 60;
        holder.tvTimestamp.setText(String.format(Locale.getDefault(), "%02d:%02d", min, sec));
        holder.tvNoteText.setText(note.getNoteText());

        if (note.getColor() != null && !note.getColor().isEmpty()) {
            try {
                holder.viewColorIndicator.setBackgroundColor(Color.parseColor(note.getColor()));
            } catch (Exception ignored) {}
        }

        holder.tvTimestamp.setOnClickListener(v -> {
            if (listener != null) listener.onSeekTo(note.getTimestampSeconds());
        });

        holder.btnDeleteNote.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(note);
        });
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        View viewColorIndicator;
        TextView tvTimestamp, tvNoteText;
        ImageButton btnDeleteNote;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            viewColorIndicator = itemView.findViewById(R.id.viewColorIndicator);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            tvNoteText = itemView.findViewById(R.id.tvNoteText);
            btnDeleteNote = itemView.findViewById(R.id.btnDeleteNote);
        }
    }
}
