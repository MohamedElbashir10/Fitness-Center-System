import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class AuthService {
    private final DatabaseHandler dbHandler;

    public AuthService(DatabaseHandler dbHandler) {
        this.dbHandler = dbHandler;
    }

    // Handles user login
    public User authenticate(String username, String password) {
        try (Connection conn = dbHandler.connect()) {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                stmt.setString(2, password);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return new User(
                                rs.getInt("id"),
                                rs.getString("name"),
                                rs.getString("username"),
                                rs.getString("password"),
                                rs.getString("role")
                        );
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error during authentication: " + e.getMessage());
        }
        return null;
    }

    // Handles user registration
    public boolean registerUser(User user) {
        // Validate user fields
        if (user.getName() == null || user.getName().isEmpty() ||
            user.getUsername() == null || user.getUsername().isEmpty() ||
            user.getPassword() == null || user.getPassword().isEmpty() ||
            user.getRole() == null || user.getRole().isEmpty()) {
            System.err.println("Invalid user data: one or more fields are null or empty");
            return false;
        }

        // TODO: Hash the password before storing (e.g., using BCrypt)
        // String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());

        try (Connection conn = dbHandler.connect()) {
            String query = "INSERT INTO users (name, username, password, role) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, user.getName());
                stmt.setString(2, user.getUsername());
                stmt.setString(3, user.getPassword()); // Use hashedPassword in production
                stmt.setString(4, user.getRole());
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    // Retrieve the auto-generated ID
                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            user.setId(rs.getInt(1));
                        }
                    }
                    System.out.println("User registered: " + user.getName());
                    return true;
                }
            }
        } catch (Exception e) {
            System.err.println("Error during registration: " + e.getMessage());
        }
        return false;
    }

    // Verifies if a user exists
    public boolean isUserExists(String username) {
        try (Connection conn = dbHandler.connect()) {
            String query = "SELECT COUNT(*) FROM users WHERE username = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error checking user existence: " + e.getMessage());
        }
        return false;
    }
}