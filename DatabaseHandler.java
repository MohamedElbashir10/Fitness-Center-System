import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;

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
                            //rs.getInt("id"),
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
     public static boolean checkAvailability(Integer trainerId, Integer roomId, LocalDate date, LocalTime time) {
        try (Connection conn = new DatabaseHandler().connect()) {
            // Convert LocalDateTime for session checks
            Timestamp checkTime = Timestamp.valueOf(date.atTime(time));

            if (trainerId != null) {
                // Step 1: Check if trainer has an availability slot
                String availabilityQuery = "SELECT COUNT(*) FROM availability WHERE trainer_id = ? AND date = ? " +
                                          "AND start_time <= ? AND end_time >= ?";
                try (PreparedStatement stmt = conn.prepareStatement(availabilityQuery)) {
                    stmt.setInt(1, trainerId);
                    stmt.setDate(2, java.sql.Date.valueOf(date));
                    stmt.setTime(3, java.sql.Time.valueOf(time));
                    stmt.setTime(4, java.sql.Time.valueOf(time));
                    ResultSet rs = stmt.executeQuery();
                    rs.next();
                    if (rs.getInt(1) == 0) {
                        LoggerUtils.logError("Trainer has no availability for this time.");
                        return false;
                    }
                }

                // Step 2: Check for conflicting sessions
                String sessionQuery = "SELECT COUNT(*) FROM sessions WHERE trainer_id = ? AND start_time <= ? AND end_time >= ?";
                try (PreparedStatement stmt = conn.prepareStatement(sessionQuery)) {
                    stmt.setInt(1, trainerId);
                    stmt.setTimestamp(2, checkTime);
                    stmt.setTimestamp(3, checkTime);
                    ResultSet rs = stmt.executeQuery();
                    rs.next();
                    if (rs.getInt(1) > 0) {
                        LoggerUtils.logError("Trainer is already booked for this time.");
                        return false;
                    }
                }
                return true;
            }

            if (roomId != null) {
                // Check for conflicting sessions in the room
                String sessionQuery = "SELECT COUNT(*) FROM sessions WHERE room_id = ? AND start_time <= ? AND end_time >= ?";
                try (PreparedStatement stmt = conn.prepareStatement(sessionQuery)) {
                    stmt.setInt(1, roomId);
                    stmt.setTimestamp(2, checkTime);
                    stmt.setTimestamp(3, checkTime);
                    ResultSet rs = stmt.executeQuery();
                    rs.next();
                    if (rs.getInt(1) > 0) {
                        LoggerUtils.logError("Room is already booked for this time.");
                        return false;
                    }
                }
                return true;
            }

            LoggerUtils.logError("Either trainerId or roomId must be provided.");
            return false;

        } catch (SQLException e) {
            LoggerUtils.logError("Error checking availability: " + e.getMessage());
            return false;
        }
    }
}