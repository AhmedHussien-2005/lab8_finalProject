package Backend;

import java.util.ArrayList;
import java.util.List;

public class Course {
    private String courseId;
    private String title;
    private String description;
    private String instructorId;
    private List<Lesson> lessons;
    private List<String> students;
    private String status;

    public Course(String courseId, String title, String description, String instructorId) {
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.instructorId = instructorId;
        this.lessons = new ArrayList<>();
        this.students = new ArrayList<>();
        this.status = "Pending";
    }

    public Course(String courseId, String title, String description, String instructorId, List<Lesson> lessons, List<String> students, String status) {
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.instructorId = instructorId;
        if (lessons != null) {
            this.lessons = lessons;
        } else {
            this.lessons = new ArrayList<>();
        }
        if (students != null) {
            this.students = students;
        } else {
            this.students = new ArrayList<>();
        }
        this.status = status;
    }

    public void addLesson(Lesson lesson) {
        lessons.add(lesson);
    }

    public boolean removeLesson(String lessonId) {
        return lessons.removeIf(l -> l.getLessonId().equals(lessonId));
    }

    public Lesson getLessonById(String lessonId) {
        for (int i = 0; i < lessons.size(); i++) {
            if (lessons.get(i).getLessonId().equals(lessonId)) {
                return lessons.get(i);
            }
        }
        return null;
    }

    public boolean addStudent(String studentId) {
        if (!students.contains(studentId)) {
            students.add(studentId);
            return true;
        }
        return false;
    }

    public boolean removeStudent(String studentId) {
        return students.remove(studentId);
    }

    public String getCourseId() {
        return courseId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getInstructorId() {
        return instructorId;
    }

    public List<Lesson> getLessons() {
        return lessons;
    }

    public List<String> getStudents() {
        return students;
    }

    public String getStatus(){
        return this.status;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(String status){
        this.status = status;
    }

    @Override
    public String toString() {
        return title + " - " + description + " - " + status +" (" + lessons.size() + " lessons)";
    }
}