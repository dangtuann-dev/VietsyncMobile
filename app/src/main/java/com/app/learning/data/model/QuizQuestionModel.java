package com.app.learning.data.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;
import java.util.List;

public class QuizQuestionModel implements Serializable {

    @SerializedName("id")
    private String id;

    @SerializedName("lesson_id")
    private String lessonId;

    @SerializedName("question")
    private String question;

    @SerializedName("options")
    private List<String> options;

    @SerializedName("correct_answer")
    private String correctAnswer;

    @SerializedName("explanation")
    private String explanation;

    @SerializedName("image_url")
    private String imageUrl;

    @SerializedName("question_type")
    private String questionType; // MULTIPLE_CHOICE, TRUE_FALSE, SHORT_ANSWER, IMAGE_QUESTION

    private String userAnswer;

    public QuizQuestionModel() {}

    public QuizQuestionModel(String id, String lessonId, String question, List<String> options, String correctAnswer, String explanation, String questionType) {
        this.id = id;
        this.lessonId = lessonId;
        this.question = question;
        this.options = options;
        this.correctAnswer = correctAnswer;
        this.explanation = explanation;
        this.questionType = questionType;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getLessonId() { return lessonId; }
    public void setLessonId(String lessonId) { this.lessonId = lessonId; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }

    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getQuestionType() { return questionType; }
    public void setQuestionType(String questionType) { this.questionType = questionType; }

    public String getUserAnswer() { return userAnswer; }
    public void setUserAnswer(String userAnswer) { this.userAnswer = userAnswer; }

    public boolean isCorrect() {
        return userAnswer != null && userAnswer.trim().equalsIgnoreCase(correctAnswer != null ? correctAnswer.trim() : "");
    }
}
