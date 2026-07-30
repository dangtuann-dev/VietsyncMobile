package com.app.learning.ui.exam;

import android.app.Application;
import android.os.CountDownTimer;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.app.learning.data.model.ExamAttemptModel;
import com.app.learning.data.model.QuizQuestionModel;
import com.app.learning.data.repository.ExamRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ExamViewModel extends AndroidViewModel {

    private final ExamRepository repository;

    private final MutableLiveData<List<QuizQuestionModel>> questions = new MutableLiveData<>();
    private final MutableLiveData<Map<String, String>> lessonMap = new MutableLiveData<>();
    private final MutableLiveData<Integer> currentIndex = new MutableLiveData<>(0);
    private final MutableLiveData<Long> timeLeftInMillis = new MutableLiveData<>(1800000L); // 30 minutes
    private final MutableLiveData<Boolean> isTimerFinished = new MutableLiveData<>(false);
    private final MutableLiveData<List<ExamAttemptModel>> examAttempts = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<ExamAttemptModel> submitSuccess = new MutableLiveData<>();

    private CountDownTimer countDownTimer;

    public ExamViewModel(@NonNull Application application) {
        super(application);
        this.repository = new ExamRepository(application);
    }

    public LiveData<List<QuizQuestionModel>> getQuestions() { return questions; }
    public LiveData<Map<String, String>> getLessonMap() { return lessonMap; }
    public LiveData<Integer> getCurrentIndex() { return currentIndex; }
    public LiveData<Long> getTimeLeftInMillis() { return timeLeftInMillis; }
    public LiveData<Boolean> getIsTimerFinished() { return isTimerFinished; }
    public LiveData<List<ExamAttemptModel>> getExamAttempts() { return examAttempts; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<String> getErrorMessage() { return errorMessage; }
    public LiveData<ExamAttemptModel> getSubmitSuccess() { return submitSuccess; }

    public void setCurrentIndex(int index) {
        currentIndex.setValue(index);
    }

    public void startTimer(long durationMillis) {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(durationMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis.setValue(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                timeLeftInMillis.setValue(0L);
                isTimerFinished.setValue(true);
            }
        }.start();
    }

    public void stopTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    public void loadExamQuestions(String courseId, boolean random) {
        isLoading.setValue(true);
        repository.loadExamQuestions(courseId, new ExamRepository.ExamQuestionsCallback() {
            @Override
            public void onSuccess(List<QuizQuestionModel> quizPool, Map<String, String> lessons) {
                isLoading.setValue(false);
                lessonMap.setValue(lessons);

                if (quizPool.isEmpty()) {
                    errorMessage.setValue("Khóa học chưa có câu hỏi ôn tập nào.");
                    return;
                }

                List<QuizQuestionModel> selected = new ArrayList<>(quizPool);
                if (random) {
                    Collections.shuffle(selected);
                }

                // Pick between 20 to 30 questions (default: 25 questions, or size of pool if pool is smaller)
                int examSize = Math.min(selected.size(), 25);
                if (examSize > 30) examSize = 30;
                if (examSize < 20 && selected.size() >= 20) examSize = 20;

                questions.setValue(selected.subList(0, examSize));
            }

            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                errorMessage.setValue(error);
            }
        });
    }

    public void checkRetakeEligibility(String courseId) {
        isLoading.setValue(true);
        repository.loadExamAttempts(courseId, new ExamRepository.ExamAttemptsCallback() {
            @Override
            public void onSuccess(List<ExamAttemptModel> attempts) {
                isLoading.setValue(false);
                examAttempts.setValue(attempts);
            }

            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                errorMessage.setValue(error);
            }
        });
    }

    public void submitExam(String courseId) {
        List<QuizQuestionModel> quizList = questions.getValue();
        if (quizList == null || quizList.isEmpty()) {
            errorMessage.setValue("Không có câu hỏi để chấm điểm.");
            return;
        }

        int score = 0;
        for (QuizQuestionModel q : quizList) {
            if (q.isCorrect()) {
                score++;
            }
        }

        int total = quizList.size();
        float percentage = ((float) score / total) * 100;
        boolean passed = percentage >= 70.0f;

        // Calculate attempt number
        int nextAttempt = 1;
        List<ExamAttemptModel> history = examAttempts.getValue();
        if (history != null && !history.isEmpty()) {
            nextAttempt = history.get(0).getAttemptNumber() + 1; // List is sorted desc by attempt_number
        }

        isLoading.setValue(true);
        repository.submitExamAttempt(courseId, score, passed, nextAttempt, new ExamRepository.SubmitExamCallback() {
            @Override
            public void onSuccess(ExamAttemptModel attempt) {
                isLoading.setValue(false);
                submitSuccess.setValue(attempt);
            }

            @Override
            public void onError(String error) {
                isLoading.setValue(false);
                errorMessage.setValue(error);
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        stopTimer();
    }
}
