package model;

import java.util.Objects;

public class User {

    private int userID;
    private String username;
    private String password;
    private Role role;
    private String email;

    public User(int userID, String username, String password, Role role, String email) {
        this.userID = userID;
        this.username = username;
        this.password = password;
        this.role = role;
        this.email = email;
    }

    // --- Simple behaviour ---

    public boolean login(String inputPassword) {
        return authenticate(inputPassword);
    }

    public void logout() {
        // could clear session/token in a real app
    }

    public void viewDashboard() {
        // handled by UI in this project – keep as placeholder
    }

    public boolean authenticate(String inputPassword) {
        return Objects.equals(this.password, inputPassword);
    }

    // Very simple authorization based on role + action string
    public boolean authorize(String action) {
        if (role == Role.ADMIN)
            return true;

        if (action.startsWith("SHIPMENT_CREATE") && role == Role.SHIPPER)
            return true;

        if (action.startsWith("SHIPMENT_QUERY")
                && (role == Role.SHIPPER || role == Role.BUYER))
            return true;

        if (action.startsWith("SHIPMENT_UPDATE")
                && (role == Role.SHIPPER
                        || role == Role.LOGISTICS_PROVIDER
                        || role == Role.WAREHOUSE))
            return true;

        return false;
    }

    // --- Getter & Setter Methods---

    public int getUserID() {
        return userID;
    }

    public void setUserID(int id) {
        this.userID = id;
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

    // for demo only
    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
