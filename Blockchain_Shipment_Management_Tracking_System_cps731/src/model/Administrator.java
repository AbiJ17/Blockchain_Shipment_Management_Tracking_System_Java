package model;

import java.util.ArrayList;
import java.util.List;

public class Administrator extends User {

    private int adminID;
    private List<User> userList = new ArrayList<>();

    public Administrator() {
        setRole("ADMIN");
    }

    public Administrator(int userID,
            String username,
            String password,
            String email,
            int adminID) {

        super(userID, username, password, "ADMIN", email);
        this.adminID = adminID;
    }

    // ---- Domain behaviour ----

    public void addUser(User user) {
        if (user != null && !userList.contains(user)) {
            userList.add(user);
        }
    }

    public void removeUser(int userID) {
        userList.removeIf(u -> u.getUserID() == userID);
    }

    public void assignRole(User user, String role) {
        if (user != null) {
            user.setRole(role);
        }
    }

    // ---- Getters / setters ----

    public int getAdminID() {
        return adminID;
    }

    public void setAdminID(int adminID) {
        this.adminID = adminID;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }
}
