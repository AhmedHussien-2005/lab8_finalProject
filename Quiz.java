package Backend;

import java.util.ArrayList;
import java.util.List;

public class Quiz {
    private String quizId;
    private String lessonId;
    private List<Question> questions;
    private int passingScore;
    private boolean required;

    public Quiz(String quizId, String lessonId, int passingScore) {
        this.quizId = quizId;
        this.lessonId = lessonId;
        this.questions = new ArrayList<>();
        this.passingScore = passingScore;
        this.required = true;
    }

    public Quiz(String quizId, String lessonId, List<Question> questions, int passingScore, boolean required) {
        this.quizId = quizId;
        this.lessonId = lessonId;
        if (questions != null) {
            this.questions = questions;
        } else {
            this.questions = new ArrayList<>();
        }
        this.passingScore = passingScore;
        this.required = required;
    }

    public void addQuestion(Question question) {
        questions.add(question);
    }

    public boolean removeQuestion(String questionId) {
        return questions.removeIf(q -> q.getQuestionId().equals(questionId));
    }

    public int calculateScore(List<Integer> studentAnswers) {
        if (questions.isEmpty() || studentAnswers == null) {
            return 0;
        }

        int Count = 0;
        int totalQuestions = Math.min(questions.size(), studentAnswers.size());

        for (int i = 0; i < totalQuestions; i++) {
            if (questions.get(i).isCorrectAnswer(studentAnswers.get(i))) {
                Count++;
            }
        }

        return (Count * 100) / questions.size();
    }

    public boolean isPassed(int score) {
        return score >= passingScore;
    }

    public String getQuizId() {
        return quizId;
    }

    public String getLessonId() {
        return lessonId;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public int getPassingScore() {
        return passingScore;
    }

    public boolean isRequired() {
        return required;
    }


    public void setPassingScore(int passingScore) {
        this.passingScore = passingScore;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    @Override
    public String toString() {
        return "Quiz for Lesson " + lessonId + " (" + questions.size() + " questions)";
    }
}
