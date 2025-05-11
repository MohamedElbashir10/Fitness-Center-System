import java.util.HashMap;
import java.util.Map;

public class AuthService {

    private Map<String, User> users; // Stores username

    public AuthService() {
        this.users = new HashMap<>();
    }

    public boolean registerUser(User user) {
        if (users.containsKey(user.getUsername())) {
            LoggerUtils.logError("Username already exists.");
            return false;
        }
        users.put(user.getUsername(), user);
        LoggerUtils.logSuccess("User registered successfully.");
        return true;
    }

    public User authenticate(String username, String password) {
        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            LoggerUtils.logSuccess("Authentication successful.");
            return user;
        }
        LoggerUtils.logError("Invalid username or password.");
        return null;
    }

    public boolean hasRole(User user, String role) {
        if (user.getRole().equalsIgnoreCase(role)) {
            return true;
        }
        LoggerUtils.logError("User does not have the required role: " + role);
        return false;
    }

    public void listUsers() {
        LoggerUtils.logSection("Registered Users");
        for (User user : users.values()) {
            LoggerUtils.logInfo("ID: " + user.getId() + " | Name: " + user.getName() + " | Username: " + user.getUsername() + " | Role: " + user.getRole());
        }
    }
}
