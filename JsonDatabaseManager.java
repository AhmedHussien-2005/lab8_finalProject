package Backend;

import java.util.List;

public class JsonDatabaseManager {

    private static final UserDatabaseManager userManager = UserDatabaseManager.getInstance();
    private static final CourseDatabaseManager courseManager = CourseDatabaseManager.getInstance();


    public static void saveUsers(List<User> users) {
        userManager.save(users);
    }

    public static List<User> loadUsers() {
        return userManager.load();
    }

    public static User findByUsernameOrEmail(String usernameOrEmail) {
        return userManager.findByUsernameOrEmail(usernameOrEmail);
    }

    public static boolean userIdExists(String userId) {
        return userManager.userIdExists(userId);
    }


    public static void saveCourses(List<Course> courses) {
        courseManager.save(courses);
    }

    public static List<Course> loadCourses() {
        return courseManager.load();
    }

    public static boolean courseIdExists(String courseId) {
        return courseManager.courseIdExists(courseId);
    }

    public static Course findCourseById(String courseId) {
        return courseManager.findById(courseId);
    }


    public static void clearAll() {
        userManager.clear();
        courseManager.clear();
        System.out.println("[Database] JSON data cleared");
    }

    public static void printStats() {
        System.out.println(" Database States ");
        System.out.println("Users: " + userManager.getCount());
        System.out.println("Courses: " + courseManager.getCount());
    }
}