import java.util.Scanner;

public class FitnessCenterSystem {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        RegistrationService registrationService = new RegistrationService();
        AuthService authService = new AuthService();
        Schedule schedule = new Schedule();
        Reservation reservation = new Reservation();

        User loggedInUser = null;
        boolean running = true;

        System.out.println("🏋️ Welcome to the Fitness Center System! 🏋️");

        while (running) {
            System.out.println("\n---- Main Menu ----");
            System.out.println("1 - Register");
            System.out.println("2 - Login");
            System.out.println("3 - View Schedule");
            System.out.println("4 - Make Reservation");
            System.out.println("0 - Exit");
            System.out.print("Your choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.print("ID: ");
                    String id = scanner.nextLine();

                    System.out.print("Full Name: ");
                    String name = scanner.nextLine();

                    System.out.print("Email: ");
                    String email = scanner.nextLine(); 
                    System.out.print("Password: ");
                    String password = scanner.nextLine();

                    System.out.print("Role (member/trainer/admin): ");
                    String role = scanner.nextLine();

                    User newUser = registrationService.registerUser(id, name, email, password, role);

                    if (newUser != null) {
                        authService.registerUser(newUser);
                    }
                    break;

                case "2":
                    System.out.print("Username: ");
                    String username = scanner.nextLine();

                    System.out.print("Password: ");
                    String loginPassword = scanner.nextLine(); 

                    loggedInUser = authService.authenticate(username, loginPassword);

                    if (loggedInUser != null) {
                        System.out.println("[SUCCESS] Logged in as: " + loggedInUser.getName() + " (" + loggedInUser.getRole() + ")");

                        String roleType = loggedInUser.getRole().toLowerCase();
                        switch (roleType) {
                            case "member":
                                System.out.println("→ Redirecting to booking menu...");
                                break;
                            case "trainer":
                                System.out.println("→ Redirecting to trainer schedule...");
                                break;
                            case "admin":
                                System.out.println("→ Redirecting to admin panel...");
                                break;
                            default:
                                System.out.println("[ERROR] Unknown role.");
                        }
                    } else {
                        System.out.println("[ERROR] Login failed.");
                    }
                    break;

                case "3":
                    schedule.displaySchedule();
                    break;

                case "4":
                    if (loggedInUser == null) {
                        System.out.println("[ERROR] Please log in first.");
                    } else {
                        reservation.makeReservation(loggedInUser);
                    }
                    break;

                case "0":
                    running = false;
                    System.out.println("Goodbye!");
                    break;

                default:
                    System.out.println("[ERROR] Invalid choice.");
            }
        }

        scanner.close();
    }
}
      
