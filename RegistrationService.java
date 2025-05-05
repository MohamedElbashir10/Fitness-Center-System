import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RegistrationService {

    private Map<String, User> registeredUsersByEmail;

    public RegistrationService() {
        this.registeredUsersByEmail = new HashMap<>();
    }

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    public User registerUser(String name, String email, String password, String role) {
        if (name == null || name.isBlank() || email == null || email.isBlank() || password == null || password.isBlank()) {
            System.out.println("[ERROR] Name, email, and password are required.");
            return null;
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            System.out.println("[ERROR] Invalid email format.");
            return null;
        }

        if (registeredUsersByEmail.containsKey(email)) {
            System.out.println("[ERROR] Email is already registered.");
            return null;
        }

        User newUser;
        switch (role.toLowerCase()) {
            case "member":
                newUser = new Member(name, email, password);
                break;
            case "trainer":
                newUser = new Trainer(name, email, password);
                break;
            case "admin":
                newUser = new Admin(name, email, password);
                break;
            default:
                System.out.println("[ERROR] Invalid role. Choose: member, trainer, or admin.");
                return null;
        }

        registeredUsersByEmail.put(email, newUser);
        System.out.println("[SUCCESS] " + role + " registered: " + name);
        return newUser;
    }

    public User findUserByEmail(String email) {
        return registeredUsersByEmail.get(email);
    }

    public boolean isEmailRegistered(String email) {
        return registeredUsersByEmail.containsKey(email);
    }

    public void displayAllUsers() {
        System.out.println("\n=== Registered Users ===");
        for (User user : registeredUsersByEmail.values()) {
            System.out.println("Role: " + user.getClass().getSimpleName() + " | Name: " + user.name + " | Email: " + user.email);
        }
    }
}
