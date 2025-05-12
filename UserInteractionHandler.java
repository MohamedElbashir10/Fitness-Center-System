import java.time.LocalDateTime;
import java.util.Scanner;

public class UserInteractionHandler {
    private AuthService authService;
    private Schedule schedule;
    private RegistrationService registrationService;

    public UserInteractionHandler(Schedule schedule, RegistrationService registrationService) {
        this.authService = authService;
        this.schedule = schedule;
        this.registrationService = registrationService;
    }

    public void handleUserInteraction(Scanner scanner) {
        User loggedInUser = null;
        boolean running = true;

        while (running) {
            if (loggedInUser == null) {
                System.out.println("\n--- Main Menu ---");
                System.out.println("1. Login");
                System.out.println("2. Exit");
                System.out.print("Enter your choice: ");
                String choice = scanner.nextLine();

                switch (choice) {
                    case "1":
                        loggedInUser = handleLogin(scanner);
                        break;
                    case "2":
                        running = false;
                        LoggerUtils.logInfo("Exiting system.");
                        break;
                    default:
                        LoggerUtils.logError("Invalid choice.");
                }
            } else {
                redirectUserByRole(scanner, loggedInUser);
                loggedInUser = null; // Logout after role-specific actions
            }
        }
    }

    private User handleLogin(Scanner scanner) {
        System.out.print("Enter Username: ");
        String username = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        User user = authService.authenticate(username, password);
        if (user != null) {
            LoggerUtils.logSuccess("Welcome, " + user.getName() + "!");
        }
        return user;
    }

    private void redirectUserByRole(Scanner scanner, User user) {
        String role = user.getClass().getSimpleName().toLowerCase();
        switch (role) {
            case "member":
                memberPanel(scanner, (Member) user);
                break;
            case "trainer":
                trainerPanel((Trainer) user);
                break;
            case "admin":
                adminPanel(scanner, (Admin) user);
                break;
            default:
                LoggerUtils.logError("Unknown role.");
        }
    }

    private void memberPanel(Scanner scanner, Member member) {
        System.out.println("\n=== Member Menu ===");
        System.out.println("1. View Workout Sessions");
        System.out.println("2. Make Reservation");
        System.out.println("0. Logout");
        System.out.print("Enter your choice: ");
        String choice = scanner.nextLine();

        switch (choice) {
            case "1":
                schedule.getScheduledSessions();
                break;
            case "2":
                schedule.getScheduledSessions();
                System.out.print("Enter Session ID to reserve: ");
                String sessionId = scanner.nextLine();
                WorkoutSession session = schedule.getScheduledSessions().stream()
                        .filter(s -> s.getSessionID().equals(sessionId))
                        .findFirst().orElse(null);

                if (session != null) {
                    boolean added = session.addParticipant(member);
                    if (added) {
                        LoggerUtils.logSuccess("Reservation successful!");
                        NotificationService.sendBookingConfirmation(member.getUsername(),
                                session.getExerciseType() + " on " + session.getDateTime());
                    } else {
                        LoggerUtils.logError("Session is full.");
                    }
                } else {
                    LoggerUtils.logError("Session not found.");
                }
                break;
            case "0":
                LoggerUtils.logInfo("Logging out...");
                break;
            default:
                LoggerUtils.logError("Invalid choice.");
        }
    }

    private void trainerPanel(Trainer trainer) {
        System.out.println("\n=== Trainer Schedule ===");
        for (WorkoutSession session : trainer.getAssignedSessions()) {
            LoggerUtils.logInfo("Session: " + session.getExerciseType()
                    + " | Date: " + session.getDateTime()
                    + " | Room: " + session.getRoom().getName());
        }
    }

    private void adminPanel(Scanner scanner, Admin admin) {
        System.out.println("\n=== Admin Panel ===");
        System.out.println("1. Schedule New Workout Session");
        System.out.println("2. View All Users");
        System.out.println("0. Logout");
        System.out.print("Enter your choice: ");
        String choice = scanner.nextLine();

        switch (choice) {
            case "1":
                System.out.print("Enter session ID: ");
                String sessionID = scanner.nextLine();
                System.out.print("Enter exercise type: ");
                String exerciseType = scanner.nextLine();
                System.out.print("Enter date (YYYY-MM-DD): ");
                String date = scanner.nextLine();
                System.out.print("Enter time (HH:MM): ");
                String time = scanner.nextLine();
                System.out.print("Enter max capacity: ");
                int maxCapacity = Integer.parseInt(scanner.nextLine());

                Trainer trainer = new Trainer(0066, "Trainer One", "trainer1", "pass123");
                Room room = new Room("Main Room", 201, 30, "");
                WorkoutSession session = new WorkoutSession(sessionID, exerciseType,
                        LocalDateTime.parse(date + "T" + time), maxCapacity, room, trainer);

                boolean success = schedule.scheduleWorkout(admin, session, trainer, room);
                if (success) {
                    LoggerUtils.logSuccess("Session scheduled successfully.");
                } else {
                    LoggerUtils.logError("Failed to schedule session.");
                }
                break;
            case "2":
                registrationService.displayAllUsers();
                break;
            case "0":
                LoggerUtils.logInfo("Logging out...");
                break;
            default:
                LoggerUtils.logError("Invalid choice.");
        }
    }
}