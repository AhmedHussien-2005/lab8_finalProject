package Backend;

import java.util.List;

public class User {
    private String userId;
    private String role;
    private String username;
    private String email;
    private String passwordHash;

    public User(String userId,String role,String username,String email,String passwordHash){
        this.userId = userId;
        this.role = role;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    public boolean signup() {
        if(!email.contains("@") || username.isEmpty() || passwordHash.isEmpty())
            return false;
        List<User> users = JsonDatabaseManager.loadUsers();
        boolean duplicate = checkDuplicateSignup(users);

        if(duplicate){
            System.out.println("Error:: duplicate credentials");
            return false;
        }
        users.add(this);
        JsonDatabaseManager.saveUsers(users);
        return true;
    }

    public boolean login(String usernameOrEmail, String password) {
        List<User> users = JsonDatabaseManager.loadUsers();
        for (int i = 0; i < users.size(); i++) {
            User u = users.get(i);
            if ((u.getEmail().equalsIgnoreCase(usernameOrEmail) || u.getUsername().equalsIgnoreCase(usernameOrEmail))
                    && u.getPasswordHash().equals(password)) {
                return true;
            }
        }
        return false;
    }

    public boolean checkDuplicateSignup(List<User> users) {
        for (int i = 0; i < users.size(); i++) {
            User u = users.get(i);
            if (u.getEmail().equalsIgnoreCase(this.email) || u.getUsername().equalsIgnoreCase(this.username)) {
                return true;
            }
        }
        return false;
    }

    public boolean checkDuplicateLogin(List<User> users, String usernameOrEmail, String passwordHash, String role) {
        for (int i = 0; i < users.size(); i++) {
            User u = users.get(i);
            if ((u.getEmail().equalsIgnoreCase(usernameOrEmail) || u.getUsername().equalsIgnoreCase(usernameOrEmail))
                    && u.getPasswordHash().equalsIgnoreCase(passwordHash)
                    && u.getRole().equalsIgnoreCase(role)) {
                return true;
            }
        }
        return false;
    }


    public void setUserId(String userId){
        this.userId = userId;
    }
    public String getUserId(){
        return this.userId;
    }
    public void setRole(String role){
        this.role = role;
    }
    public String getRole(){
        return this.role;
    }
    public void setUsername(String username){
        this.username = username;
    }
    public String getUsername(){
        return this.username;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public String getEmail(){
        return this.email;
    }
    public void setPasswordHash(String passwordHash){
        this.passwordHash = passwordHash;
    }
    public String getPasswordHash(){
        return this.passwordHash;
    }

}
