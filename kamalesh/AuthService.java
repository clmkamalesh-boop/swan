package capstone.service;

import capstone.exception.AuthenticationException;
import capstone.exception.DuplicateUserException;
import capstone.exception.InvalidInputException;
import capstone.model.Admin;
import capstone.model.Buyer;
import capstone.model.Role;
import capstone.model.Seller;
import capstone.model.User;
import capstone.repository.UserRepository;

public class AuthService {
    private final UserRepository userRepo;
    private User currentUser;

    public AuthService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    // not a real crypto hash, just enough to avoid storing raw passwords in the demo store
    private String hashPassword(String raw) {
        int h = raw.hashCode();
        return Integer.toHexString(h) + "_" + raw.length();
    }

    public User registerAdmin(String username, String password, String email) {
        return register(username, password, email, Role.ADMIN, null, null);
    }

    public User registerSeller(String username, String password, String email, String storeName) {
        return register(username, password, email, Role.SELLER, storeName, null);
    }

    public User registerBuyer(String username, String password, String email, String shippingAddress) {
        return register(username, password, email, Role.BUYER, null, shippingAddress);
    }

    private User register(String username, String password, String email, Role role, String storeName, String address) {
        if (username == null || username.trim().length() < 3) {
            throw new InvalidInputException("Username must be at least 3 characters.");
        }
        if (password == null || password.length() < 4) {
            throw new InvalidInputException("Password must be at least 4 characters.");
        }
        if (email == null || !email.contains("@")) {
            throw new InvalidInputException("Email looks invalid.");
        }
        if (userRepo.exists(username)) {
            throw new DuplicateUserException("Username '" + username + "' is already taken.");
        }

        String userId = userRepo.generateUserId();
        String hash = hashPassword(password);
        User newUser;

        if (role == Role.ADMIN) {
            newUser = new Admin(userId, username, hash, email);
        } else if (role == Role.SELLER) {
            newUser = new Seller(userId, username, hash, email, storeName == null ? username + "'s Store" : storeName);
        } else {
            newUser = new Buyer(userId, username, hash, email, address == null ? "N/A" : address);
        }

        userRepo.save(newUser);
        return newUser;
    }

    public User login(String username, String password) {
        User u = userRepo.findByUsername(username);
        if (u == null) {
            throw new AuthenticationException("No account found for username '" + username + "'.");
        }
        String hash = hashPassword(password);
        if (!u.getPasswordHash().equals(hash)) {
            throw new AuthenticationException("Incorrect password.");
        }
        currentUser = u;
        return u;
    }

    public void logout() {
        currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public java.util.Collection<User> getAllUsers() {
        return userRepo.getAll().values();
    }
}
