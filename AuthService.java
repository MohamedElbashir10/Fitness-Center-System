import java.util.HashMap;
import java.util.Map;

public class AuthService {

    private Map<String, User> users; // Stores  username

    public AuthService() {
        this.users = new HashMap<>();
    }

    public boolean registerUser(User user) {
        if (users.containsKey(user.getUsername())) {
            System.out.println("[ERROR] Username already exists.");
            return false;
        }
        users.put(user.getUsername(), user);
        System.out.println("[SUCCESS] User registered successfully.");
        return true;
    }

    public User authenticate(String username, String password) {
        User user = users.get(username);
        if (user != null && user.getPassword().equals(password)) {
            System.out.println("[SUCCESS] Authentication successful.");
            return user;
        }
        System.out.println("[ERROR] Invalid username or password.");
        return null;
    }

    public boolean hasRole(User user, String role) {
        if (user.getRole().equalsIgnoreCase(role)) {
            return true;
        }
        System.out.println("[ERROR] User does not have the required role: " + role);
        return false;
    }

    public void listUsers() {
        System.out.println("\n=== Registered Users ===");
        for (User user : users.values()) {
            System.out.println("ID: " + user.getId() + " | Name: " + user.getName() + " | Username: " + user.getUsername() + " | Role: " + user.getRole());
        }
    }
}