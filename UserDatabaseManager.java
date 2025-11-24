package Backend;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class UserDatabaseManager extends AbstractDatabaseManager<User> {

    private static final String USERS_FILE = "users.json";
    private static UserDatabaseManager instance;

    private UserDatabaseManager() {
        super(USERS_FILE);
    }

    public static UserDatabaseManager getInstance() {
        if (instance == null) {
            instance = new UserDatabaseManager();
        }
        return instance;
    }

    @Override
    protected Object convertToJSON(User user) {
        JSONObject obj = new JSONObject();
        obj.put("userId", user.getUserId());
        obj.put("role", user.getRole());
        obj.put("username", user.getUsername());
        obj.put("email", user.getEmail());
        obj.put("passwordHash", user.getPasswordHash());

        if (user instanceof Student) {
            Student student = (Student) user;

            JSONArray enrolled = new JSONArray();
            enrolled.addAll(student.getEnrolledCourses());
            obj.put("enrolledCourses", enrolled);

            JSONArray completed = new JSONArray();
            completed.addAll(student.getCompletedLessons());
            obj.put("completedLessons", completed);

            JSONArray quizResultsArr = new JSONArray();
            for (QuizResult qr : student.getAllQuizResults()) {
                quizResultsArr.add(convertQuizResultToJSON(qr));
            }
            obj.put("quizResults", quizResultsArr);

            JSONArray certificatesArr = new JSONArray();
            for (Certificate cert : student.getCertificates()) {
                certificatesArr.add(convertCertificateToJSON(cert));
            }
            obj.put("certificates", certificatesArr);

        } else if (user instanceof Instructor) {
            Instructor instructor = (Instructor) user;
            JSONArray created = new JSONArray();
            created.addAll(instructor.getCreatedCourses());
            obj.put("createdCourses", created);
        }

        return obj;
    }

    @Override
    protected User convertFromJSON(Object jsonObject) {
        JSONObject obj = (JSONObject) jsonObject;

        String userId = (String) obj.getOrDefault("userId", UUID.randomUUID().toString());
        String role = (String) obj.getOrDefault("role", "student");
        String username = (String) obj.getOrDefault("username", "unknown");
        String email = (String) obj.getOrDefault("email", "");
        String passwordHash = (String) obj.getOrDefault("passwordHash", "");

        if ("student".equalsIgnoreCase(role)) {
            return createStudentFromJSON(obj, userId, username, email, passwordHash);
        } else if ("instructor".equalsIgnoreCase(role)) {
            return createInstructorFromJSON(obj, userId, username, email, passwordHash);
        } else if ("admin".equalsIgnoreCase(role)) {
            return new Admin(userId, username, email, passwordHash);
        } else {
            return new User(userId, role, username, email, passwordHash);
        }
    }

    @Override
    protected List<User> createEmptyList() {
        return new ArrayList<>();
    }


    private Student createStudentFromJSON(JSONObject obj, String userId, String username, String email, String passwordHash) {
        List<String> enrolled = new ArrayList<>();
        Object enrolledObj = obj.get("enrolledCourses");
        if (enrolledObj instanceof JSONArray) {
            for (Object it : (JSONArray) enrolledObj) {
                enrolled.add((String) it);
            }
        }

        List<String> completedLessons = new ArrayList<>();
        Object completedObj = obj.get("completedLessons");
        if (completedObj instanceof JSONArray) {
            for (Object it : (JSONArray) completedObj) {
                completedLessons.add((String) it);
            }
        }

        List<QuizResult> allQuizResults = new ArrayList<>();
        Object quizResultsObj = obj.get("quizResults");
        if (quizResultsObj instanceof JSONArray) {
            for (Object resultObj : (JSONArray) quizResultsObj) {
                QuizResult qr = convertQuizResultFromJSON(resultObj);
                if (qr != null) {
                    allQuizResults.add(qr);
                }
            }
        }

        List<Certificate> certificates = new ArrayList<>();
        Object certificatesObj = obj.get("certificates");
        if (certificatesObj instanceof JSONArray) {
            for (Object certObj : (JSONArray) certificatesObj) {
                Certificate cert = convertCertificateFromJSON(certObj);
                if (cert != null) {
                    certificates.add(cert);
                }
            }
        }

        return new Student(userId, username, email, passwordHash, enrolled, completedLessons, allQuizResults, certificates);
    }

    private Instructor createInstructorFromJSON(JSONObject obj, String userId, String username, String email, String passwordHash) {
        List<String> created = new ArrayList<>();
        Object createdObj = obj.get("createdCourses");
        if (createdObj instanceof JSONArray) {
            for (Object it : (JSONArray) createdObj) {
                created.add((String) it);
            }
        }
        return new Instructor(userId, username, email, passwordHash, created);
    }

    private JSONObject convertQuizResultToJSON(QuizResult qr) {
        JSONObject resultObj = new JSONObject();
        resultObj.put("resultId", qr.getResultId());
        resultObj.put("studentId", qr.getStudentId());
        resultObj.put("quizId", qr.getQuizId());
        resultObj.put("lessonId", qr.getLessonId());
        resultObj.put("courseId", qr.getCourseId());
        resultObj.put("score", qr.getScore());
        resultObj.put("timestamp", qr.getTimestamp());
        resultObj.put("passed", qr.isPassed());

        JSONArray answersArr = new JSONArray();
        answersArr.addAll(qr.getStudentAnswers());
        resultObj.put("studentAnswers", answersArr);

        return resultObj;
    }

    private QuizResult convertQuizResultFromJSON(Object resultObj) {
        JSONObject rJson = (JSONObject) resultObj;

        String resultId = (String) rJson.getOrDefault("resultId", "QR" + System.currentTimeMillis());
        String studentId = (String) rJson.getOrDefault("studentId", "");
        String quizId = (String) rJson.getOrDefault("quizId", "");
        String lessonId = (String) rJson.getOrDefault("lessonId", "");
        String courseId = (String) rJson.getOrDefault("courseId", "");
        int score = ((Long) rJson.getOrDefault("score", 0L)).intValue();
        String timestamp = (String) rJson.getOrDefault("timestamp", "");
        boolean passed = (Boolean) rJson.getOrDefault("passed", false);

        List<Integer> studentAnswers = new ArrayList<>();
        Object answersObj = rJson.get("studentAnswers");
        if (answersObj instanceof JSONArray) {
            for (Object ans : (JSONArray) answersObj) {
                studentAnswers.add(((Long) ans).intValue());
            }
        }

        return new QuizResult(resultId, studentId, quizId, lessonId,
                courseId, score, timestamp, studentAnswers, passed);
    }

    private JSONObject convertCertificateToJSON(Certificate cert) {
        JSONObject certObj = new JSONObject();
        certObj.put("certificateId", cert.getCertificateId());
        certObj.put("studentId", cert.getStudentId());
        certObj.put("studentName", cert.getStudentName());
        certObj.put("courseId", cert.getCourseId());
        certObj.put("courseTitle", cert.getCourseTitle());
        certObj.put("issueDate", cert.getIssueDate());
        certObj.put("finalScore", cert.getFinalScore());
        certObj.put("completionPercentage", cert.getCompletionPercentage());

        return certObj;
    }

    private Certificate convertCertificateFromJSON(Object certObj) {
        JSONObject cJson = (JSONObject) certObj;

        String certificateId = (String) cJson.getOrDefault("certificateId", "CERT" + System.currentTimeMillis());
        String studentId = (String) cJson.getOrDefault("studentId", "");
        String studentName = (String) cJson.getOrDefault("studentName", "");
        String courseId = (String) cJson.getOrDefault("courseId", "");
        String courseTitle = (String) cJson.getOrDefault("courseTitle", "");
        String issueDate = (String) cJson.getOrDefault("issueDate", "");
        int finalScore = ((Long) cJson.getOrDefault("finalScore", 0L)).intValue();
        int completionPercentage = ((Long) cJson.getOrDefault("completionPercentage", 100L)).intValue();

        return new Certificate(certificateId, studentId, studentName, courseId,
                courseTitle, issueDate, finalScore, completionPercentage);
    }


    public User findByUsernameOrEmail(String usernameOrEmail) {
        List<User> users = load();
        for (User user : users) {
            if (user.getUsername().equalsIgnoreCase(usernameOrEmail) ||
                    user.getEmail().equalsIgnoreCase(usernameOrEmail)) {
                return user;
            }
        }
        return null;
    }

    public boolean userIdExists(String userId) {
        List<User> users = load();
        for (User user : users) {
            if (user.getUserId().equals(userId)) {
                return true;
            }
        }
        return false;
    }
}