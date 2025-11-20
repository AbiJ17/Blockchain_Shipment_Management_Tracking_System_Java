package model;

public class User {

    private int userID;
    private String username;
    private String password;
    private String role;
    private String email;

    public boolean login() { 
        return false; 
    }

    public void logout() {

    }
    
    public void viewDashboard() {

    }

    public boolean authenticate(String inputPassword) { 
        return false; 
    }

    public boolean authorize(String action) { 
        return false; 
    }

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
}
