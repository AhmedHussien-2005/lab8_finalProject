package Backend;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class QuizResult {
    private String resultId;
    private String studentId;
    private String quizId;
    private String lessonId;
    private String courseId;
    private int score;
    private String timestamp;
    private List<Integer> studentAnswers;
    private boolean passed;

    public QuizResult(String studentId, String quizId, String lessonId, String courseId, int score, List<Integer> studentAnswers, boolean passed) {
        this.resultId = "QR" + System.currentTimeMillis();
        this.studentId = studentId;
        this.quizId = quizId;
        this.lessonId = lessonId;
        this.courseId = courseId;
        this.score = score;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        if (studentAnswers != null) {
            this.studentAnswers = studentAnswers;
        } else {
            this.studentAnswers = new ArrayList<>();
        }
        this.passed = passed;
    }

    public QuizResult(String resultId, String studentId, String quizId, String lessonId, String courseId, int score, String timestamp, List<Integer> studentAnswers, boolean passed) {
        this.resultId = resultId;
        this.studentId = studentId;
        this.quizId = quizId;
        this.lessonId = lessonId;
        this.courseId = courseId;
        this.score = score;
        this.timestamp = timestamp;
        if (studentAnswers != null) {
            this.studentAnswers = studentAnswers;
        } else {
            this.studentAnswers = new ArrayList<>();
        }
        this.passed = passed;
    }

    public String getResultId() {
        return resultId;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getQuizId() {
        return quizId;
    }

    public String getLessonId() {
        return lessonId;
    }

    public String getCourseId() {
        return courseId;
    }

    public int getScore() {
        return score;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public List<Integer> getStudentAnswers() {
        return studentAnswers;
    }

    public boolean isPassed() {
        return passed;
    }

    @Override
    public String toString() {
        String state;
        if (passed) {
            state = "PASSED";
        } else {
            state = "FAILED";
        }

        return "Quiz Result - Score: " + score + "% (" + state + ")";
    }
}