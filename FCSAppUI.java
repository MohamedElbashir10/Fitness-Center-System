import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FCSAppUI extends JFrame {

    private AuthService authService;

    public FCSAppUI(AuthService authService) {
        this.authService = authService;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("FCS Fitness Center System");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 10, 10));

        // Buttons
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");
        JButton viewScheduleButton = new JButton("View Schedule");
        JButton exitButton = new JButton("Exit");

        // Add action listeners
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayLoginScreen();
            }
        });

        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayRegistrationScreen();
            }
        });

        viewScheduleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayScheduleScreen();
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        // Add buttons to panel
        panel.add(loginButton);
        panel.add(registerButton);
        panel.add(viewScheduleButton);
        panel.add(exitButton);

        // Add panel to frame
        add(panel);
    }

    private void displayLoginScreen() {
        LoginUI loginUI = new LoginUI(authService);
        User user = loginUI.displayLoginScreen();
        if (user != null) {
            JOptionPane.showMessageDialog(this, "Welcome, " + user.getName() + "!", "Login Successful", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void displayRegistrationScreen() {
        JOptionPane.showMessageDialog(this, "Registration functionality is not implemented yet.", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    private void displayScheduleScreen() {
        JOptionPane.showMessageDialog(this, "Schedule viewing functionality is not implemented yet.", "Info", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        AuthService authService = new AuthService();
        SwingUtilities.invokeLater(() -> {
            FCSAppUI app = new FCSAppUI(authService);
            app.setVisible(true);
        });
    }
}