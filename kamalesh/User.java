package capstone.model;

public class User {
    private String userId;
    private String username;
    private String passwordHash;
    private String email;
    private Role role;

    public User(String userId, String username, String passwordHash, String email, Role role) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.email = email;
        this.role = role;
    }

    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getEmail() { return email; }
    public Role getRole() { return role; }

    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return "[" + role + "] " + username + " (" + userId + ") - " + email;
    }
}
