import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FCSAppUI extends JFrame {
    private final AuthService authService;
    private final Schedule schedule;
    private final RegistrationService registrationService;

    public FCSAppUI(AuthService authService) {
        this.authService = authService;
        this.schedule = new Schedule();
        this.registrationService = new RegistrationService();
        initializeWelcomePage();
    }

    private void initializeWelcomePage() {
        setTitle("FCS Fitness Center");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel("Welcome to FCS Fitness Center!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(welcomeLabel, BorderLayout.CENTER);

        JButton startButton = new JButton("Start");
        startButton.addActionListener(e -> initializeMainMenu());
        panel.add(startButton, BorderLayout.SOUTH);

        add(panel);
        setVisible(true);
    }

    private void initializeMainMenu() {
        getContentPane().removeAll();
        setTitle("FCS Main Menu");

        JPanel panel = new JPanel(new GridLayout(3, 1, 10, 10));
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");
        JButton exitButton = new JButton("Exit");

        loginButton.addActionListener(e -> displayLoginDialog());
        registerButton.addActionListener(e -> displayRegistrationDialog());
        exitButton.addActionListener(e -> System.exit(0));

        panel.add(loginButton);
        panel.add(registerButton);
        panel.add(exitButton);

        add(panel);
        revalidate();
        repaint();
    }

    private void displayLoginDialog() {
        JDialog dialog = new JDialog(this, "Login", true);
        dialog.setSize(300, 200);
        dialog.setLayout(new GridLayout(3, 2));

        dialog.add(new JLabel("Username:"));
        JTextField usernameField = new JTextField();
        dialog.add(usernameField);

        dialog.add(new JLabel("Password:"));
        JPasswordField passwordField = new JPasswordField();
        dialog.add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            User user = authService.authenticate(username, password);
            if (user != null) {
                JOptionPane.showMessageDialog(dialog, "Welcome, " + user.getName() + "!", "Login Successful", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                handleUserActions(user);
            } else {
                JOptionPane.showMessageDialog(dialog, "Login failed. Invalid username or password.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        dialog.add(loginButton);

        dialog.setVisible(true);
    }

    private void displayRegistrationDialog() {
        JDialog dialog = new JDialog(this, "Register", true);
        dialog.setSize(400, 250);
        dialog.setLayout(new GridLayout(4, 2));

        dialog.add(new JLabel("Full Name:"));
        JTextField nameField = new JTextField();
        dialog.add(nameField);

        dialog.add(new JLabel("Email (Username):"));
        JTextField emailField = new JTextField();
        dialog.add(emailField);

        dialog.add(new JLabel("Password:"));
        JPasswordField passwordField = new JPasswordField();
        dialog.add(passwordField);

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(e -> {
            String name = nameField.getText();
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            String role = "Member";

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "All fields are required.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            User newUser = new User(name, email, password, role);
            boolean isRegistered = authService.registerUser(newUser);
            if (isRegistered) {
                JOptionPane.showMessageDialog(dialog, "Registration successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Registration failed. Username may already exist.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        dialog.add(registerButton);

        dialog.setVisible(true);
    }

    private void handleUserActions(User user) {
        String role = user.getRole().toLowerCase();
        switch (role) {
            case "member":
                memberPanel((Member) user);
                break;
            case "trainer":
                trainerPanel((Trainer) user);
                break;
            case "admin":
                adminPanel((Admin) user);
                break;
            default:
                JOptionPane.showMessageDialog(this, "Unknown role. Please contact support.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void memberPanel(Member member) {
        getContentPane().removeAll();
        setTitle("Member Panel - Welcome, " + member.getName());
        setSize(700, 500);

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        JTextArea scheduleArea = new JTextArea(member.checkSchedule(schedule));
        scheduleArea.setEditable(false);
        scheduleArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(scheduleArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        JButton viewSessionsButton = new JButton("View Sessions");
        JButton bookSessionButton = new JButton("Book Session");
        JButton cancelReservationButton = new JButton("Cancel Reservation");
        JButton logoutButton = new JButton("Logout");

        viewSessionsButton.addActionListener(e -> scheduleArea.setText(member.viewSessions(schedule)));
        bookSessionButton.addActionListener(e -> {
            JTextField sessionIdField = new JTextField();
            Object[] fields = {
                "Available Sessions:\n" + schedule.getFormattedSchedule() + "\nEnter Session ID:", sessionIdField
            };
            int result = JOptionPane.showConfirmDialog(this, fields, "Book Session", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                String sessionId = sessionIdField.getText().trim();
                if (sessionId.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Session ID is required.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                WorkoutSession session = schedule.getScheduledSessions().stream()
                        .filter(s -> s.getSessionID().equals(sessionId))
                        .findFirst().orElse(null);
                if (session == null) {
                    JOptionPane.showMessageDialog(this, "Session not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Reservation reservation = Reservation.createReservation("RES" + System.currentTimeMillis(), member, session);
                if (reservation != null) {
                    member.reserveSession(session);
                    JOptionPane.showMessageDialog(this, "Session booked successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    NotificationService.sendBookingConfirmation(member.getUsername(), 
                            session.getExerciseType() + " on " + session.getDateTime());
                } else {
                    JOptionPane.showMessageDialog(this, "Booking failed. Session may be full or already reserved.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        cancelReservationButton.addActionListener(e -> {
            StringBuilder reservations = new StringBuilder("Your Reservations:\n");
            if (member.getReservedSessions().isEmpty()) {
                reservations.append("No reservations found.");
            } else {
                for (WorkoutSession s : member.getReservedSessions()) {
                    reservations.append("Session ID: ").append(s.getSessionID())
                                .append(", Exercise: ").append(s.getExerciseType())
                                .append(", Date: ").append(s.getDateTime()).append("\n");
                }
            }
            JTextField sessionIdField = new JTextField();
            Object[] fields = {
                reservations.toString() + "\nEnter Session ID to cancel:", sessionIdField
            };
            int result = JOptionPane.showConfirmDialog(this, fields, "Cancel Reservation", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                String sessionId = sessionIdField.getText().trim();
                if (sessionId.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Session ID is required.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                WorkoutSession toCancel = member.getReservedSessions().stream()
                        .filter(s -> s.getSessionID().equals(sessionId))
                        .findFirst().orElse(null);
                if (toCancel == null) {
                    JOptionPane.showMessageDialog(this, "Reservation not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Reservation reservation = new Reservation("RES" + System.currentTimeMillis(), member, toCancel);
                if (reservation.cancelReservation()) {
                    member.cancelReservation(toCancel);
                    JOptionPane.showMessageDialog(this, "Reservation cancelled successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    NotificationService.sendCancellationNotification(member.getUsername(), 
                            toCancel.getExerciseType() + " on " + toCancel.getDateTime());
                } else {
                    JOptionPane.showMessageDialog(this, "Cancellation failed.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        logoutButton.addActionListener(e -> initializeMainMenu());

        buttonPanel.add(viewSessionsButton);
        buttonPanel.add(bookSessionButton);
        buttonPanel.add(cancelReservationButton);
        buttonPanel.add(logoutButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);
        add(panel);
        revalidate();
        repaint();
    }

    private void trainerPanel(Trainer trainer) {
        getContentPane().removeAll();
        setTitle("Trainer Panel");
        JPanel panel = new JPanel(new GridLayout(4, 1, 10, 10));

        JButton viewSessionsButton = new JButton("View Assigned Sessions");
        JButton addAvailabilityButton = new JButton("Add Availability");
        JButton checkScheduleButton = new JButton("Check Schedule");
        JButton logoutButton = new JButton("Logout");

        viewSessionsButton.addActionListener(e -> {
            StringBuilder sessions = new StringBuilder("Assigned Sessions:\n");
            for (WorkoutSession session : trainer.getAssignedSessions()) {
                sessions.append("Session: ").append(session.getExerciseType())
                       .append(" | Date: ").append(session.getDateTime())
                       .append(" | Room: ").append(session.getRoom().getName()).append("\n");
            }
            JOptionPane.showMessageDialog(this, sessions.toString(), "Assigned Sessions", JOptionPane.INFORMATION_MESSAGE);
        });
        addAvailabilityButton.addActionListener(e -> {
            JTextField dateField = new JTextField();
            JTextField startField = new JTextField();
            JTextField endField = new JTextField();
            Object[] fields = {
                "Date (YYYY-MM-DD):", dateField,
                "Start Time (HH:MM):", startField,
                "End Time (HH:MM):", endField
            };
            int result = JOptionPane.showConfirmDialog(this, fields, "Add Availability", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                try {
                    Availability slot = new Availability(
                        LocalDate.parse(dateField.getText()),
                        LocalTime.parse(startField.getText()),
                        LocalTime.parse(endField.getText())
                    );
                    trainer.addAvailabilitySlot(slot);
                    JOptionPane.showMessageDialog(this, "Availability added!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Invalid input format. Use YYYY-MM-DD and HH:MM.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        checkScheduleButton.addActionListener(e -> JOptionPane.showMessageDialog(this, schedule.getFormattedSchedule(), "Fitness Center Schedule", JOptionPane.INFORMATION_MESSAGE));
        logoutButton.addActionListener(e -> initializeMainMenu());

        panel.add(viewSessionsButton);
        panel.add(addAvailabilityButton);
        panel.add(checkScheduleButton);
        panel.add(logoutButton);

        add(panel);
        revalidate();
        repaint();
    }

    private void adminPanel(Admin admin) {
        getContentPane().removeAll();
        setTitle("Admin Panel");
        JPanel panel = new JPanel(new GridLayout(8, 1, 10, 10));

        JButton scheduleSessionButton = new JButton("Schedule New Workout Session");
        JButton viewUsersButton = new JButton("View All Users");
        JButton createTrainerButton = new JButton("Create Trainer Account");
        JButton removeTrainerButton = new JButton("Remove Trainer Account");
        JButton addExerciseButton = new JButton("Add Exercise");
        JButton removeExerciseButton = new JButton("Remove Exercise");
        JButton checkScheduleButton = new JButton("Check Schedule");
        JButton logoutButton = new JButton("Logout");

        scheduleSessionButton.addActionListener(e -> displayScheduleSessionDialog(admin));
        viewUsersButton.addActionListener(e -> registrationService.displayAllUsers());
        createTrainerButton.addActionListener(e -> {
            JTextField nameField = new JTextField();
            JTextField usernameField = new JTextField();
            JTextField passwordField = new JTextField();
            Object[] fields = {
                "Name:", nameField,
                "Username:", usernameField,
                "Password:", passwordField
            };
            int result = JOptionPane.showConfirmDialog(this, fields, "Create Trainer", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                if (admin.createTrainerAccount(nameField.getText(), usernameField.getText(), passwordField.getText()) != null) {
                    JOptionPane.showMessageDialog(this, "Trainer account created!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to create trainer account.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        removeTrainerButton.addActionListener(e -> {
            String username = JOptionPane.showInputDialog(this, "Enter trainer username to remove:");
            if (username != null && !username.trim().isEmpty()) {
                if (admin.removeTrainerAccount(username)) {
                    JOptionPane.showMessageDialog(this, "Trainer account removed!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to remove trainer account.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Username is required.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        addExerciseButton.addActionListener(e -> {
            JTextField nameField = new JTextField();
            JTextField descField = new JTextField();
            Object[] fields = {
                "Exercise Name:", nameField,
                "Description:", descField
            };
            int result = JOptionPane.showConfirmDialog(this, fields, "Add Exercise", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                if (schedule.addExercise(nameField.getText(), descField.getText())) {
                    JOptionPane.showMessageDialog(this, "Exercise added!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to add exercise.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        removeExerciseButton.addActionListener(e -> {
            String name = JOptionPane.showInputDialog(this, "Enter exercise name to remove:");
            if (name != null && !name.trim().isEmpty()) {
                if (schedule.removeExercise(name)) {
                    JOptionPane.showMessageDialog(this, "Exercise removed!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to remove exercise.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Exercise name is required.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        checkScheduleButton.addActionListener(e -> JOptionPane.showMessageDialog(this, schedule.getFormattedSchedule(), "Fitness Center Schedule", JOptionPane.INFORMATION_MESSAGE));
        logoutButton.addActionListener(e -> initializeMainMenu());

        panel.add(scheduleSessionButton);
        panel.add(viewUsersButton);
        panel.add(createTrainerButton);
        panel.add(removeTrainerButton);
        panel.add(addExerciseButton);
        panel.add(removeExerciseButton);
        panel.add(checkScheduleButton);
        panel.add(logoutButton);

        add(panel);
        revalidate();
        repaint();
    }

    private void displayScheduleSessionDialog(Admin admin) {
        JDialog dialog = new JDialog(this, "Schedule New Workout Session", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new GridLayout(6, 2, 10, 10));

        // Fetch available exercise types
        List<String> exerciseTypes = getExerciseTypes();
        dialog.add(new JLabel("Exercise Type:"));
        JComboBox<String> exerciseCombo = new JComboBox<>(exerciseTypes.toArray(new String[0]));
        dialog.add(exerciseCombo);

        // Fetch available trainers
        List<User> trainers = getTrainers();
        dialog.add(new JLabel("Trainer:"));
        JComboBox<String> trainerCombo = new JComboBox<>(
                trainers.stream().map(User::getUsername).toArray(String[]::new));
        dialog.add(trainerCombo);

        // Fetch available rooms
        List<Room> rooms = getRooms();
        dialog.add(new JLabel("Room:"));
        JComboBox<String> roomCombo = new JComboBox<>(
                rooms.stream().map(room -> room.getName() + " (ID: " + room.getId() + ")").toArray(String[]::new));
        dialog.add(roomCombo);

        // Date selection
        dialog.add(new JLabel("Date (YYYY-MM-DD):"));
        JTextField dateField = new JTextField(LocalDate.now().toString());
        dialog.add(dateField);

        // Time slot selection
        String[] timeSlots = {"09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00"};
        dialog.add(new JLabel("Time:"));
        JComboBox<String> timeCombo = new JComboBox<>(timeSlots);
        dialog.add(timeCombo);

        // Capacity selection
        dialog.add(new JLabel("Max Capacity:"));
        JSpinner capacitySpinner = new JSpinner(new SpinnerNumberModel(15, 1, 50, 1));
        dialog.add(capacitySpinner);

        JButton scheduleButton = new JButton("Schedule");
        scheduleButton.addActionListener(e -> {
            try {
                String exerciseType = (String) exerciseCombo.getSelectedItem();
                String trainerUsername = (String) trainerCombo.getSelectedItem();
                String roomSelection = (String) roomCombo.getSelectedItem();
                String date = dateField.getText();
                String time = (String) timeCombo.getSelectedItem();
                int capacity = (Integer) capacitySpinner.getValue();

                // Extract room ID from selection
                int roomId = Integer.parseInt(roomSelection.replaceAll(".*ID: (\\d+)\\)", "$1"));
                Room room = rooms.stream().filter(r -> r.getId() == roomId).findFirst().orElse(null);
                Trainer trainer = (Trainer) trainers.stream()
                        .filter(t -> t.getUsername().equals(trainerUsername)).findFirst().orElse(null);

                if (exerciseType == null || trainer == null || room == null) {
                    JOptionPane.showMessageDialog(dialog, "Please select valid exercise, trainer, and room.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Generate session ID
                String sessionId = "SES" + System.currentTimeMillis();
                LocalDateTime dateTime = LocalDateTime.parse(date + "T" + time);

                WorkoutSession session = new WorkoutSession(
                        sessionId, exerciseType, dateTime, capacity, room, trainer);

                if (schedule.scheduleWorkout(admin, session, trainer, room)) {
                    JOptionPane.showMessageDialog(dialog, "Session scheduled successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to schedule session. Check trainer/room availability.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid input format. Use YYYY-MM-DD for date.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        dialog.add(scheduleButton);

        dialog.setVisible(true);
    }

    private List<String> getExerciseTypes() {
        List<String> exerciseTypes = new ArrayList<>();
        try (Connection conn = new DatabaseHandler().connect()) {
            String query = "SELECT name FROM session_types";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    exerciseTypes.add(rs.getString("name"));
                }
            }
        } catch (SQLException e) {
            LoggerUtils.logError("Error fetching exercise types: " + e.getMessage());
        }
        return exerciseTypes;
    }

    private List<User> getTrainers() {
        List<User> trainers = new ArrayList<>();
        try (Connection conn = new DatabaseHandler().connect()) {
            String query = "SELECT id, name, username, role FROM users WHERE role = 'Trainer'";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    trainers.add(new Trainer(
                            rs.getString("name"),
                            rs.getString("username"),
                            null));
                }
            }
        } catch (SQLException e) {
            LoggerUtils.logError("Error fetching trainers: " + e.getMessage());
        }
        return trainers;
    }

    private List<Room> getRooms() {
        List<Room> rooms = new ArrayList<>();
        try (Connection conn = new DatabaseHandler().connect()) {
            String query = "SELECT id, name, capacity, description FROM rooms";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    rooms.add(new Room(
                            rs.getString("name"),
                            rs.getInt("id"),
                            rs.getInt("capacity"),
                            rs.getString("description")));
                }
            }
        } catch (SQLException e) {
            LoggerUtils.logError("Error fetching rooms: " + e.getMessage());
        }
        return rooms;
    }

    private Room getRoomById(int roomId) {
        try (Connection conn = new DatabaseHandler().connect()) {
            String query = "SELECT * FROM rooms WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, roomId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return new Room(rs.getString("name"), rs.getInt("id"), rs.getInt("capacity"), "");
                }
            }
        } catch (SQLException e) {
            LoggerUtils.logError("Error fetching room: " + e.getMessage());
        }
        return null;
    }

    public static void main(String[] args) {
        DatabaseHandler dbHandler = new DatabaseHandler();
        AuthService authService = new AuthService(dbHandler);
        SwingUtilities.invokeLater(() -> new FCSAppUI(authService));
    }
}