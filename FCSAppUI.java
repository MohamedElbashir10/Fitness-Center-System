import javax.swing.*;
import javax.swing.border.EmptyBorder;
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
    private static final Font LABEL_FONT = new Font("Arial", Font.BOLD, 14);
    private static final Font INPUT_FONT = new Font("Arial", Font.PLAIN, 12);
    private static final Color BUTTON_COLOR = new Color(59, 89, 182);
    private static final Color CANCEL_COLOR = new Color(182, 59, 59);

    public FCSAppUI(AuthService authService) {
        this.authService = authService;
        this.schedule = new Schedule();
        this.registrationService = new RegistrationService();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(400, 300));
        setLocationRelativeTo(null);
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            LoggerUtils.logError("Failed to set Nimbus look and feel: " + e.getMessage());
        }
        initializeWelcomePage();
    }

    private void initializeWelcomePage() {
        setTitle("FCS Fitness Center");
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel welcomeLabel = new JLabel("Welcome to FCS Fitness Center!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        panel.add(welcomeLabel, BorderLayout.CENTER);

        JButton startButton = createStyledButton("Start", BUTTON_COLOR);
        startButton.addActionListener(e -> initializeMainMenu());
        panel.add(startButton, BorderLayout.SOUTH);

        setContentPane(panel);
        pack();
        setVisible(true);
    }

    private void initializeMainMenu() {
        getContentPane().removeAll();
        setTitle("FCS Main Menu");

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JButton loginButton = createStyledButton("Login", BUTTON_COLOR);
        loginButton.addActionListener(e -> displayLoginDialog());
        JButton registerButton = createStyledButton("Register", BUTTON_COLOR);
        registerButton.addActionListener(e -> displayRegistrationDialog());
        JButton exitButton = createStyledButton("Exit", CANCEL_COLOR);
        exitButton.addActionListener(e -> System.exit(0));

        panel.add(loginButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(registerButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(exitButton);

        setContentPane(panel);
        pack();
        revalidate();
        repaint();
    }

    private void displayLoginDialog() {
        JDialog dialog = createStyledDialog("Login");
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(LABEL_FONT);
        panel.add(usernameLabel, gbc);

        gbc.gridx = 1;
        JTextField usernameField = new JTextField(15);
        usernameField.setFont(INPUT_FONT);
        panel.add(usernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(LABEL_FONT);
        panel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        JPasswordField passwordField = new JPasswordField(15);
        passwordField.setFont(INPUT_FONT);
        panel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        JButton loginButton = createStyledButton("Login", BUTTON_COLOR);
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            User user = authService.authenticate(username, password);
            if (user != null) {
                JOptionPane.showMessageDialog(dialog, "Welcome, " + user.getName() + "!", "Login Successful", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                handleUserActions(user);
            } else {
                JOptionPane.showMessageDialog(dialog, "Invalid username or password.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(loginButton, gbc);

        dialog.setContentPane(panel);
        dialog.pack();
        dialog.setVisible(true);
    }

    private void displayRegistrationDialog() {
        JDialog dialog = createStyledDialog("Register");
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel nameLabel = new JLabel("Full Name:");
        nameLabel.setFont(LABEL_FONT);
        panel.add(nameLabel, gbc);

        gbc.gridx = 1;
        JTextField nameField = new JTextField(15);
        nameField.setFont(INPUT_FONT);
        panel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel emailLabel = new JLabel("Email (Username):");
        emailLabel.setFont(LABEL_FONT);
        panel.add(emailLabel, gbc);

        gbc.gridx = 1;
        JTextField emailField = new JTextField(15);
        emailField.setFont(INPUT_FONT);
        panel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(LABEL_FONT);
        panel.add(passwordLabel, gbc);

        gbc.gridx = 1;
        JPasswordField passwordField = new JPasswordField(15);
        passwordField.setFont(INPUT_FONT);
        panel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        JButton registerButton = createStyledButton("Register", BUTTON_COLOR);
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
        panel.add(registerButton, gbc);

        dialog.setContentPane(panel);
        dialog.pack();
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

        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JTextArea scheduleArea = new JTextArea(member.checkSchedule(schedule));
        scheduleArea.setEditable(false);
        scheduleArea.setFont(INPUT_FONT);
        JScrollPane scrollPane = new JScrollPane(scheduleArea);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));

        JButton viewSessionsButton = createStyledButton("View Sessions", BUTTON_COLOR);
        viewSessionsButton.setToolTipText("View all available workout sessions");
        viewSessionsButton.addActionListener(e -> scheduleArea.setText(member.viewSessions(schedule)));

        JButton bookSessionButton = createStyledButton("Book Session", BUTTON_COLOR);
        bookSessionButton.setToolTipText("Book a workout session");
        bookSessionButton.addActionListener(e -> {
            JDialog dialog = createStyledDialog("Book Session");
            JPanel dialogPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            JTextArea sessionsArea = new JTextArea(schedule.getFormattedSchedule());
            sessionsArea.setEditable(false);
            sessionsArea.setFont(INPUT_FONT);
            JScrollPane sessionsScroll = new JScrollPane(sessionsArea);
            sessionsScroll.setPreferredSize(new Dimension(300, 100));
            dialogPanel.add(sessionsScroll, gbc);

            gbc.gridy = 1;
            gbc.gridwidth = 1;
            JLabel sessionLabel = new JLabel("Select Session:");
            sessionLabel.setFont(LABEL_FONT);
            dialogPanel.add(sessionLabel, gbc);

            gbc.gridx = 1;
            JComboBox<String> sessionCombo = new JComboBox<>(
                    schedule.getScheduledSessions().stream()
                            .map(s -> s.getSessionID() + ": " + s.getExerciseType() + " on " + s.getDateTime())
                            .toArray(String[]::new));
            sessionCombo.setFont(INPUT_FONT);
            dialogPanel.add(sessionCombo, gbc);

            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.gridwidth = 2;
            JButton bookButton = createStyledButton("Book", BUTTON_COLOR);
            bookButton.addActionListener(evt -> {
                String sessionSelection = (String) sessionCombo.getSelectedItem();
                if (sessionSelection == null) {
                    JOptionPane.showMessageDialog(dialog, "Please select a session.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String sessionId = sessionSelection.split(":")[0];
                WorkoutSession session = schedule.getScheduledSessions().stream()
                        .filter(s -> s.getSessionID().equals(sessionId))
                        .findFirst().orElse(null);
                if (session == null) {
                    JOptionPane.showMessageDialog(dialog, "Session not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Reservation reservation = Reservation.createReservation("RES" + System.currentTimeMillis(), member, session);
                if (reservation != null) {
                    member.reserveSession(session);
                    JOptionPane.showMessageDialog(dialog, "Session booked successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    NotificationService.sendBookingConfirmation(
                            member.getUsername(),
                            session.getExerciseType() + " on " + session.getDateTime());
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Booking failed. Session may be full or already reserved.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            dialogPanel.add(bookButton, gbc);

            dialog.setContentPane(dialogPanel);
            dialog.pack();
            dialog.setVisible(true);
        });

        JButton cancelReservationButton = createStyledButton("Cancel Reservation", BUTTON_COLOR);
        cancelReservationButton.setToolTipText("Cancel a booked session");
        cancelReservationButton.addActionListener(e -> {
            JDialog dialog = createStyledDialog("Cancel Reservation");
            JPanel dialogPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridwidth = 2;
            StringBuilder reservations = new StringBuilder("Your Reservations:\n");
            if (member.getReservedSessions().isEmpty()) {
                reservations.append("No reservations found.");
            } else {
                for (WorkoutSession s : member.getReservedSessions()) {
                    reservations.append(s.getSessionID()).append(": ")
                            .append(s.getExerciseType()).append(" on ")
                            .append(s.getDateTime()).append("\n");
                }
            }
            JTextArea reservationsArea = new JTextArea(reservations.toString());
            reservationsArea.setEditable(false);
            reservationsArea.setFont(INPUT_FONT);
            JScrollPane reservationsScroll = new JScrollPane(reservationsArea);
            reservationsScroll.setPreferredSize(new Dimension(300, 100));
            dialogPanel.add(reservationsScroll, gbc);

            gbc.gridy = 1;
            gbc.gridwidth = 1;
            JLabel sessionLabel = new JLabel("Select Reservation:");
            sessionLabel.setFont(LABEL_FONT);
            dialogPanel.add(sessionLabel, gbc);

            gbc.gridx = 1;
            JComboBox<String> reservationCombo = new JComboBox<>(
                    member.getReservedSessions().stream()
                            .map(s -> s.getSessionID() + ": " + s.getExerciseType() + " on " + s.getDateTime())
                            .toArray(String[]::new));
            reservationCombo.setFont(INPUT_FONT);
            dialogPanel.add(reservationCombo, gbc);

            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.gridwidth = 2;
            JButton cancelButton = createStyledButton("Cancel", BUTTON_COLOR);
            cancelButton.addActionListener(evt -> {
                String reservationSelection = (String) reservationCombo.getSelectedItem();
                if (reservationSelection == null) {
                    JOptionPane.showMessageDialog(dialog, "Please select a reservation.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String sessionId = reservationSelection.split(":")[0];
                WorkoutSession toCancel = member.getReservedSessions().stream()
                        .filter(s -> s.getSessionID().equals(sessionId))
                        .findFirst().orElse(null);
                if (toCancel == null) {
                    JOptionPane.showMessageDialog(dialog, "Reservation not found.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                Reservation reservation = new Reservation("RES" + System.currentTimeMillis(), member, toCancel);
                if (reservation.cancelReservation()) {
                    member.cancelReservation(toCancel);
                    JOptionPane.showMessageDialog(dialog, "Reservation cancelled successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    NotificationService.sendCancellationNotification(
                            member.getUsername(),
                            toCancel.getExerciseType() + " on " + toCancel.getDateTime());
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Cancellation failed.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            dialogPanel.add(cancelButton, gbc);

            dialog.setContentPane(dialogPanel);
            dialog.pack();
            dialog.setVisible(true);
        });

        JButton logoutButton = createStyledButton("Logout", CANCEL_COLOR);
        logoutButton.setToolTipText("Return to main menu");
        logoutButton.addActionListener(e -> initializeMainMenu());

        buttonPanel.add(viewSessionsButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(bookSessionButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(cancelReservationButton);
        buttonPanel.add(Box.createHorizontalStrut(10));
        buttonPanel.add(logoutButton);

        panel.add(buttonPanel, BorderLayout.SOUTH);
        setContentPane(panel);
        pack();
        revalidate();
        repaint();
    }

    private void trainerPanel(Trainer trainer) {
        getContentPane().removeAll();
        setTitle("Trainer Panel");

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton viewSessionsButton = createStyledButton("View Assigned Sessions", BUTTON_COLOR);
        viewSessionsButton.setToolTipText("View your assigned workout sessions");
        viewSessionsButton.addActionListener(e -> {
            StringBuilder sessions = new StringBuilder("Assigned Sessions:\n");
            for (WorkoutSession session : trainer.getAssignedSessions()) {
                sessions.append("Session: ").append(session.getExerciseType())
                        .append(" | Date: ").append(session.getDateTime())
                        .append(" | Room: ").append(session.getRoom() != null ? session.getRoom().getName() : "N/A").append("\n");
            }
            JOptionPane.showMessageDialog(this, sessions.toString(), "Assigned Sessions", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton addAvailabilityButton = createStyledButton("Add Availability", BUTTON_COLOR);
        addAvailabilityButton.setToolTipText("Add available time slots for training");
        addAvailabilityButton.addActionListener(e -> {
            JDialog dialog = createStyledDialog("Add Availability");
            JPanel dialogPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            gbc.gridx = 0;
            gbc.gridy = 0;
            JLabel dateLabel = new JLabel("Select Date:");
            dateLabel.setFont(LABEL_FONT);
            dialogPanel.add(dateLabel, gbc);

            gbc.gridx = 1;
            List<LocalDate> futureDates = new ArrayList<>();
            for (int i = 0; i < 30; i++) {
                futureDates.add(LocalDate.now().plusDays(i));
            }
            JComboBox<String> dateCombo = new JComboBox<>(
                    futureDates.stream().map(LocalDate::toString).toArray(String[]::new));
            dateCombo.setFont(INPUT_FONT);
            dialogPanel.add(dateCombo, gbc);

            gbc.gridx = 0;
            gbc.gridy = 1;
            JLabel startTimeLabel = new JLabel("Start Time:");
            startTimeLabel.setFont(LABEL_FONT);
            dialogPanel.add(startTimeLabel, gbc);

            gbc.gridx = 1;
            String[] timeSlots = {"09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00"};
            JComboBox<String> startTimeCombo = new JComboBox<>(timeSlots);
            startTimeCombo.setFont(INPUT_FONT);
            dialogPanel.add(startTimeCombo, gbc);

            gbc.gridx = 0;
            gbc.gridy = 2;
            JLabel endTimeLabel = new JLabel("End Time:");
            endTimeLabel.setFont(LABEL_FONT);
            dialogPanel.add(endTimeLabel, gbc);

            gbc.gridx = 1;
            JComboBox<String> endTimeCombo = new JComboBox<>(timeSlots);
            endTimeCombo.setFont(INPUT_FONT);
            dialogPanel.add(endTimeCombo, gbc);

            gbc.gridx = 0;
            gbc.gridy = 3;
            gbc.gridwidth = 2;
            JButton addButton = createStyledButton("Add", BUTTON_COLOR);
            addButton.addActionListener(evt -> {
                try {
                    LocalDate date = LocalDate.parse((String) dateCombo.getSelectedItem());
                    LocalTime startTime = LocalTime.parse((String) startTimeCombo.getSelectedItem());
                    LocalTime endTime = LocalTime.parse((String) endTimeCombo.getSelectedItem());
                    if (startTime.isAfter(endTime) || startTime.equals(endTime)) {
                        JOptionPane.showMessageDialog(dialog, "Start time must be before end time.", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    Availability slot = new Availability(date, startTime, endTime);
                    trainer.addAvailabilitySlot(slot);
                    JOptionPane.showMessageDialog(dialog, "Availability added!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, "Invalid input. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            dialogPanel.add(addButton, gbc);

            dialog.setContentPane(dialogPanel);
            dialog.pack();
            dialog.setVisible(true);
        });

        JButton checkScheduleButton = createStyledButton("Check Schedule", BUTTON_COLOR);
        checkScheduleButton.setToolTipText("View the fitness center schedule");
        checkScheduleButton.addActionListener(e -> {
            JTextArea scheduleText = new JTextArea(schedule.getFormattedSchedule());
            scheduleText.setEditable(false);
            scheduleText.setFont(INPUT_FONT);
            JOptionPane.showMessageDialog(this, new JScrollPane(scheduleText), "Fitness Center Schedule", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton logoutButton = createStyledButton("Logout", CANCEL_COLOR);
        logoutButton.setToolTipText("Return to main menu");
        logoutButton.addActionListener(e -> initializeMainMenu());

        panel.add(viewSessionsButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(addAvailabilityButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(checkScheduleButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(logoutButton);

        setContentPane(panel);
        pack();
        revalidate();
        repaint();
    }

    private void adminPanel(Admin admin) {
        getContentPane().removeAll();
        setTitle("Admin Panel");

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JButton scheduleSessionButton = createStyledButton("Schedule New Workout Session", BUTTON_COLOR);
        scheduleSessionButton.setToolTipText("Schedule a new workout session");
        scheduleSessionButton.addActionListener(e -> displayScheduleSessionDialog(admin));

        JButton viewUsersButton = createStyledButton("View All Users", BUTTON_COLOR);
        viewUsersButton.setToolTipText("View all registered users");
        viewUsersButton.addActionListener(e -> {
            JTextArea userList = new JTextArea(registrationService.displayAllUsers());
            userList.setEditable(false);
            userList.setFont(INPUT_FONT);
            JOptionPane.showMessageDialog(this, new JScrollPane(userList), "Registered Users", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton viewRoomBookingsButton = createStyledButton("View Room Bookings", BUTTON_COLOR);
        viewRoomBookingsButton.setToolTipText("View booked times for all rooms");
        viewRoomBookingsButton.addActionListener(e -> {
            JDialog dialog = createStyledDialog("View Room Bookings");
            JPanel dialogPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            gbc.gridx = 0;
            gbc.gridy = 0;
            JLabel roomLabel = new JLabel("Select Room:");
            roomLabel.setFont(LABEL_FONT);
            dialogPanel.add(roomLabel, gbc);

            gbc.gridx = 1;
            List<Room> rooms = getRooms();
            JComboBox<String> roomCombo = new JComboBox<>(
                    rooms.stream().map(room -> room.getName() + " (ID: " + room.getId() + ")").toArray(String[]::new));
            roomCombo.setFont(INPUT_FONT);
            dialogPanel.add(roomCombo, gbc);

            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.gridwidth = 2;
            JTextArea bookingsArea = new JTextArea();
            bookingsArea.setEditable(false);
            bookingsArea.setFont(INPUT_FONT);
            JScrollPane bookingsScroll = new JScrollPane(bookingsArea);
            bookingsScroll.setPreferredSize(new Dimension(300, 100));
            dialogPanel.add(bookingsScroll, gbc);

            roomCombo.addActionListener(evt -> {
                String roomSelection = (String) roomCombo.getSelectedItem();
                if (roomSelection != null) {
                    int roomId = Integer.parseInt(roomSelection.replaceAll(".*ID: (\\d+)\\)", "$1"));
                    Room room = rooms.stream().filter(r -> r.getId() == roomId).findFirst().orElse(null);
                    if (room != null) {
                        bookingsArea.setText(room.getBookedTimes());
                    }
                }
            });

            dialog.setContentPane(dialogPanel);
            dialog.pack();
            dialog.setVisible(true);
        });

        JButton viewTrainerAvailabilityButton = createStyledButton("View Trainer Availability", BUTTON_COLOR);
        viewTrainerAvailabilityButton.setToolTipText("View availability slots for trainers");
        viewTrainerAvailabilityButton.addActionListener(e -> {
            JDialog dialog = createStyledDialog("View Trainer Availability");
            JPanel dialogPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            gbc.gridx = 0;
            gbc.gridy = 0;
            JLabel trainerLabel = new JLabel("Select Trainer:");
            trainerLabel.setFont(LABEL_FONT);
            dialogPanel.add(trainerLabel, gbc);

            gbc.gridx = 1;
            List<User> trainers = getTrainers();
            JComboBox<String> trainerCombo = new JComboBox<>(
                    trainers.stream().map(t -> t.getUsername() + " (" + t.getName() + ")").toArray(String[]::new));
            trainerCombo.setFont(INPUT_FONT);
            dialogPanel.add(trainerCombo, gbc);

            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.gridwidth = 2;
            JTextArea availabilityArea = new JTextArea();
            availabilityArea.setEditable(false);
            availabilityArea.setFont(INPUT_FONT);
            JScrollPane availabilityScroll = new JScrollPane(availabilityArea);
            availabilityScroll.setPreferredSize(new Dimension(300, 100));
            dialogPanel.add(availabilityScroll, gbc);

            trainerCombo.addActionListener(evt -> {
                String trainerSelection = (String) trainerCombo.getSelectedItem();
                if (trainerSelection != null) {
                    String username = trainerSelection.split(" ")[0];
                    Trainer trainer = (Trainer) trainers.stream()
                            .filter(t -> t.getUsername().equals(username)).findFirst().orElse(null);
                    if (trainer != null) {
                        availabilityArea.setText(trainer.getFormattedAvailability());
                    }
                }
            });

            dialog.setContentPane(dialogPanel);
            dialog.pack();
            dialog.setVisible(true);
        });

        JButton addRoomButton = createStyledButton("Add Room", BUTTON_COLOR);
        addRoomButton.setToolTipText("Add a new room to the fitness center");
        addRoomButton.addActionListener(e -> {
            JDialog dialog = createStyledDialog("Add Room");
            JPanel dialogPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            gbc.gridx = 0;
            gbc.gridy = 0;
            JLabel nameLabel = new JLabel("Room Name:");
            nameLabel.setFont(LABEL_FONT);
            dialogPanel.add(nameLabel, gbc);

            gbc.gridx = 1;
            JTextField nameField = new JTextField(15);
            nameField.setFont(INPUT_FONT);
            dialogPanel.add(nameField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 1;
            JLabel capacityLabel = new JLabel("Capacity:");
            capacityLabel.setFont(LABEL_FONT);
            dialogPanel.add(capacityLabel, gbc);

            gbc.gridx = 1;
            Integer[] capacities = {5, 10, 15, 20, 25};
            JComboBox<Integer> capacityCombo = new JComboBox<>(capacities);
            capacityCombo.setFont(INPUT_FONT);
            dialogPanel.add(capacityCombo, gbc);

            gbc.gridx = 0;
            gbc.gridy = 2;
            JLabel descLabel = new JLabel("Description:");
            descLabel.setFont(LABEL_FONT);
            dialogPanel.add(descLabel, gbc);

            gbc.gridx = 1;
            JTextField descField = new JTextField(15);
            descField.setFont(INPUT_FONT);
            dialogPanel.add(descField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 3;
            gbc.gridwidth = 2;
            JButton addButton = createStyledButton("Add", BUTTON_COLOR);
            addButton.addActionListener(evt -> {
                String name = nameField.getText();
                Integer capacity = (Integer) capacityCombo.getSelectedItem();
                String description = descField.getText();

                if (name.isBlank() || capacity == null) {
                    JOptionPane.showMessageDialog(dialog, "Room name and capacity are required.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                Room newRoom = new Room(name, 0, capacity, description);
                if (newRoom.addRoom()) {
                    JOptionPane.showMessageDialog(dialog, "Room added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to add room. Name may already exist.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            dialogPanel.add(addButton, gbc);

            dialog.setContentPane(dialogPanel);
            dialog.pack();
            dialog.setVisible(true);
        });

        JButton createTrainerButton = createStyledButton("Create Trainer Account", BUTTON_COLOR);
        createTrainerButton.setToolTipText("Create a new trainer account");
        createTrainerButton.addActionListener(e -> {
            JDialog dialog = createStyledDialog("Create Trainer");
            JPanel dialogPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            gbc.gridx = 0;
            gbc.gridy = 0;
            JLabel nameLabel = new JLabel("Name:");
            nameLabel.setFont(LABEL_FONT);
            dialogPanel.add(nameLabel, gbc);

            gbc.gridx = 1;
            JTextField nameField = new JTextField(15);
            nameField.setFont(INPUT_FONT);
            dialogPanel.add(nameField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 1;
            JLabel usernameLabel = new JLabel("Username:");
            usernameLabel.setFont(LABEL_FONT);
            dialogPanel.add(usernameLabel, gbc);

            gbc.gridx = 1;
            JTextField usernameField = new JTextField(15);
            usernameField.setFont(INPUT_FONT);
            dialogPanel.add(usernameField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 2;
            JLabel passwordLabel = new JLabel("Password:");
            passwordLabel.setFont(LABEL_FONT);
            dialogPanel.add(passwordLabel, gbc);

            gbc.gridx = 1;
            JPasswordField passwordField = new JPasswordField(15);
            passwordField.setFont(INPUT_FONT);
            dialogPanel.add(passwordField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 3;
            gbc.gridwidth = 2;
            JButton createButton = createStyledButton("Create", BUTTON_COLOR);
            createButton.addActionListener(evt -> {
                if (admin.createTrainerAccount(nameField.getText(), usernameField.getText(), new String(passwordField.getPassword())) != null) {
                    JOptionPane.showMessageDialog(dialog, "Trainer account created!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to create trainer account.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            dialogPanel.add(createButton, gbc);

            dialog.setContentPane(dialogPanel);
            dialog.pack();
            dialog.setVisible(true);
        });

        JButton removeTrainerButton = createStyledButton("Remove Trainer Account", BUTTON_COLOR);
        removeTrainerButton.setToolTipText("Remove an existing trainer account");
        removeTrainerButton.addActionListener(e -> {
            JDialog dialog = createStyledDialog("Remove Trainer");
            JPanel dialogPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            gbc.gridx = 0;
            gbc.gridy = 0;
            JLabel trainerLabel = new JLabel("Select Trainer:");
            trainerLabel.setFont(LABEL_FONT);
            dialogPanel.add(trainerLabel, gbc);

            gbc.gridx = 1;
            List<User> trainers = getTrainers();
            JComboBox<String> trainerCombo = new JComboBox<>(
                    trainers.stream().map(t -> t.getUsername() + " (" + t.getName() + ")").toArray(String[]::new));
            trainerCombo.setFont(INPUT_FONT);
            dialogPanel.add(trainerCombo, gbc);

            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.gridwidth = 2;
            JButton removeButton = createStyledButton("Remove", BUTTON_COLOR);
            removeButton.addActionListener(evt -> {
                String trainerSelection = (String) trainerCombo.getSelectedItem();
                if (trainerSelection == null) {
                    JOptionPane.showMessageDialog(dialog, "Please select a trainer.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                String username = trainerSelection.split(" ")[0];
                if (admin.removeTrainerAccount(username)) {
                    JOptionPane.showMessageDialog(dialog, "Trainer account removed!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to remove trainer account.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            dialogPanel.add(removeButton, gbc);

            dialog.setContentPane(dialogPanel);
            dialog.pack();
            dialog.setVisible(true);
        });

        JButton addExerciseButton = createStyledButton("Add Exercise", BUTTON_COLOR);
        addExerciseButton.setToolTipText("Add a new exercise type");
        addExerciseButton.addActionListener(e -> {
            JDialog dialog = createStyledDialog("Add Exercise");
            JPanel dialogPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            gbc.gridx = 0;
            gbc.gridy = 0;
            JLabel nameLabel = new JLabel("Exercise Name:");
            nameLabel.setFont(LABEL_FONT);
            dialogPanel.add(nameLabel, gbc);

            gbc.gridx = 1;
            JTextField nameField = new JTextField(15);
            nameField.setFont(INPUT_FONT);
            dialogPanel.add(nameField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 1;
            JLabel descLabel = new JLabel("Description:");
            descLabel.setFont(LABEL_FONT);
            dialogPanel.add(descLabel, gbc);

            gbc.gridx = 1;
            JTextField descField = new JTextField(15);
            descField.setFont(INPUT_FONT);
            dialogPanel.add(descField, gbc);

            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.gridwidth = 2;
            JButton addButton = createStyledButton("Add", BUTTON_COLOR);
            addButton.addActionListener(evt -> {
                if (schedule.addExercise(nameField.getText(), descField.getText())) {
                    JOptionPane.showMessageDialog(dialog, "Exercise added!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to add exercise.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            dialogPanel.add(addButton, gbc);

            dialog.setContentPane(dialogPanel);
            dialog.pack();
            dialog.setVisible(true);
        });

        JButton removeExerciseButton = createStyledButton("Remove Exercise", BUTTON_COLOR);
        removeExerciseButton.setToolTipText("Remove an existing exercise type");
        removeExerciseButton.addActionListener(e -> {
            JDialog dialog = createStyledDialog("Remove Exercise");
            JPanel dialogPanel = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            gbc.gridx = 0;
            gbc.gridy = 0;
            JLabel exerciseLabel = new JLabel("Select Exercise:");
            exerciseLabel.setFont(LABEL_FONT);
            dialogPanel.add(exerciseLabel, gbc);

            gbc.gridx = 1;
            List<String> exerciseTypes = getExerciseTypes();
            JComboBox<String> exerciseCombo = new JComboBox<>(exerciseTypes.toArray(new String[0]));
            exerciseCombo.setFont(INPUT_FONT);
            dialogPanel.add(exerciseCombo, gbc);

            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.gridwidth = 2;
            JButton removeButton = createStyledButton("Remove", BUTTON_COLOR);
            removeButton.addActionListener(evt -> {
                String exerciseName = (String) exerciseCombo.getSelectedItem();
                if (exerciseName == null) {
                    JOptionPane.showMessageDialog(dialog, "Please select an exercise.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (schedule.removeExercise(exerciseName)) {
                    JOptionPane.showMessageDialog(dialog, "Exercise removed!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to remove exercise.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            dialogPanel.add(removeButton, gbc);

            dialog.setContentPane(dialogPanel);
            dialog.pack();
            dialog.setVisible(true);
        });

        JButton checkScheduleButton = createStyledButton("Check Schedule", BUTTON_COLOR);
        checkScheduleButton.setToolTipText("View the fitness center schedule");
        checkScheduleButton.addActionListener(e -> {
            JTextArea scheduleText = new JTextArea(schedule.getFormattedSchedule());
            scheduleText.setEditable(false);
            scheduleText.setFont(INPUT_FONT);
            JOptionPane.showMessageDialog(this, new JScrollPane(scheduleText), "Fitness Center Schedule", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton logoutButton = createStyledButton("Logout", CANCEL_COLOR);
        logoutButton.setToolTipText("Return to main menu");
        logoutButton.addActionListener(e -> initializeMainMenu());

        panel.add(scheduleSessionButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(viewUsersButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(viewRoomBookingsButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(viewTrainerAvailabilityButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(addRoomButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(createTrainerButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(removeTrainerButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(addExerciseButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(removeExerciseButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(checkScheduleButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(logoutButton);

        setContentPane(panel);
        pack();
        revalidate();
        repaint();
    }

    private void displayScheduleSessionDialog(Admin admin) {
        JDialog dialog = createStyledDialog("Schedule New Workout Session");
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel exerciseLabel = new JLabel("Exercise Type:");
        exerciseLabel.setFont(LABEL_FONT);
        panel.add(exerciseLabel, gbc);

        gbc.gridx = 1;
        List<String> exerciseTypes = getExerciseTypes();
        JComboBox<String> exerciseCombo = new JComboBox<>(exerciseTypes.toArray(new String[0]));
        exerciseCombo.setFont(INPUT_FONT);
        panel.add(exerciseCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel trainerLabel = new JLabel("Trainer:");
        trainerLabel.setFont(LABEL_FONT);
        panel.add(trainerLabel, gbc);

        gbc.gridx = 1;
        List<User> trainers = getTrainers();
        JComboBox<String> trainerCombo = new JComboBox<>(
                trainers.stream().map(t -> t.getUsername() + " (" + t.getName() + ")").toArray(String[]::new));
        trainerCombo.setFont(INPUT_FONT);
        panel.add(trainerCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel roomLabel = new JLabel("Room:");
        roomLabel.setFont(LABEL_FONT);
        panel.add(roomLabel, gbc);

        gbc.gridx = 1;
        List<Room> rooms = getRooms();
        JComboBox<String> roomCombo = new JComboBox<>(
                rooms.stream().map(room -> room.getName() + " (ID: " + room.getId() + ")").toArray(String[]::new));
        roomCombo.setFont(INPUT_FONT);
        panel.add(roomCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel dateLabel = new JLabel("Date:");
        dateLabel.setFont(LABEL_FONT);
        panel.add(dateLabel, gbc);

        gbc.gridx = 1;
        List<LocalDate> futureDates = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            futureDates.add(LocalDate.now().plusDays(i));
        }
        JComboBox<String> dateCombo = new JComboBox<>(
                futureDates.stream().map(LocalDate::toString).toArray(String[]::new));
        dateCombo.setFont(INPUT_FONT);
        panel.add(dateCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel timeLabel = new JLabel("Time:");
        timeLabel.setFont(LABEL_FONT);
        panel.add(timeLabel, gbc);

        gbc.gridx = 1;
        String[] timeSlots = {"09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00"};
        JComboBox<String> timeCombo = new JComboBox<>(timeSlots);
        timeCombo.setFont(INPUT_FONT);
        panel.add(timeCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel capacityLabel = new JLabel("Max Capacity:");
        capacityLabel.setFont(LABEL_FONT);
        panel.add(capacityLabel, gbc);

        gbc.gridx = 1;
        Integer[] capacities = {5, 10, 15, 20, 25};
        JComboBox<Integer> capacityCombo = new JComboBox<>(capacities);
        capacityCombo.setFont(INPUT_FONT);
        panel.add(capacityCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        JButton scheduleButton = createStyledButton("Schedule", BUTTON_COLOR);
        scheduleButton.addActionListener(e -> {
            try {
                String exerciseType = (String) exerciseCombo.getSelectedItem();
                String trainerSelection = (String) trainerCombo.getSelectedItem();
                String roomSelection = (String) roomCombo.getSelectedItem();
                String date = (String) dateCombo.getSelectedItem();
                String time = (String) timeCombo.getSelectedItem();
                int capacity = (Integer) capacityCombo.getSelectedItem();

                if (exerciseType == null || trainerSelection == null || roomSelection == null) {
                    JOptionPane.showMessageDialog(dialog, "Please select all required fields.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String trainerUsername = trainerSelection.split(" ")[0];
                int roomId = Integer.parseInt(roomSelection.replaceAll(".*ID: (\\d+)\\)", "$1"));
                Room room = rooms.stream().filter(r -> r.getId() == roomId).findFirst().orElse(null);
                Trainer trainer = (Trainer) trainers.stream()
                        .filter(t -> t.getUsername().equals(trainerUsername)).findFirst().orElse(null);

                if (trainer == null || room == null) {
                    JOptionPane.showMessageDialog(dialog, "Invalid trainer or room.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String sessionId = "SES" + System.currentTimeMillis();
                LocalDateTime dateTime = LocalDateTime.parse(date + "T" + time + ":00");

                WorkoutSession session = new WorkoutSession(
                        sessionId, exerciseType, dateTime, capacity, room, trainer);

                if (schedule.scheduleWorkout(admin, session, trainer, room)) {
                    JOptionPane.showMessageDialog(dialog, "Session scheduled successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } else {
                    String errorMsg = "Failed to schedule session. ";
                    if (!room.isAvailable(dateTime)) {
                        errorMsg += "Room " + room.getName() + " is booked at " + dateTime + ". Check room bookings for details.";
                    } else {
                        errorMsg += "Trainer " + trainer.getUsername() + " is not available at " + dateTime + ".\nAvailable slots:\n" + trainer.getFormattedAvailability();
                    }
                    JOptionPane.showMessageDialog(dialog, new JScrollPane(new JTextArea(errorMsg)), "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid input: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panel.add(scheduleButton, gbc);

        dialog.setContentPane(panel);
        dialog.pack();
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

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(LABEL_FONT);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        return button;
    }

    private JDialog createStyledDialog(String title) {
        JDialog dialog = new JDialog(this, title, true);
        dialog.setLocationRelativeTo(this);
        return dialog;
    }

    public static void main(String[] args) {
        DatabaseHandler dbHandler = new DatabaseHandler();
        AuthService authService = new AuthService(dbHandler);
        SwingUtilities.invokeLater(() -> new FCSAppUI(authService));
    }
}