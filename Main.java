import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        LoggerUtils.logSection("Fitness Center System Start");

        // Initialize the AuthService
        AuthService authService = new AuthService();

        // Register an Admin
        User admin = new User("1", "Admin User", "admin", "admin123", "Admin");
        if (authService.registerUser(admin)) {
            LoggerUtils.logSuccess("Admin registered successfully.");
        }

        // Register a Trainer
        Trainer trainer = new Trainer("2", "John Doe", "trainer1", "trainer123");
        if (authService.registerUser(trainer)) {
            LoggerUtils.logSuccess("Trainer registered successfully.");
        }

        // Register a Member
        User member = new User("3", "Jane Smith", "member1", "member123", "Member");
        if (authService.registerUser(member)) {
            LoggerUtils.logSuccess("Member registered successfully.");
        }

        LoggerUtils.logInfo("System initialized at: " + LocalDateTime.now());
    }
}



