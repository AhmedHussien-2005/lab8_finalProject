package Backend;

import java.util.*;

public class PerformanceAnalytics {

    public static double getCourseAverageQuizScore(String courseId) {
        List<User> users = JsonDatabaseManager.loadUsers();
        List<Double> scores = new ArrayList<>();

        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            if (user instanceof Student) {
                Student student = (Student) user;
                if (student.getEnrolledCourses().contains(courseId)) {
                    double avgScore = student.getAverageQuizScoreForCourse(courseId);
                    if (avgScore > 0) {
                        scores.add(avgScore);
                    }
                }
            }
        }

        if (scores.isEmpty()) {
            return 0.0;
        }

        double sum = 0;
        for (double score : scores) {
            sum += score;
        }
        return sum / scores.size();
    }

    public static double getCourseAverageCompletion(String courseId) {
        List<User> users = JsonDatabaseManager.loadUsers();
        List<Course> courses = JsonDatabaseManager.loadCourses();

        Course course = null;
        for (int i = 0; i < courses.size(); i++) {
            Course c = courses.get(i);
            if (c.getCourseId().equals(courseId)) {
                course = c;
                break;
            }
        }

        if (course == null || course.getLessons().isEmpty()) {
            return 0.0;
        }

        List<Integer> completionPercentages = new ArrayList<>();
        int totalLessons = course.getLessons().size();

        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            if (user instanceof Student) {
                Student student = (Student) user;
                if (student.getEnrolledCourses().contains(courseId)) {
                    int progress = student.getCourseProgress(courseId, totalLessons);
                    completionPercentages.add(progress);
                }
            }
        }

        if (completionPercentages.isEmpty()) {
            return 0.0;
        }

        int sum = 0;
        for (int i = 0; i < completionPercentages.size(); i++) {
            sum += completionPercentages.get(i);
        }

        return (double) sum / completionPercentages.size();
    }


    public static Map<String, Double> getLessonQuizAverages(String courseId) {
        Map<String, Double> lessonAverages = new HashMap<>();
        List<User> users = JsonDatabaseManager.loadUsers();
        List<Course> courses = JsonDatabaseManager.loadCourses();

        Course course = null;
        for (int i = 0; i < courses.size(); i++) {
            Course c = courses.get(i);
            if (c.getCourseId().equals(courseId)) {
                course = c;
                break;
            }
        }

        if (course == null) {
            return lessonAverages;
        }

        List<Lesson> lessons = course.getLessons();
        for (int i = 0; i < lessons.size(); i++) {
            Lesson lesson = lessons.get(i);
            List<Integer> scores = new ArrayList<>();

            for (int j = 0; j < users.size(); j++) {
                User user = users.get(j);
                if (user instanceof Student) {
                    Student student = (Student) user;
                    if (student.getEnrolledCourses().contains(courseId)) {
                        QuizResult best = student.getBestQuizResult(lesson.getLessonId());
                        if (best != null) {
                            scores.add(best.getScore());
                        }
                    }
                }
            }

            if (!scores.isEmpty()) {
                int sum = 0;
                for (int k = 0; k < scores.size(); k++) {
                    sum += scores.get(k);
                }
                double average = (double) sum / scores.size();
                lessonAverages.put(lesson.getTitle(), average);
            } else {
                lessonAverages.put(lesson.getTitle(), 0.0);
            }
        }

        return lessonAverages;
    }


    public static List<StudentPerformance> getStudentPerformances(String courseId) {
        List<StudentPerformance> performances = new ArrayList<>();
        List<User> users = JsonDatabaseManager.loadUsers();
        List<Course> courses = JsonDatabaseManager.loadCourses();

        Course course = null;
        for (int i = 0; i < courses.size(); i++) {
            Course c = courses.get(i);
            if (c.getCourseId().equals(courseId)) {
                course = c;
                break;
            }
        }

        if (course == null) {
            return performances;
        }

        int totalLessons = course.getLessons().size();

        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            if (user instanceof Student) {
                Student student = (Student) user;
                if (student.getEnrolledCourses().contains(courseId)) {
                    int completionPercent = student.getCourseProgress(courseId, totalLessons);
                    double avgQuizScore = student.getAverageQuizScoreForCourse(courseId);

                    StudentPerformance perf = new StudentPerformance(
                            student.getUsername(),
                            student.getUserId(),
                            completionPercent,
                            avgQuizScore
                    );
                    performances.add(perf);
                }
            }
        }

        return performances;
    }


    public static class StudentPerformance {
        private String studentName;
        private String studentId;
        private int completionPercent;
        private double averageQuizScore;

        public StudentPerformance(String studentName, String studentId, int completionPercent, double averageQuizScore) {
            this.studentName = studentName;
            this.studentId = studentId;
            this.completionPercent = completionPercent;
            this.averageQuizScore = averageQuizScore;
        }

        public String getStudentName() {
            return studentName;
        }

        public String getStudentId() {
            return studentId;
        }

        public int getCompletionPercent() {
            return completionPercent;
        }

        public double getAverageQuizScore() {
            return averageQuizScore;
        }

        @Override
        public String toString() {
            return studentName + " - Completion: " + completionPercent + "%, Average Quiz: " + String.format("%.1f", averageQuizScore) + "%";
        }
    }
}