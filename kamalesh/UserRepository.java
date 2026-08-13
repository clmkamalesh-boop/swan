package capstone.repository;

import capstone.model.User;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class UserRepository {
    private final Map<String, User> usersByUsername = new LinkedHashMap<>();
    private final ReentrantLock lock = new ReentrantLock();
    private int nextId = 1;

    public String generateUserId() {
        lock.lock();
        try {
            String id = "U" + String.format("%04d", nextId);
            nextId++;
            return id;
        } finally {
            lock.unlock();
        }
    }

    public void save(User user) {
        lock.lock();
        try {
            usersByUsername.put(user.getUsername().toLowerCase(), user);
        } finally {
            lock.unlock();
        }
    }

    public User findByUsername(String username) {
        return usersByUsername.get(username.toLowerCase());
    }

    public boolean exists(String username) {
        return usersByUsername.containsKey(username.toLowerCase());
    }

    public User findById(String userId) {
        // small dataset for a demo repo, linear scan is fine
        for (User u : usersByUsername.values()) {
            if (u.getUserId().equals(userId)) {
                return u;
            }
        }
        return null;
    }

    public Map<String, User> getAll() {
        return usersByUsername;
    }
}
