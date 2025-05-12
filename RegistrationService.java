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
            LoggerUtils.logError("Name, email, and password are required.");
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
                LoggerUtils.logError("Invalid role. Choose: member, trainer, or admin.");
                return null;
        }

        registeredUsersByEmail.put(email, newUser);
        LoggerUtils.logSuccess(role + " registered: " + name);
        return newUser;
    }

    public User findUserByEmail(String email) {
        return registeredUsersByEmail.get(email);
    }

    public boolean isEmailRegistered(String email) {
        return registeredUsersByEmail.containsKey(email);
    }

    public String displayAllUsers() {
        if (registeredUsersByEmail.isEmpty()) {
            return "No registered users.";
        }
        StringBuilder userList = new StringBuilder("Registered Users:\n");
        for (User user : registeredUsersByEmail.values()) {
            userList.append("Role: ").append(user.getClass().getSimpleName())
                    .append(" | Name: ").append(user.getName())
                    .append(" | Username: ").append(user.getUsername())
                    .append("\n");
        }
        return userList.toString();
    }
}