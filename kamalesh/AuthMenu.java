package capstone.ui;

import capstone.exception.AuthenticationException;
import capstone.exception.DuplicateUserException;
import capstone.exception.InvalidInputException;
import capstone.model.Role;
import capstone.model.User;
import capstone.service.AuthService;
import java.util.Scanner;

public class AuthMenu {
    private final Scanner sc;
    private final AuthService authService;

    public AuthMenu(Scanner sc, AuthService authService) {
        this.sc = sc;
        this.authService = authService;
    }

    // returns the logged-in user, or null if user chose to exit the app
    public User run() {
        while (true) {
            System.out.println("\n===== SkillSwap Marketplace =====");
            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");
            System.out.print("Choose: ");
            String choice = sc.nextLine().trim();

            if (choice.equals("1")) {
                User u = login();
                if (u != null) return u;
            } else if (choice.equals("2")) {
                register();
            } else if (choice.equals("3")) {
                return null;
            } else {
                System.out.println("Invalid option.");
            }
        }
    }

    private User login() {
        System.out.print("Username: ");
        String username = sc.nextLine().trim();
        System.out.print("Password: ");
        String password = sc.nextLine().trim();
        try {
            User u = authService.login(username, password);
            System.out.println("Welcome back, " + u.getUsername() + "!");
            return u;
        } catch (AuthenticationException e) {
            System.out.println("Login failed: " + e.getMessage());
            return null;
        }
    }

    private void register() {
        System.out.println("Register as: 1) Admin  2) Seller  3) Buyer");
        System.out.print("Choose: ");
        String roleChoice = sc.nextLine().trim();

        Role role;
        if (roleChoice.equals("1")) role = Role.ADMIN;
        else if (roleChoice.equals("2")) role = Role.SELLER;
        else if (roleChoice.equals("3")) role = Role.BUYER;
        else {
            System.out.println("Invalid role choice.");
            return;
        }

        System.out.print("Choose a username: ");
        String username = sc.nextLine().trim();
        System.out.print("Choose a password: ");
        String password = sc.nextLine().trim();
        System.out.print("Email: ");
        String email = sc.nextLine().trim();

        try {
            if (role == Role.ADMIN) {
                authService.registerAdmin(username, password, email);
            } else if (role == Role.SELLER) {
                System.out.print("Store name: ");
                String storeName = sc.nextLine().trim();
                authService.registerSeller(username, password, email, storeName);
            } else {
                System.out.print("Shipping address: ");
                String address = sc.nextLine().trim();
                authService.registerBuyer(username, password, email, address);
            }
            System.out.println("Registration successful. You can now log in.");
        } catch (InvalidInputException | DuplicateUserException e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }
}
