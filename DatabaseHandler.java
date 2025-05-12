import java.sql.*;

public class DatabaseHandler {
    public Connection connect() throws SQLException {
        String url = "jdbc:mysql://127.0.0.1:3307/fcsDatabase";
        String user = "root";
        String password = "rootpassword";
        return DriverManager.getConnection(url, user, password);
    }

    public boolean authenticateUser(Connection conn, String username, String password) throws SQLException {
        String query = "SELECT COUNT(*) FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    public User getUserDetails(Connection conn, String username) throws SQLException {
        String query = "SELECT id, name, username, role FROM users WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getString("username"),
                            null, // Password is not retrieved for security reasons
                            rs.getString("role")
                    );
                }
            }
        }
        return null;
    }
}