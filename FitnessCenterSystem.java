import java.util.Scanner;

public class FitnessCenterSystem {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        RegistrationService registrationService = new RegistrationService();
        AuthService authService = new AuthService();
        Schedule schedule = new Schedule();

        User loggedInUser = null;
        boolean running = true;

        System.out.println("Welcome to the Fitness Center System!");

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

                    System.out.print("Email (Username): ");
                    String email = scanner.nextLine();

                    System.out.print("Password: ");
                    String password = scanner.nextLine();

                    System.out.print("Role (member/trainer/admin): ");
                    String role = scanner.nextLine();

                    User newUser = registrationService.registerUser(id, name, email, password, role);

                    if (newUser != null) {
                        authService.registerUser(newUser);
                        NotificationService.sendSystemMessage(
                                newUser.getUsername(),
                                newUser.getId(),
                                "Welcome to FCS",
                                "Hello " + newUser.getName() + ", your registration is complete!");
                    }
                    break;

                case "2":
                    System.out.print("Username: ");
                    String username = scanner.nextLine();

                    System.out.print("Password: ");
                    String loginPassword = scanner.nextLine();

                    loggedInUser = authService.authenticate(username, loginPassword);

                    if (loggedInUser != null) {
                        LoggerUtils.logSuccess("Logged in as: " + loggedInUser.getName() + " (" + loggedInUser.getRole() + ")");
                        NotificationService.sendInAppNotification(loggedInUser.getId(),
                                "You have successfully logged in. Welcome back!");
                        String roleType = loggedInUser.getRole().toLowerCase();
                        switch (roleType) {
                            case "member":
                                memberPanel(scanner, schedule, (Member) loggedInUser);
                                break;
                            case "trainer":
                                trainerPanel((Trainer) loggedInUser);
                                break;
                            case "admin":
                                adminPanel(scanner, schedule, (Admin) loggedInUser, registrationService);
                                break;
                            default:
                                LoggerUtils.logError("Unknown role.");
                        }
                    } else {
                        LoggerUtils.logError("Login failed.");
                    }
                    break;

                case "3":
                    schedule.displaySchedule();
                    break;

                case "4":
                    if (loggedInUser == null) {
                        LoggerUtils.logError("Please log in first.");
                    } else if (loggedInUser instanceof Member) {
                        schedule.displaySchedule();
                        System.out.print("Enter Session ID to reserve: ");
                        String sessionId = scanner.nextLine();
                        WorkoutSession selectedSession = schedule.getScheduledSessions().stream()
                                .filter(s -> s.getSessionID().equals(sessionId))
                                .findFirst().orElse(null);

                        if (selectedSession != null) {
                            Reservation newReservation = Reservation.createReservation(
                                    "R" + System.currentTimeMillis(),
                                    (Member) loggedInUser,
                                    selectedSession
                            );
                            if (newReservation != null) {
                                LoggerUtils.logSuccess("Reservation successful!");
                            }
                        } else {
                            LoggerUtils.logError("Session not found.");
                        }
                    } else {
                        LoggerUtils.logError("Only members can make reservations.");
                    }
                    break;

                case "0":
                    running = false;
                    System.out.println("Goodbye!");
                    break;

                default:
                    LoggerUtils.logError("Invalid choice.");
            }
        }

        scanner.close();
    }

    private static void adminPanel(Scanner scanner, Schedule schedule, Admin admin, RegistrationService registrationService) {
        System.out.println("\n=== Admin Panel ===");
        System.out.println("1 - Schedule New Workout Session");
        System.out.println("2 - View All Users");
        System.out.println("0 - Back to Main Menu");
        System.out.print("Your choice: ");
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

                Trainer trainer = new Trainer("T001", "Trainer One", "trainer1", "pass123");
                trainer.addAvailabilitySlot(new Availability(
                        java.time.LocalDate.parse(date),
                        java.time.LocalTime.of(8, 0),
                        java.time.LocalTime.of(18, 0)));

                Room room = new Room("Main Room", 201, 30, "Large workout hall");
                java.time.LocalDateTime dateTime = java.time.LocalDateTime.parse(date + "T" + time);

                WorkoutSession session = new WorkoutSession(sessionID, exerciseType, dateTime, maxCapacity, null, null);
                boolean success = schedule.scheduleWorkout(admin, session, trainer, room);

                if (success) {
                    LoggerUtils.logSuccess("Session scheduled successfully.");
                    NotificationService.sendSystemMessage(admin.getUsername(), admin.getId(),
                            "Session Scheduled",
                            "You have scheduled a new session: " + session.getExerciseType() + " on " + session.getDateTime());
                } else {
                    LoggerUtils.logError("Failed to schedule session.");
                }
                break;

            case "2":
                registrationService.displayAllUsers();
                break;

            case "0":
                LoggerUtils.logInfo("Returning to main menu...");
                break;

            default:
                LoggerUtils.logError("Invalid choice.");
        }
    }

    private static void memberPanel(Scanner scanner, Schedule schedule, Member member) {
        System.out.println("\n=== Member Menu ===");
        System.out.println("1 - View Workout Sessions");
        System.out.println("2 - Make Reservation");
        System.out.println("0 - Back to Main Menu");
        System.out.print("Your choice: ");
        String choice = scanner.nextLine();

        switch (choice) {
            case "1":
                schedule.displaySchedule();
                break;
            case "2":
                schedule.displaySchedule();
                System.out.print("Enter Session ID to reserve: ");
                String sessionId = scanner.nextLine();
                WorkoutSession selected = schedule.getScheduledSessions().stream()
                        .filter(s -> s.getSessionID().equals(sessionId))
                        .findFirst().orElse(null);

                if (selected != null) {
                    boolean added = selected.addParticipant(member);
                    if (added) {
                        LoggerUtils.logSuccess("Reservation successful!");
                        String sessionInfo = selected.getExerciseType() + " on " + selected.getDateTime();
                        NotificationService.sendBookingConfirmation(member.getUsername(), sessionInfo);
                    } else {
                        LoggerUtils.logError("Session is full.");
                    }
                } else {
                    LoggerUtils.logError("Session not found.");
                }
                break;
            case "0":
                LoggerUtils.logInfo("Returning to main menu...");
                break;
            default:
                LoggerUtils.logError("Invalid choice.");
        }
    }

    private static void trainerPanel(Trainer trainer) {
        System.out.println("\n=== Trainer Schedule ===");
        for (WorkoutSession session : trainer.getAssignedSessions()) {
            LoggerUtils.logInfo("Session: " + session.getExerciseType()
                    + " | Date: " + session.getDateTime()
                    + " | Room: " + session.getRoom().getName());
        }
    }
}