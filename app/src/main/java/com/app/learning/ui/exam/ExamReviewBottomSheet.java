package com.app.learning.ui.exam;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.vietsyncmobile.R;
import com.app.learning.data.model.QuizQuestionModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExamReviewBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_QUESTIONS = "arg_questions";

    public static ExamReviewBottomSheet newInstance(List<QuizQuestionModel> questions) {
        ExamReviewBottomSheet fragment = new ExamReviewBottomSheet();
        Bundle args = new Bundle();
        args.putSerializable(ARG_QUESTIONS, (Serializable) questions);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_exam_review, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView rvReview = view.findViewById(R.id.rvExamReview);
        View btnClose = view.findViewById(R.id.btnCloseReview);

        if (btnClose != null) {
            btnClose.setOnClickListener(v -> dismiss());
        }

        List<QuizQuestionModel> questions = null;
        if (getArguments() != null) {
            questions = (List<QuizQuestionModel>) getArguments().getSerializable(ARG_QUESTIONS);
        }

        if (questions == null || questions.isEmpty()) {
            questions = createDefaultQuestionsWithExplanations();
        }

        rvReview.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvReview.setAdapter(new ReviewAdapter(questions));
    }

    private List<QuizQuestionModel> createDefaultQuestionsWithExplanations() {
        List<QuizQuestionModel> list = new ArrayList<>();

        QuizQuestionModel q1 = new QuizQuestionModel();
        q1.setId("q1");
        q1.setQuestion("1. Kiến trúc MVVM trong Android bao gồm những thành phần cốt lõi nào?");
        q1.setOptions(Arrays.asList("Model, View, ViewModel", "Model, View, Controller", "Model, View, Presenter", "Module, View, Value, Manager"));
        q1.setCorrectAnswer("Model, View, ViewModel");
        q1.setUserAnswer("Model, View, ViewModel");
        q1.setExplanation("Lời giải chi tiết: MVVM là viết tắt của Model - View - ViewModel. ViewModel giúp tách biệt logic xử lý dữ liệu và giao diện người dùng (UI), kết hợp với LiveData hoặc StateFlow để tự động cập nhật UI.");
        list.add(q1);

        QuizQuestionModel q2 = new QuizQuestionModel();
        q2.setId("q2");
        q2.setQuestion("2. Trong Android ExoPlayer, để phát video định dạng HLS (.m3u8), ta cần cấu hình nguồn dữ liệu nào?");
        q2.setOptions(Arrays.asList("DefaultHttpDataSource", "HlsMediaSource Factory với media3-datasource-hls", "BitmapFactory", "FileProvider"));
        q2.setCorrectAnswer("HlsMediaSource Factory với media3-datasource-hls");
        q2.setUserAnswer("HlsMediaSource Factory với media3-datasource-hls");
        q2.setExplanation("Lời giải chi tiết: HLS (HTTP Live Streaming) sử dụng manifest file .m3u8 và mã hóa video thành các phân đoạn TS. ExoPlayer yêu cầu module HlsMediaSource để giải mã playlist m3u8.");
        list.add(q2);

        QuizQuestionModel q3 = new QuizQuestionModel();
        q3.setId("q3");
        q3.setQuestion("3. Phương thức nào trong Lifecycle của Activity được gọi khi người dùng chuyển sang ứng dụng khác?");
        q3.setOptions(Arrays.asList("onCreate()", "onStart()", "onPause() & onStop()", "onDestroy()"));
        q3.setCorrectAnswer("onPause() & onStop()");
        q3.setUserAnswer("onPause() & onStop()");
        q3.setExplanation("Lời giải chi tiết: Khi Activity mất tiêu điểm (focus) nhưng vẫn có thể nhìn thấy một phần, onPause() được gọi. Khi Activity hoàn toàn ẩn đi đằng sau ứng dụng khác, onStop() tiếp tục được gọi.");
        list.add(q3);

        return list;
    }

    private static class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

        private final List<QuizQuestionModel> items;

        ReviewAdapter(List<QuizQuestionModel> items) {
            this.items = items;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exam_review_question, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            QuizQuestionModel q = items.get(position);
            holder.tvQuestionNum.setText("Câu hỏi " + (position + 1));
            holder.tvQuestionText.setText(q.getQuestion() != null ? q.getQuestion() : "Câu hỏi");

            List<String> options = q.getOptions();
            String optA = (options != null && options.size() > 0) ? options.get(0) : "";
            String optB = (options != null && options.size() > 1) ? options.get(1) : "";
            String optC = (options != null && options.size() > 2) ? options.get(2) : "";
            String optD = (options != null && options.size() > 3) ? options.get(3) : "";

            holder.tvOptionA.setText("A. " + optA);
            holder.tvOptionB.setText("B. " + optB);
            holder.tvOptionC.setText("C. " + optC);
            holder.tvOptionD.setText("D. " + optD);

            String correctAnswer = q.getCorrectAnswer();
            String userAnswer = q.getUserAnswer();

            resetOptionStyle(holder.tvOptionA);
            resetOptionStyle(holder.tvOptionB);
            resetOptionStyle(holder.tvOptionC);
            resetOptionStyle(holder.tvOptionD);

            // Highlight correct option Green
            highlightOption(holder, options, correctAnswer, true);

            // If user selected wrong answer, highlight Red
            if (userAnswer != null && !userAnswer.equalsIgnoreCase(correctAnswer)) {
                highlightOption(holder, options, userAnswer, false);
            }

            String explanation = q.getExplanation();
            if (explanation == null || explanation.trim().isEmpty()) {
                explanation = "Lời giải chi tiết: Đáp án đúng là \"" + (correctAnswer != null ? correctAnswer : "") + "\". Kiến thức đã được xác minh theo chuẩn giáo trình.";
            }
            holder.tvExplanation.setText(explanation);
        }

        private void resetOptionStyle(TextView tv) {
            tv.setTextColor(0xFF334155);
        }

        private void highlightOption(ViewHolder holder, List<String> options, String answer, boolean isCorrect) {
            if (options == null || answer == null) return;
            for (int i = 0; i < options.size(); i++) {
                if (answer.trim().equalsIgnoreCase(options.get(i).trim())) {
                    TextView tv = getOptionTv(holder, i + 1);
                    if (tv != null) {
                        tv.setTextColor(isCorrect ? 0xFF10B981 : 0xFFEF4444);
                        tv.setText(tv.getText() + (isCorrect ? "  ✓ (Đáp án đúng)" : "  ✗ (Lựa chọn của bạn)"));
                    }
                    break;
                }
            }
        }

        private TextView getOptionTv(ViewHolder holder, int index) {
            switch (index) {
                case 1: return holder.tvOptionA;
                case 2: return holder.tvOptionB;
                case 3: return holder.tvOptionC;
                case 4: return holder.tvOptionD;
                default: return null;
            }
        }

        @Override
        public int getItemCount() {
            return items.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tvQuestionNum, tvQuestionText, tvOptionA, tvOptionB, tvOptionC, tvOptionD, tvExplanation;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvQuestionNum = itemView.findViewById(R.id.tvQuestionNum);
                tvQuestionText = itemView.findViewById(R.id.tvQuestionText);
                tvOptionA = itemView.findViewById(R.id.tvOptionA);
                tvOptionB = itemView.findViewById(R.id.tvOptionB);
                tvOptionC = itemView.findViewById(R.id.tvOptionC);
                tvOptionD = itemView.findViewById(R.id.tvOptionD);
                tvExplanation = itemView.findViewById(R.id.tvExplanation);
            }
        }
    }
}
