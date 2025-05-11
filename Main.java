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

        LoginUI loginUI = new LoginUI(authService);
        User loggedInUser = loginUI.displayLoginScreen();

        if (loggedInUser != null) {
            // route to proper dashboard based on role
            switch (loggedInUser.getRole()) {
                case "Admin":
                    // showAdminDashboard();
                    break;
                case "Trainer":
                    // showTrainerPanel();
                    break;
                case "Member":
                    // showMemberHome();
                    break;
            }
        }

    }
}
