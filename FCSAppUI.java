import javax.swing.*;
import java.awt.*;

public class FCSAppUI extends JFrame {

    private final AuthService authService;

    public FCSAppUI(AuthService authService) {
        this.authService = authService;
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
                JOptionPane.showMessageDialog(dialog, "Login failed. Invalid username or password.");
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
            String role = "Member"; // Default role assigned (matches database)

            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "All fields are required.");
                return;
            }

            User newUser = new User(name, email, password, role);
            boolean isRegistered = authService.registerUser(newUser);
            if (isRegistered) {
                JOptionPane.showMessageDialog(dialog, "Registration successful!");
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Registration failed. Please try again.");
            }
        });
        dialog.add(registerButton);

        dialog.setVisible(true);
    }

    private void handleUserActions(User user) {
        String role = user.getRole().toLowerCase();
        switch (role) {
            case "member" -> JOptionPane.showMessageDialog(this, "Member Panel: Access your features.");
            case "trainer" -> JOptionPane.showMessageDialog(this, "Trainer Panel: Manage your sessions.");
            case "admin" -> JOptionPane.showMessageDialog(this, "Admin Panel: Manage the system.");
            default -> JOptionPane.showMessageDialog(this, "Unknown role. Please contact support.");
        }
    }

    public static void main(String[] args) {
        DatabaseHandler dbHandler = new DatabaseHandler();
        AuthService authService = new AuthService(dbHandler);
        SwingUtilities.invokeLater(() -> new FCSAppUI(authService));
    }
}