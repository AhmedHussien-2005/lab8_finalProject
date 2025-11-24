package Backend;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class CourseDatabaseManager extends AbstractDatabaseManager<Course> {

    private static final String COURSES_FILE = "courses.json";
    private static CourseDatabaseManager instance;

    private CourseDatabaseManager() {
        super(COURSES_FILE);
    }

    public static CourseDatabaseManager getInstance() {
        if (instance == null) {
            instance = new CourseDatabaseManager();
        }
        return instance;
    }

    @Override
    protected Object convertToJSON(Course course) {
        JSONObject obj = new JSONObject();
        obj.put("courseId", course.getCourseId());
        obj.put("title", course.getTitle());
        obj.put("description", course.getDescription());
        obj.put("instructorId", course.getInstructorId());
        obj.put("status", course.getStatus());

        JSONArray lessonsArr = new JSONArray();
        for (Lesson lesson : course.getLessons()) {
            lessonsArr.add(convertLessonToJSON(lesson));
        }
        obj.put("lessons", lessonsArr);

        JSONArray studentsArr = new JSONArray();
        studentsArr.addAll(course.getStudents());
        obj.put("students", studentsArr);

        return obj;
    }

    @Override
    protected Course convertFromJSON(Object jsonObject) {
        JSONObject obj = (JSONObject) jsonObject;

        String courseId = (String) obj.getOrDefault("courseId", UUID.randomUUID().toString());
        String title = (String) obj.getOrDefault("title", "Untitled");
        String description = (String) obj.getOrDefault("description", "");
        String instructorId = (String) obj.getOrDefault("instructorId", "");
        String status = (String) obj.getOrDefault("status", "Pending");

        List<Lesson> lessons = new ArrayList<>();
        Object lessonsObj = obj.get("lessons");
        if (lessonsObj instanceof JSONArray) {
            for (Object lObj : (JSONArray) lessonsObj) {
                Lesson lesson = convertLessonFromJSON(lObj);
                if (lesson != null) {
                    lessons.add(lesson);
                }
            }
        }

        List<String> students = new ArrayList<>();
        Object studentsObj = obj.get("students");
        if (studentsObj instanceof JSONArray) {
            for (Object s : (JSONArray) studentsObj) {
                students.add((String) s);
            }
        }

        return new Course(courseId, title, description, instructorId, lessons, students, status);
    }

    @Override
    protected List<Course> createEmptyList() {
        return new ArrayList<>();
    }


    private JSONObject convertLessonToJSON(Lesson lesson) {
        JSONObject lessonObj = new JSONObject();
        lessonObj.put("lessonId", lesson.getLessonId());
        lessonObj.put("title", lesson.getTitle());
        lessonObj.put("content", lesson.getContent());

        JSONArray resArr = new JSONArray();
        resArr.addAll(lesson.getResources());
        lessonObj.put("resources", resArr);

        if (lesson.getQuiz() != null) {
            lessonObj.put("quiz", convertQuizToJSON(lesson.getQuiz()));
        }

        return lessonObj;
    }

    private Lesson convertLessonFromJSON(Object jsonObject) {
        JSONObject lJson = (JSONObject) jsonObject;

        String lessonId = (String) lJson.getOrDefault("lessonId", UUID.randomUUID().toString());
        String title = (String) lJson.getOrDefault("title", "Untitled Lesson");
        String content = (String) lJson.getOrDefault("content", "");

        List<String> resources = new ArrayList<>();
        Object resObj = lJson.get("resources");
        if (resObj instanceof JSONArray) {
            for (Object r : (JSONArray) resObj) {
                resources.add((String) r);
            }
        }

        Quiz quiz = null;
        Object quizObj = lJson.get("quiz");
        if (quizObj instanceof JSONObject) {
            quiz = convertQuizFromJSON(quizObj, lessonId);
        }

        return new Lesson(lessonId, title, content, resources, quiz);
    }


    private JSONObject convertQuizToJSON(Quiz quiz) {
        JSONObject quizObj = new JSONObject();
        quizObj.put("quizId", quiz.getQuizId());
        quizObj.put("lessonId", quiz.getLessonId());
        quizObj.put("passingScore", quiz.getPassingScore());
        quizObj.put("required", quiz.isRequired());

        JSONArray questionsArr = new JSONArray();
        for (Question question : quiz.getQuestions()) {
            questionsArr.add(convertQuestionToJSON(question));
        }
        quizObj.put("questions", questionsArr);

        return quizObj;
    }

    private Quiz convertQuizFromJSON(Object jsonObject, String lessonId) {
        JSONObject qJson = (JSONObject) jsonObject;

        String quizId = (String) qJson.getOrDefault("quizId", "Q" + System.currentTimeMillis());
        String qLessonId = (String) qJson.getOrDefault("lessonId", lessonId);
        int passingScore = ((Long) qJson.getOrDefault("passingScore", 70L)).intValue();
        boolean required = (Boolean) qJson.getOrDefault("required", true);

        List<Question> questions = new ArrayList<>();
        Object questionsObj = qJson.get("questions");
        if (questionsObj instanceof JSONArray) {
            for (Object qObj : (JSONArray) questionsObj) {
                Question question = convertQuestionFromJSON(qObj);
                if (question != null) {
                    questions.add(question);
                }
            }
        }

        return new Quiz(quizId, qLessonId, questions, passingScore, required);
    }


    private JSONObject convertQuestionToJSON(Question question) {
        JSONObject questionObj = new JSONObject();
        questionObj.put("questionId", question.getQuestionId());
        questionObj.put("questionText", question.getQuestionText());
        questionObj.put("correctAnswerIndex", question.getCorrectAnswerIndex());

        JSONArray optionsArr = new JSONArray();
        optionsArr.addAll(question.getOptions());
        questionObj.put("options", optionsArr);

        return questionObj;
    }

    private Question convertQuestionFromJSON(Object jsonObject) {
        JSONObject questionJson = (JSONObject) jsonObject;

        String questionId = (String) questionJson.getOrDefault("questionId", "QU" + System.currentTimeMillis());
        String questionText = (String) questionJson.getOrDefault("questionText", "");
        int correctAnswerIndex = ((Long) questionJson.getOrDefault("correctAnswerIndex", 0L)).intValue();

        List<String> options = new ArrayList<>();
        Object optionsObj = questionJson.get("options");
        if (optionsObj instanceof JSONArray) {
            for (Object opt : (JSONArray) optionsObj) {
                options.add((String) opt);
            }
        }

        return new Question(questionId, questionText, options, correctAnswerIndex);
    }


    public boolean courseIdExists(String courseId) {
        List<Course> courses = load();
        for (Course course : courses) {
            if (course.getCourseId().equals(courseId)) {
                return true;
            }
        }
        return false;
    }

    public Course findById(String courseId) {
        List<Course> courses = load();
        for (Course course : courses) {
            if (course.getCourseId().equals(courseId)) {
                return course;
            }
        }
        return null;
    }
}