package com.app.learning.data.repository;

import android.content.Context;

import com.app.learning.data.api.ApiClient;
import com.app.learning.data.api.CourseApi;
import com.app.learning.data.api.ExamApi;
import com.app.learning.data.model.ExamAttemptModel;
import com.app.learning.data.model.Lesson;
import com.app.learning.data.model.QuizQuestionModel;
import com.app.learning.utils.SessionManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExamRepository {

    public interface ExamQuestionsCallback {
        void onSuccess(List<QuizQuestionModel> questions, Map<String, String> lessonMap);
        void onError(String error);
    }

    public interface ExamAttemptsCallback {
        void onSuccess(List<ExamAttemptModel> attempts);
        void onError(String error);
    }

    public interface SubmitExamCallback {
        void onSuccess(ExamAttemptModel attempt);
        void onError(String error);
    }

    private final ExamApi examApi;
    private final CourseApi courseApi;
    private final SessionManager sessionManager;

    public ExamRepository(Context context) {
        this.examApi = ApiClient.getInstance().createService(ExamApi.class);
        this.courseApi = ApiClient.getInstance().createService(CourseApi.class);
        this.sessionManager = SessionManager.getInstance(context);
    }

    public void loadExamQuestions(String courseId, ExamQuestionsCallback callback) {
        // Step 1: Load all lessons for the course to get titles
        courseApi.getLessonsByCourseId("eq." + courseId, "order_index.asc").enqueue(new Callback<List<Lesson>>() {
            @Override
            public void onResponse(Call<List<Lesson>> call, Response<List<Lesson>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Lesson> lessons = response.body();
                    Map<String, String> lessonMap = new HashMap<>();
                    for (Lesson l : lessons) {
                        lessonMap.put(l.getId(), l.getTitle());
                    }

                    // Step 2: Fetch all quizzes in this course
                    examApi.getQuizzesForCourse("*,lessons!inner(course_id)", "eq." + courseId).enqueue(new Callback<List<QuizQuestionModel>>() {
                        @Override
                        public void onResponse(Call<List<QuizQuestionModel>> call, Response<List<QuizQuestionModel>> quizResponse) {
                            if (quizResponse.isSuccessful() && quizResponse.body() != null) {
                                callback.onSuccess(quizResponse.body(), lessonMap);
                            } else {
                                callback.onError("Lỗi tải câu hỏi thi: " + quizResponse.code());
                            }
                        }

                        @Override
                        public void onFailure(Call<List<QuizQuestionModel>> call, Throwable t) {
                            callback.onError("Lỗi kết nối mạng: " + t.getMessage());
                        }
                    });

                } else {
                    callback.onError("Lỗi tải danh sách bài học: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<Lesson>> call, Throwable t) {
                callback.onError("Lỗi kết nối mạng: " + t.getMessage());
            }
        });
    }

    public void loadExamAttempts(String courseId, ExamAttemptsCallback callback) {
        String userId = sessionManager.getUserId();
        if (userId == null) {
            callback.onError("Người dùng chưa đăng nhập");
            return;
        }

        examApi.getExamAttempts("eq." + userId, "eq." + courseId, "attempt_number.desc").enqueue(new Callback<List<ExamAttemptModel>>() {
            @Override
            public void onResponse(Call<List<ExamAttemptModel>> call, Response<List<ExamAttemptModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Lỗi tải danh sách lượt thi: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<ExamAttemptModel>> call, Throwable t) {
                callback.onError("Lỗi kết nối mạng: " + t.getMessage());
            }
        });
    }

    public void submitExamAttempt(String courseId, int score, boolean passed, int attemptNumber, SubmitExamCallback callback) {
        String userId = sessionManager.getUserId();
        if (userId == null) {
            callback.onError("Người dùng chưa đăng nhập");
            return;
        }

        ExamAttemptModel attempt = new ExamAttemptModel(userId, courseId, score, passed, attemptNumber);

        examApi.submitExamAttempt(attempt, "return=representation").enqueue(new Callback<List<ExamAttemptModel>>() {
            @Override
            public void onResponse(Call<List<ExamAttemptModel>> call, Response<List<ExamAttemptModel>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    ExamAttemptModel result = response.body().get(0);

                    // If passed, update course enrollment progress to 100%
                    if (passed) {
                        Map<String, Object> progressBody = new HashMap<>();
                        progressBody.put("progress_percent", 100);
                        examApi.updateEnrollmentProgress("eq." + userId, "eq." + courseId, progressBody).enqueue(new Callback<Void>() {
                            @Override
                            public void onResponse(Call<Void> call, Response<Void> resResponse) {
                                callback.onSuccess(result);
                            }

                            @Override
                            public void onFailure(Call<Void> call, Throwable t) {
                                // Still succeed the exam submission even if progress update fails
                                callback.onSuccess(result);
                            }
                        });
                    } else {
                        callback.onSuccess(result);
                    }
                } else {
                    callback.onError("Lỗi nộp bài thi: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<List<ExamAttemptModel>> call, Throwable t) {
                callback.onError("Lỗi kết nối mạng: " + t.getMessage());
            }
        });
    }
}
