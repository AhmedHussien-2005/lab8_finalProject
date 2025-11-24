package Backend;

import java.util.ArrayList;
import java.util.List;

public class Question {
    private String questionId;
    private String questionText;
    private List<String> options;
    private int correctAnswerIndex;

    public Question(String questionId, String questionText, List<String> options, int correctAnswerIndex) {
        this.questionId = questionId;
        this.questionText = questionText;

        if(options == null){
            this.options = new ArrayList<>();
        } else {
            this.options = options;
        }

        this.correctAnswerIndex = correctAnswerIndex;
    }

    public String getQuestionId() {
        return questionId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public List<String> getOptions() {
        return options;
    }

    public int getCorrectAnswerIndex() {
        return correctAnswerIndex;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

    public void setCorrectAnswerIndex(int correctAnswerIndex) {
        this.correctAnswerIndex = correctAnswerIndex;
    }

    public boolean isCorrectAnswer(int answerIndex) {
        return answerIndex == correctAnswerIndex;
    }

    @Override
    public String toString() {
        return questionText;
    }
}