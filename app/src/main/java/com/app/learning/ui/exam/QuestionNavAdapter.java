package com.app.learning.ui.exam;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vietsyncmobile.R;
import com.app.learning.data.model.QuizQuestionModel;

import java.util.ArrayList;
import java.util.List;

public class QuestionNavAdapter extends RecyclerView.Adapter<QuestionNavAdapter.NavViewHolder> {

    public interface OnQuestionNavListener {
        void onQuestionClick(int index);
    }

    private final List<QuizQuestionModel> questions = new ArrayList<>();
    private final OnQuestionNavListener listener;
    private int currentIndex = 0;

    public QuestionNavAdapter(OnQuestionNavListener listener) {
        this.listener = listener;
    }

    public void setQuestions(List<QuizQuestionModel> questions, int currentIndex) {
        this.questions.clear();
        if (questions != null) {
            this.questions.addAll(questions);
        }
        this.currentIndex = currentIndex;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public NavViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // We can dynamically create a simple TextView for the grid cell or inflate a small item layout.
        // A simple text view with padding, shape background is extremely easy and robust to define.
        TextView textView = new TextView(parent.getContext());
        textView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                120 // height in pixels
        ));
        textView.setGravity(android.view.Gravity.CENTER);
        textView.setTextSize(16);
        textView.setTypeface(null, android.graphics.Typeface.BOLD);
        return new NavViewHolder(textView);
    }

    @Override
    public void onBindViewHolder(@NonNull NavViewHolder holder, int position) {
        QuizQuestionModel model = questions.get(position);
        TextView textView = (TextView) holder.itemView;
        textView.setText(String.valueOf(position + 1));

        boolean isCurrent = (position == currentIndex);
        boolean isAnswered = (model.getUserAnswer() != null && !model.getUserAnswer().trim().isEmpty());

        // Background & Text colors depending on state
        if (isCurrent) {
            textView.setBackgroundResource(R.drawable.bg_circle_primary_light);
            textView.setTextColor(textView.getContext().getResources().getColor(android.R.color.white));
        } else if (isAnswered) {
            // Use green background for answered questions
            android.graphics.drawable.GradientDrawable answeredBg = new android.graphics.drawable.GradientDrawable();
            answeredBg.setShape(android.graphics.drawable.GradientDrawable.OVAL);
            answeredBg.setColor(0xFF10B981); // Emerald Green
            textView.setBackground(answeredBg);
            textView.setTextColor(0xFFFFFFFF);
        } else {
            // Default grey outline for unanswered questions
            android.graphics.drawable.GradientDrawable defaultBg = new android.graphics.drawable.GradientDrawable();
            defaultBg.setShape(android.graphics.drawable.GradientDrawable.OVAL);
            defaultBg.setColor(0xFF334155); // Slate Grey
            textView.setBackground(defaultBg);
            textView.setTextColor(0xFF94A3B8);
        }

        textView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onQuestionClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    static class NavViewHolder extends RecyclerView.ViewHolder {
        public NavViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
