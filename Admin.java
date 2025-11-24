package Backend;

import java.util.ArrayList;
import java.util.List;

public class Admin extends User{

    public Admin(String userId,String username,String email,String passwordHash){
        super(userId, "admin", username, email, passwordHash);
    }

    public boolean deleteUser(String userIdRemove){
        List<User> users = JsonDatabaseManager.loadUsers();

        boolean delete = false;
        for(int i = 0; i < users.size(); i++){
            if(users.get(i).getUserId().equalsIgnoreCase(userIdRemove)){
                users.remove(i);
                delete = true;
                break;
            }
        }
        if(delete){
            JsonDatabaseManager.saveUsers(users);
            System.out.println("Admin Remove User ID : "+userIdRemove);
            return true;
        }
        return false;
    }

    public boolean deleteCourse(String courseIdRemove){
        List<Course> courses = JsonDatabaseManager.loadCourses();

        boolean delete = false;
        for(int i = 0; i < courses.size(); i++){
            if(courses.get(i).getCourseId().equalsIgnoreCase(courseIdRemove)){
                courses.remove(i);
                delete = true;
            }
        }
        if(delete){
            JsonDatabaseManager.saveCourses(courses);
            System.out.println("Admin Delete Course ID: "+courseIdRemove);
            return true;
        }
        return false;
    }

    public List<Course> getPendingCourses(){
        List<Course> courses = JsonDatabaseManager.loadCourses();
        List<Course> pendingCourses = new ArrayList<>();

        for(int i = 0; i < courses.size(); i++){
            if(courses.get(i).getStatus().equalsIgnoreCase("pending")){
                pendingCourses.add(courses.get(i));
            }
        }
        return pendingCourses;
    }

    public boolean setCourseStatus(String courseId, boolean state){
        List<Course> courses = JsonDatabaseManager.loadCourses();

        boolean found = false;
        for(int i = 0; i < courses.size(); i++){
            if(courses.get(i).getCourseId().equalsIgnoreCase(courseId)){
                if(state){
                    courses.get(i).setStatus("Approved");
                    System.out.println("Admin Approve Course ID: " + courseId);
                }
                else{
                    courses.get(i).setStatus("Rejected");
                    System.out.println("Admin Rejecte Course ID: " + courseId);
                }
                found = true;
                break;
            }
        }
        if(found){
            JsonDatabaseManager.saveCourses(courses);
            return true;
        }
        System.out.println("Course ID: "+ courseId +" , Not Found");
        return false;
    }
}
