import java.time.LocalDateTime;

public class Main {
    public static void main(String[] args) {
        // Initialize the AuthService
        AuthService authService = new AuthService();

        // Register an Admin
        User admin = new User("1", "Admin User", "admin", "admin123", "Admin");
        authService.registerUser(admin);

        // Register a Trainer
        Trainer trainer = new Trainer("2", "John Doe", "trainer1", "trainer123");
        authService.registerUser(trainer);

        // Register a Member
        User member = new User("3", "Jane Smith", "member1", "member123", "Member");
        authService.registerUser(member);

    }
}


