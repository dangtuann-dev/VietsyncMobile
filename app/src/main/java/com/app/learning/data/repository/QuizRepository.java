package com.app.learning.data.repository;

import android.content.Context;

import com.app.learning.data.api.ApiClient;
import com.app.learning.data.api.QuizApi;
import com.app.learning.data.model.QuizQuestionModel;
import com.app.learning.utils.SessionManager;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizRepository {

    public interface QuizListCallback {
        void onSuccess(List<QuizQuestionModel> questions);
        void onError(String error);
    }

    private final QuizApi quizApi;
    private final SessionManager sessionManager;

    public QuizRepository(Context context) {
        this.quizApi = ApiClient.getInstance().createService(QuizApi.class);
        this.sessionManager = new SessionManager(context);
    }

    public void loadQuizzes(String lessonId, QuizListCallback callback) {
        quizApi.getQuizzesByLesson("eq." + lessonId, "*").enqueue(new Callback<List<QuizQuestionModel>>() {
            @Override
            public void onResponse(Call<List<QuizQuestionModel>> call, Response<List<QuizQuestionModel>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onSuccess(createFallbackQuizzes(lessonId));
                }
            }

            @Override
            public void onFailure(Call<List<QuizQuestionModel>> call, Throwable t) {
                callback.onSuccess(createFallbackQuizzes(lessonId));
            }
        });
    }

    public void submitAttempt(String quizId, String selectedAnswer, boolean isCorrect) {
        String userId = sessionManager.getUserId();
        if (userId == null || userId.isEmpty()) return;

        JsonObject body = new JsonObject();
        body.addProperty("user_id", userId);
        body.addProperty("quiz_id", quizId);
        body.addProperty("selected_answer", selectedAnswer);
        body.addProperty("is_correct", isCorrect);

        quizApi.submitQuizAttempt(body).enqueue(new Callback<List<JsonObject>>() {
            @Override
            public void onResponse(Call<List<JsonObject>> call, Response<List<JsonObject>> response) {}

            @Override
            public void onFailure(Call<List<JsonObject>> call, Throwable t) {}
        });
    }

    private List<QuizQuestionModel> createFallbackQuizzes(String lessonId) {
        List<QuizQuestionModel> list = new ArrayList<>();

        list.add(new QuizQuestionModel(
                "q1", lessonId,
                "Trong mô hình MVVM, thành phần nào chịu trách nhiệm giữ logic xử lý dữ liệu?",
                Arrays.asList("View (Activity/Fragment)", "ViewModel", "Model (Repository/API)", "Intent"),
                "Model (Repository/API)",
                "Model quản lý trực tiếp dữ liệu nguồn từ DB hoặc Remote API.",
                "MULTIPLE_CHOICE"
        ));

        list.add(new QuizQuestionModel(
                "q2", lessonId,
                "Lớp ExoPlayer trong Android Media3 hỗ trợ phát cả video HLS (.m3u8) và MP4.",
                Arrays.asList("Đúng", "Sai"),
                "Đúng",
                "ExoPlayer tích hợp media3-datasource-hls cho phép phát mượt mà các stream HLS và tệp MP4.",
                "TRUE_FALSE"
        ));

        list.add(new QuizQuestionModel(
                "q3", lessonId,
                "Điền tên thư viện hỗ trợ truyền dữ liệu bất đồng bộ LiveData/StateFlow của Android Jetpack?",
                new ArrayList<>(),
                "LiveData",
                "LiveData là lớp giữ dữ liệu có thể quan sát theo vòng đời (lifecycle-aware).",
                "SHORT_ANSWER"
        ));

        return list;
    }
}
