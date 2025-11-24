package Backend;

import java.util.ArrayList;
import java.util.List;

public class Instructor extends User{

    private List<String> createdCourses;

    public Instructor(String userId,String username,String email,String passwordHash){
        super(userId, "instructor", username, email, passwordHash);
        this.createdCourses = new ArrayList<>();
    }
    public Instructor(String userId,String username,String email,String passwordHash,List<String> createdCourses){
        super(userId, "Instructor", username, email, passwordHash);
        this.createdCourses = createdCourses;
    }

    public void addCreatedCourseId(String courseId){
        this.createdCourses.add(courseId);
    }

    public boolean removeCreatedCourseId(String courseId){
        return this.createdCourses.remove(courseId);
    }

    public List<String> getCreatedCourses(){
        return this.createdCourses;
    }

    public void setCreatedCourses(List<String> createdCourses){
        this.createdCourses = createdCourses;
    }
}
