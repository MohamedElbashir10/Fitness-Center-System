import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RegistrationService {

    private final Map<String, User> registeredUsersByEmail = new HashMap<>();
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    public User registerUser(int id, String name, String email, String password, String role) {
        if (isInvalidInput(id, name, email, password)) {
            LoggerUtils.logError("ID, name, email, and password are required.");
            return null;
        }

        if (!EMAIL_PATTERN.matcher(email).matches()) {
            LoggerUtils.logError("Invalid email format.");
            return null;
        }

        if (registeredUsersByEmail.containsKey(email)) {
            LoggerUtils.logError("Email is already registered.");
            return null;
        }

        User newUser = createUserByRole(id, name, email, password, role);
        if (newUser == null) {
            LoggerUtils.logError("Invalid role. Choose: member, trainer, or admin.");
            return null;
        }

        registeredUsersByEmail.put(email, newUser);
        LoggerUtils.logSuccess(role + " registered: " + name);
        return newUser;
    }

    private boolean isInvalidInput(int id, String name, String email, String password) {
        return id <= 0 || name == null || name.isBlank() || email == null || email.isBlank() || password == null || password.isBlank();
    }

    private User createUserByRole(int id, String name, String email, String password, String role) {
        return switch (role.toLowerCase()) {
            case "member" -> new Member(id, name, email, password);
            case "trainer" -> new Trainer(id, name, email, password);
            case "admin" -> new Admin(id, name, email, password);
            default -> null;
        };
    }

    public User findUserByEmail(String email) {
        return registeredUsersByEmail.get(email);
    }

    public boolean isEmailRegistered(String email) {
        return registeredUsersByEmail.containsKey(email);
    }

    public void displayAllUsers() {
        LoggerUtils.logSection("Registered Users:");
        registeredUsersByEmail.values().forEach(user ->
                LoggerUtils.logInfo("Role: " + user.getClass().getSimpleName() + " | Name: " + user.getName() + " | Username: " + user.getUsername())
        );
    }
}