package Backend;

import java.util.ArrayList;
import java.util.List;

public class Lesson {
    private String lessonId;
    private String title;
    private String content;
    private List<String> resources;
    private Quiz quiz;

    public Lesson(String lessonId, String title, String content) {
        this.lessonId = lessonId;
        this.title = title;
        this.content = content;
        this.resources = new ArrayList<>();
        this.quiz = null;
    }

    public Lesson(String lessonId, String title, String content, List<String> resources) {
        this.lessonId = lessonId;
        this.title = title;
        this.content = content;
        if (resources != null) {
            this.resources = resources;
        } else {
            this.resources = new ArrayList<>();
        }
        this.quiz = null;
    }

    public Lesson(String lessonId, String title, String content, List<String> resources, Quiz quiz) {
        this.lessonId = lessonId;
        this.title = title;
        this.content = content;
        if (resources != null) {
            this.resources = resources;
        } else {
            this.resources = new ArrayList<>();
        }
        this.quiz = quiz;
    }

    public void addResource(String resource) {
        if (!resources.contains(resource)) {
            resources.add(resource);
        }
    }

    public void removeResource(String resource) {
        resources.remove(resource);
    }

    public String getLessonId() {
        return lessonId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public List<String> getResources() {
        return resources;
    }

    public Quiz getQuiz() {
        return quiz;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }


    @Override
    public String toString() {
        return title + " (ID: " + lessonId + ")";
    }
}