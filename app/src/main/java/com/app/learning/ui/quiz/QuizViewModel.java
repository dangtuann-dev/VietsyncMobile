package com.app.learning.ui.quiz;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.app.learning.data.model.QuizQuestionModel;
import com.app.learning.data.repository.QuizRepository;

import java.util.ArrayList;
import java.util.List;

public class QuizViewModel extends AndroidViewModel {

    private final QuizRepository repository;
    private final MutableLiveData<List<QuizQuestionModel>> questions = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Integer> currentIndex = new MutableLiveData<>(0);
    private final MutableLiveData<Boolean> isFinished = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public QuizViewModel(@NonNull Application application) {
        super(application);
        this.repository = new QuizRepository(application);
    }

    public LiveData<List<QuizQuestionModel>> getQuestions() { return questions; }
    public LiveData<Integer> getCurrentIndex() { return currentIndex; }
    public LiveData<Boolean> getIsFinished() { return isFinished; }
    public LiveData<String> getErrorMessage() { return errorMessage; }

    public void loadQuiz(String lessonId) {
        repository.loadQuizzes(lessonId, new QuizRepository.QuizListCallback() {
            @Override
            public void onSuccess(List<QuizQuestionModel> list) {
                questions.setValue(list);
                currentIndex.setValue(0);
            }

            @Override
            public void onError(String error) {
                errorMessage.setValue(error);
            }
        });
    }

    public void submitAnswer(String answer) {
        List<QuizQuestionModel> list = questions.getValue();
        Integer idx = currentIndex.getValue();
        if (list != null && idx != null && idx < list.size()) {
            QuizQuestionModel q = list.get(idx);
            q.setUserAnswer(answer);

            repository.submitAttempt(q.getId(), answer, q.isCorrect());

            if (idx + 1 < list.size()) {
                currentIndex.setValue(idx + 1);
            } else {
                isFinished.setValue(true);
            }
        }
    }

    public int calculateScore() {
        List<QuizQuestionModel> list = questions.getValue();
        if (list == null) return 0;
        int score = 0;
        for (QuizQuestionModel q : list) {
            if (q.isCorrect()) score++;
        }
        return score;
    }
}
