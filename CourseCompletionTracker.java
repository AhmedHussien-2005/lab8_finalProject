package Backend;

import java.util.List;


public class CourseCompletionTracker {

    public static boolean isCourseCompleted(Student student, Course course) {
        if (!student.getEnrolledCourses().contains(course.getCourseId())) {
            return false;
        }

        for (int i = 0; i < course.getLessons().size(); i++) {
            Lesson lesson = course.getLessons().get(i);

            if (!student.isLessonCompleted(lesson.getLessonId())) {
                return false;
            }

            if (lesson.getQuiz() != null && lesson.getQuiz().isRequired()) {
                if (!student.hasPassedQuiz(lesson.getLessonId())) {
                    return false;
                }
            }
        }

        return true;
    }



    public static Certificate generateCertificate(Student student, Course course) {
        if (!isCourseCompleted(student, course)) {
            return null;
        }

        int finalScore = (int) student.getAverageQuizScoreForCourse(course.getCourseId());


        int completionPercentage = 100;

        Certificate certificate = new Certificate(
                student.getUserId(),
                student.getUsername(),
                course.getCourseId(),
                course.getTitle(),
                finalScore,
                completionPercentage
        );


        student.addCertificate(certificate);

        List<User> users = JsonDatabaseManager.loadUsers();
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUserId().equals(student.getUserId())) {
                users.set(i, student);
                break;
            }
        }
        JsonDatabaseManager.saveUsers(users);

        return certificate;
    }

    public static boolean hasCertificate(Student student, String courseId) {
        List<Certificate> certs = student.getCertificates();

        for (int i = 0; i < certs.size(); i++) {
            Certificate cert = certs.get(i);
            if (cert.getCourseId().equals(courseId)) {
                return true;
            }
        }

        return false;
    }


    public static Certificate getCertificate(Student student, String courseId) {
        List<Certificate> certificates = student.getCertificates();

        for (int i = 0; i < certificates.size(); i++) {
            Certificate cert = certificates.get(i);
            if (cert.getCourseId().equals(courseId)) {
                return cert;
            }
        }
        return null;
    }

    public static int getCompletionPercentage(Student student, Course course) {
        if (course.getLessons().isEmpty()) {
            return 0;
        }

        int completedCount = 0;
        List<Lesson> lessons = course.getLessons();

        for (int i = 0; i < lessons.size(); i++) {
            Lesson lesson = lessons.get(i);
            if (student.isLessonCompleted(lesson.getLessonId())) {
                completedCount++;
            }
        }

        return (completedCount * 100) / lessons.size();
    }


    public static String getCompletionStatus(Student student, Course course) {
        if (isCourseCompleted(student, course)) {
            return "✓ COMPLETED";
        }

        int percentage = getCompletionPercentage(student, course);
        return percentage + "% Complete";
    }
}