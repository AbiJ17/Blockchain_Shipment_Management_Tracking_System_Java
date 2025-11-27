package model;

public class User {

    private int userID;
    private String username;
    private String password;
    private String role;
    private String email;

    // No argument constructor (needed by Shipper(), Buyer(), etc.)
    public User() {
        // defaults
        this.userID = -1;
        this.username = "";
        this.password = "";
        this.role = "";
        this.email = ""; 
    }

    // Existing full constructor used by subclasses and login seed
    public User(int userID,
            String username,
            String password,
            String role,
            String email) {
        this.userID = userID;
        this.username = username;
        this.password = password;
        this.role = role;
        this.email = email;
    }

    // ----- getters & setters -----
    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // ----- simple auth helpers (you can keep whatever you had) -----
    public boolean authenticate(String inputPassword) {
        return password != null && password.equals(inputPassword);
    }
}
