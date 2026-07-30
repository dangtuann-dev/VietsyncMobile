package com.app.learning.ui.exam;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vietsyncmobile.R;
import com.app.learning.data.model.QuizQuestionModel;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class QuestionPagerAdapter extends RecyclerView.Adapter<QuestionPagerAdapter.QuestionViewHolder> {

    private final List<QuizQuestionModel> questions = new ArrayList<>();

    public void setQuestions(List<QuizQuestionModel> questions) {
        this.questions.clear();
        if (questions != null) {
            this.questions.addAll(questions);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exam_question, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        QuizQuestionModel model = questions.get(position);
        holder.tvQuestionText.setText(model.getQuestion());

        if (model.getImageUrl() != null && !model.getImageUrl().isEmpty()) {
            holder.imgQuestion.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext())
                    .load(model.getImageUrl())
                    .placeholder(R.drawable.ic_logo_placeholder)
                    .into(holder.imgQuestion);
        } else {
            holder.imgQuestion.setVisibility(View.GONE);
        }

        // Dynamically add RadioButtons for options
        holder.rgOptions.removeAllViews();
        List<String> options = model.getOptions();
        if (options != null) {
            for (int i = 0; i < options.size(); i++) {
                String optionText = options.get(i);
                RadioButton radioButton = new RadioButton(holder.itemView.getContext());
                radioButton.setText(optionText);
                radioButton.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.white));
                radioButton.setTextSize(16);
                radioButton.setPadding(8, 12, 8, 12);
                
                // Set layout margins
                RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(
                        RadioGroup.LayoutParams.MATCH_PARENT,
                        RadioGroup.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 8, 0, 8);
                radioButton.setLayoutParams(params);

                // Pre-check if already answered
                if (optionText.equals(model.getUserAnswer())) {
                    radioButton.setChecked(true);
                }

                radioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        model.setUserAnswer(optionText);
                    }
                });

                holder.rgOptions.addView(radioButton);
            }
        }
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    static class QuestionViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestionText;
        ImageView imgQuestion;
        RadioGroup rgOptions;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvQuestionText = itemView.findViewById(R.id.tvQuestionText);
            imgQuestion = itemView.findViewById(R.id.imgQuestion);
            rgOptions = itemView.findViewById(R.id.rgOptions);
        }
    }
}
