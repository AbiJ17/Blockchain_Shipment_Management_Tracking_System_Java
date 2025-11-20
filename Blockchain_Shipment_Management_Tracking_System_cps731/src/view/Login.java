package view;

import model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Login {

    private List<User> users = new ArrayList<>();
    private Scanner scanner = new Scanner(System.in);

    public Login() {}

    public void start() {
        while (true) {
            System.out.println("=== LOGIN PAGE ===");
            System.out.print("Username: ");
            String username = scanner.nextLine();

            System.out.print("Password: ");
            String password = scanner.nextLine();

            User user = authenticate(username, password);
            if (user != null) {
                System.out.println("\nLogin Successful! Welcome " + user.getUsername());
                
            } else {
                System.out.println("\nInvalid credentials. Try again.\n");
            }
        }
    }

    private User authenticate(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.authenticate(password)) {
                return user;
            }
        }
        return null;
    }
}

