package capstone.model;

public class Admin extends User {
    public Admin(String userId, String username, String passwordHash, String email) {
        super(userId, username, passwordHash, email, Role.ADMIN);
    }
}
