import java.util.Scanner;

public class LoginUI {

    private AuthService authService;

    public LoginUI(AuthService authService) {
        this.authService = authService;
    }

    public User displayLoginScreen() {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== Login ===");
        System.out.print("Enter Username: ");
        String username = scanner.nextLine();

        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        User user = authService.authenticate(username, password);

        if (user != null) {
            System.out.println("[SUCCESS] Welcome, " + user.getName() + "!");
            return user;
        } else {
            System.out.println("[ERROR] Invalid credentials. Please try again.");
            return null;
        }
    }
}