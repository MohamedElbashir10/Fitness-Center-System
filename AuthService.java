import java.sql.Connection;

public class AuthService {
    private final DatabaseHandler dbHandler;

    public AuthService(DatabaseHandler dbHandler) {
        this.dbHandler = dbHandler;
    }

    public boolean login(String username, String password) {
        try (Connection conn = dbHandler.connect()) {
            if (dbHandler.authenticateUser(conn, username, password)) {
                System.out.println("Login successful for user: " + username);
                return true;
            }
            System.out.println("Invalid username or password.");
            return false;
        } catch (Exception e) {
            System.err.println("Error during login: " + e.getMessage());
            return false;
        }
    }

    public boolean registerUser(User user) {
        try (Connection conn = dbHandler.connect()) {
            String query = "INSERT INTO users (id, name, username, password, role) VALUES (?, ?, ?, ?, ?)";
            try (var stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, user.getId());
                stmt.setString(2, user.getName());
                stmt.setString(3, user.getUsername());
                stmt.setString(4, user.getPassword());
                stmt.setString(5, user.getRole());
                stmt.executeUpdate();
                System.out.println("User registered: " + user.getName());
                return true;
            }
        } catch (Exception e) {
            System.err.println("Error during registration: " + e.getMessage());
            return false;
        }
    }

    public User authenticate(String username, String password) {
        try (Connection conn = dbHandler.connect()) {
            if (dbHandler.authenticateUser(conn, username, password)) {
                return dbHandler.getUserDetails(conn, username);
            }
        } catch (Exception e) {
            System.err.println("Error during authentication: " + e.getMessage());
        }
        return null;
    }
}