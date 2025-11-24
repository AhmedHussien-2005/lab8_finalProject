package Backend;

import java.util.ArrayList;
import java.util.List;

public class Student extends User {
    private List<String> enrolledCourses;
    private List<String> completedLessons;
    private List<QuizResult> allQuizResults;
    private List<Certificate> certificates;

    public Student(String userId, String username, String email, String passwordHash) {
        super(userId, "student", username, email, passwordHash);
        this.enrolledCourses = new ArrayList<>();
        this.completedLessons = new ArrayList<>();
        this.allQuizResults = new ArrayList<>();
        this.certificates = new ArrayList<>();
    }

    public Student(String userId, String username, String email, String passwordHash, List<String> enrolledCourses, List<String> completedLessons, List<QuizResult> allQuizResults) {
        super(userId, "student", username, email, passwordHash);

        if(enrolledCourses == null) {
            this.enrolledCourses = new ArrayList<>();
        } else {
            this.enrolledCourses = enrolledCourses;
        }

        if(completedLessons == null) {
            this.completedLessons = new ArrayList<>();
        } else {
            this.completedLessons = completedLessons;
        }

        if(allQuizResults == null) {
            this.allQuizResults = new ArrayList<>();
        } else {
            this.allQuizResults = allQuizResults;
        }

        this.certificates = new ArrayList<>();
    }

    public Student(String userId, String username, String email, String passwordHash, List<String> enrolledCourses, List<String> completedLessons, List<QuizResult> allQuizResults, List<Certificate> certificates) {
        super(userId, "student", username, email, passwordHash);

        if(enrolledCourses == null) {
            this.enrolledCourses = new ArrayList<>();
        } else {
            this.enrolledCourses = enrolledCourses;
        }

        if(completedLessons == null) {
            this.completedLessons = new ArrayList<>();
        } else {
            this.completedLessons = completedLessons;
        }

        if(allQuizResults == null) {
            this.allQuizResults = new ArrayList<>();
        } else {
            this.allQuizResults = allQuizResults;
        }

        if(certificates == null) {
            this.certificates = new ArrayList<>();
        } else {
            this.certificates = certificates;
        }
    }

    public List<String> getEnrolledCourses() {
        return enrolledCourses;
    }

    public List<String> getCompletedLessons() {
        return completedLessons;
    }

    public List<QuizResult> getAllQuizResults() {
        return allQuizResults;
    }

    public List<Certificate> getCertificates() { // NEW
        return certificates;
    }

    public boolean enrollInCourse(String courseId) {
        if (!enrolledCourses.contains(courseId)) {
            enrolledCourses.add(courseId);
            return true;
        }
        return false;
    }

    public void markLessonCompleted(String lessonId) {
        if (!completedLessons.contains(lessonId)) {
            completedLessons.add(lessonId);
        }
    }

    public boolean isLessonCompleted(String lessonId) {
        return completedLessons.contains(lessonId);
    }

    public int getCourseProgress(String courseId, int totalLessons) {
        if (totalLessons <= 0) {
            return 0;
        }

        int completedCount = 0;
        List<Course> courses = JsonDatabaseManager.loadCourses();

        for (int i = 0; i < courses.size(); i++) {
            Course course = courses.get(i);

            if (course.getCourseId().equals(courseId)) {

                List<Lesson> lessons = course.getLessons();
                for (int j = 0; j < lessons.size(); j++) {
                    Lesson lesson = lessons.get(j);

                    if (completedLessons.contains(lesson.getLessonId())) {
                        completedCount++;
                    }
                }

                break;
            }
        }

        return (completedCount * 100) / totalLessons;
    }


    public List<String> getCompletedLessonsForCourse(String courseId) {
        List<String> courseCompletedLessons = new ArrayList<>();
        List<Course> courses = JsonDatabaseManager.loadCourses();

        for (int i = 0; i < courses.size(); i++) {
            Course course = courses.get(i);
            if (course.getCourseId().equals(courseId)) {
                List<Lesson> lessons = course.getLessons();
                for (int j = 0; j < lessons.size(); j++) {
                    Lesson lesson = lessons.get(j);
                    if (completedLessons.contains(lesson.getLessonId())) {
                        courseCompletedLessons.add(lesson.getLessonId());
                    }
                }
                break;
            }
        }

        return courseCompletedLessons;
    }


    public void recordQuizAttempt(QuizResult result) {
        allQuizResults.add(result);
    }

    public boolean hasPassedQuiz(String lessonId) {
        for (int i = 0; i < allQuizResults.size(); i++) {
            QuizResult result = allQuizResults.get(i);
            if (result.getLessonId().equals(lessonId) && result.isPassed()) {
                return true;
            }
        }
        return false;
    }


    public List<QuizResult> getQuizResultsForLesson(String lessonId) {
        List<QuizResult> lessonResults = new ArrayList<>();

        for (int i = 0; i < allQuizResults.size(); i++) {
            QuizResult result = allQuizResults.get(i);
            if (result.getLessonId().equals(lessonId)) {
                lessonResults.add(result);
            }
        }

        return lessonResults;
    }

    public QuizResult getBestQuizResult(String lessonId) {
        List<QuizResult> lessonResults = getQuizResultsForLesson(lessonId);

        if (lessonResults.isEmpty()) {
            return null;
        }

        QuizResult best = lessonResults.get(0);
        for (int i = 0; i < lessonResults.size(); i++) {
            QuizResult result = lessonResults.get(i);
            if (result.getScore() > best.getScore()) {
                best = result;
            }
        }

        return best;
    }


    public double getAverageQuizScoreForCourse(String courseId) {
        List<QuizResult> courseResults = new ArrayList<>();

        for (int i = 0; i < allQuizResults.size(); i++) {
            QuizResult result = allQuizResults.get(i);
            if (result.getCourseId().equals(courseId)) {
                courseResults.add(result);
            }
        }

        if (courseResults.isEmpty()) {
            return 0.0;
        }

        int Score = 0;
        for (int i = 0; i < courseResults.size(); i++) {
            QuizResult result = courseResults.get(i);
            Score += result.getScore();
        }

        return (double) Score / courseResults.size();
    }


    public List<QuizResult> getQuizResultsForCourse(String courseId) {
        List<QuizResult> courseResults = new ArrayList<>();

        for (int i = 0; i < allQuizResults.size(); i++) {
            QuizResult result = allQuizResults.get(i);
            if (result.getCourseId().equals(courseId)) {
                courseResults.add(result);
            }
        }

        return courseResults;
    }


    public void addCertificate(Certificate certificate) {
        certificates.add(certificate);
    }

    public Certificate getCertificateForCourse(String courseId) {
        for (int i = 0; i < certificates.size(); i++) {
            Certificate cert = certificates.get(i);
            if (cert.getCourseId().equals(courseId)) {
                return cert;
            }
        }
        return null;
    }

    public boolean hasCertificateForCourse(String courseId) {
        return getCertificateForCourse(courseId) != null;
    }
}
