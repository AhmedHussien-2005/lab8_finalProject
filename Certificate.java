package Backend;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Certificate {
    private String certificateId;
    private String studentId;
    private String studentName;
    private String courseId;
    private String courseTitle;
    private String issueDate;
    private int finalScore;
    private int completionPercentage;

    public Certificate(String studentId, String studentName, String courseId, String courseTitle, int finalScore, int completionPercentage) {
        this.certificateId = "CERT" + System.currentTimeMillis();
        this.studentId = studentId;
        this.studentName = studentName;
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.issueDate = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        this.finalScore = finalScore;
        this.completionPercentage = completionPercentage;
    }

    public Certificate(String certificateId, String studentId, String studentName, String courseId, String courseTitle, String issueDate, int finalScore, int completionPercentage) {
        this.certificateId = certificateId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.issueDate = issueDate;
        this.finalScore = finalScore;
        this.completionPercentage = completionPercentage;
    }

    public String getCertificateId() {
        return certificateId;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getCourseId() {
        return courseId;
    }

    public String getCourseTitle() {
        return courseTitle;
    }

    public String getIssueDate() {
        return issueDate;
    }

    public int getFinalScore() {
        return finalScore;
    }

    public int getCompletionPercentage() {
        return completionPercentage;
    }

    public String getFormattedIssueDate() {
        try {
            LocalDateTime dateTime = LocalDateTime.parse(issueDate, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            return dateTime.format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"));
        } catch (Exception e) {
            return issueDate;
        }
    }

    @Override
    public String toString() {
        return "Certificate - " + courseTitle + " (Issue: " + getFormattedIssueDate() + ")";
    }
}