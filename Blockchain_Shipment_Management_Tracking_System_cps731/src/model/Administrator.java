package model;

import java.util.ArrayList;
import java.util.List;

public class Administrator extends User {

    public int adminID; 
    public List<User> userList; 

    public void addUser(User user) {
        if (userList == null) {
            userList = new ArrayList<>();
        }
        userList.add(user);
    }

    public void removeUser(User user) {
        if (userList != null) {
            userList.remove(user);
        }
    }

    public void assignRole(User user, String role) {
        
    }

    public boolean validateCredentials(String username, String password) {
        return false; 
    }

    public int getAdminID() {
        return adminID;
    }

    public void setAdminID(int adminID) {
        this.adminID = adminID;
    }
    
}
