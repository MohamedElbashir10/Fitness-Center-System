import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuthService {
    private final DatabaseHandler dbHandler;

    public AuthService(DatabaseHandler dbHandler) {
        this.dbHandler = dbHandler;
    }

    public User authenticate(String username, String password) {
        try (Connection conn = dbHandler.connect()) {
            String query = "SELECT id, name, username, password, role FROM users WHERE username = ? AND password = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                stmt.setString(2, password);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String role = rs.getString("role");
                        return switch (role) {
                            case "Member" -> new Member(rs.getString("name"), rs.getString("username"), rs.getString("password"));
                            case "Trainer" -> new Trainer(rs.getString("name"), rs.getString("username"), rs.getString("password"));
                            case "Admin" -> new Admin(rs.getString("name"), rs.getString("username"), rs.getString("password"));
                            default -> null;
                        };
                    }
                }
            }
        } catch (SQLException e) {
            LoggerUtils.logError("Authentication error: " + e.getMessage());
        }
        LoggerUtils.logError("Authentication failed for username: " + username);
        return null;
    }

    public boolean registerUser(User user) {
        try (Connection conn = dbHandler.connect()) {
            String query = "INSERT INTO users (name, username, password, role) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, user.getName());
                stmt.setString(2, user.getUsername());
                stmt.setString(3, user.getPassword());
                stmt.setString(4, user.getRole());
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    LoggerUtils.logSuccess("User registered: " + user.getUsername());
                    return true;
                }
            }
        } catch (SQLException e) {
            LoggerUtils.logError("Registration error: " + e.getMessage());
        }
        LoggerUtils.logError("Registration failed for username: " + user.getUsername());
        return false;
    }

    public User getUserDetails(String username) {
        try (Connection conn = dbHandler.connect()) {
            String query = "SELECT id, name, username, role FROM users WHERE username = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String role = rs.getString("role");
                        return switch (role) {
                            case "Member" -> new Member(rs.getString("name"), rs.getString("username"), null);
                            case "Trainer" -> new Trainer(rs.getString("name"), rs.getString("username"), null);
                            case "Admin" -> new Admin(rs.getString("name"), rs.getString("username"), null);
                            default -> null;
                        };
                    }
                }
            }
        } catch (SQLException e) {
            LoggerUtils.logError("Error fetching user details for username " + username + ": " + e.getMessage());
        }
        LoggerUtils.logError("User not found: " + username);
        return null;
    }
}