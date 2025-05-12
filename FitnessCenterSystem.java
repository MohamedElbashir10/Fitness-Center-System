import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class FitnessCenterSystem {
    private final AuthService authService;
    private final RegistrationService registrationService;
    private final Schedule schedule;
    private User loggedInUser;

    public FitnessCenterSystem(AuthService authService, RegistrationService registrationService, Schedule schedule) {
        this.authService = authService;
        this.registrationService = registrationService;
        this.schedule = schedule;
        this.loggedInUser = null;
        createMainMenu();
    }

    private void createMainMenu() {
        JFrame frame = new JFrame("Fitness Center System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 1));

        JLabel welcomeLabel = new JLabel("Welcome to the Fitness Center System!", SwingConstants.CENTER);
        panel.add(welcomeLabel);

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(e -> handleRegistration(frame));
        panel.add(registerButton);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> handleLogin(frame));
        panel.add(loginButton);

        JButton viewScheduleButton = new JButton("View Schedule");
        viewScheduleButton.addActionListener(e -> schedule.displaySchedule());

        panel.add(viewScheduleButton);

        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(e -> System.exit(0));
        panel.add(exitButton);

        frame.add(panel);
        frame.setVisible(true);
    }

    private void handleRegistration(JFrame parentFrame) {
        JDialog dialog = new JDialog(parentFrame, "Register", true);
        dialog.setSize(400, 300);
        dialog.setLayout(new GridLayout(6, 2));

        dialog.add(new JLabel("ID:"));
        JTextField idField = new JTextField();
        dialog.add(idField);

        dialog.add(new JLabel("Full Name:"));
        JTextField nameField = new JTextField();
        dialog.add(nameField);

        dialog.add(new JLabel("Email (Username):"));
        JTextField emailField = new JTextField();
        dialog.add(emailField);

        dialog.add(new JLabel("Password:"));
        JPasswordField passwordField = new JPasswordField();
        dialog.add(passwordField);

        dialog.add(new JLabel("Role (member/trainer/admin):"));
        JTextField roleField = new JTextField();
        dialog.add(roleField);

        JButton registerButton = new JButton("Register");
        registerButton.addActionListener(e -> {
            int id = Integer.parseInt(idField.getText());
            String name = nameField.getText();
            String email = emailField.getText();
            String password = new String(passwordField.getPassword());
            String role = roleField.getText();

            User newUser = registrationService.registerUser(name, email, password, role);
            if (newUser != null) {
                JOptionPane.showMessageDialog(dialog, "Registration successful!");
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Registration failed. Please try again.");
            }
        });
        dialog.add(registerButton);

        dialog.setVisible(true);
    }

    private void handleLogin(JFrame parentFrame) {
        JDialog dialog = new JDialog(parentFrame, "Login", true);
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
                loggedInUser = user;
                JOptionPane.showMessageDialog(dialog, "Logged in as: " + user.getName() + " (" + user.getRole() + ")");
                dialog.dispose();
                handleUserActions();
            } else {
                JOptionPane.showMessageDialog(dialog, "Login failed. Invalid username or password.");
            }
        });
        dialog.add(loginButton);

        dialog.setVisible(true);
    }

    private void handleUserActions() {
        if (loggedInUser == null) return;

        String role = loggedInUser.getRole().toLowerCase();
        switch (role) {
            case "member" -> memberPanel((Member) loggedInUser);
            case "trainer" -> trainerPanel((Trainer) loggedInUser);
            case "admin" -> adminPanel((Admin) loggedInUser);
            default -> JOptionPane.showMessageDialog(null, "Unknown role. Logging out.");
        }
        loggedInUser = null; // Logout after actions
    }

    private void memberPanel(Member member) {
        JOptionPane.showMessageDialog(null, "Member Panel: View Workout Sessions or Make Reservations.");
        // Implement GUI for member actions
    }

    private void trainerPanel(Trainer trainer) {
        JOptionPane.showMessageDialog(null, "Trainer Panel: View Assigned Sessions.");
        // Implement GUI for trainer actions
    }

    private void adminPanel(Admin admin) {
        JOptionPane.showMessageDialog(null, "Admin Panel: Schedule Sessions or View Users.");
        // Implement GUI for admin actions
    }
}