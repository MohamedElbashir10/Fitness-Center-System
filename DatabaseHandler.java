import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler {
    private static final String URL = "jdbc:mysql://localhost:3307/fcs_db";
    private static final String USER = "root";
    private static final String PASSWORD = "rootpassword";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Insert Availability
    public static boolean insertAvailability(LocalDate date, LocalTime start, LocalTime end, int trainerId, int roomId) {
        String query = "INSERT INTO availability (date, start_time, end_time, trainer_id, room_id) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDate(1, Date.valueOf(date));
            stmt.setTime(2, Time.valueOf(start));
            stmt.setTime(3, Time.valueOf(end));
            stmt.setInt(4, trainerId);
            stmt.setInt(5, roomId);

            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Check Availability
    public static boolean isAvailable(LocalDate date, LocalTime start, LocalTime end, int trainerId, int roomId) {
        String query = "SELECT * FROM availability WHERE date = ? AND start_time <= ? AND end_time >= ? AND trainer_id = ? AND room_id = ?";

        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDate(1, Date.valueOf(date));
            stmt.setTime(2, Time.valueOf(start));
            stmt.setTime(3, Time.valueOf(end));
            stmt.setInt(4, trainerId);
            stmt.setInt(5, roomId);

            ResultSet rs = stmt.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get All Availabilities
    public static List<String> getAllAvailabilities() {
        String query = "SELECT * FROM availability";
        List<String> result = new ArrayList<>();

        try (Connection conn = connect(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String availability = "Date: " + rs.getDate("date") +
                                      ", Start: " + rs.getTime("start_time") +
                                      ", End: " + rs.getTime("end_time") +
                                      ", Trainer ID: " + rs.getInt("trainer_id") +
                                      ", Room ID: " + rs.getInt("room_id");
                result.add(availability);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    // Delete Availability
    public static boolean deleteAvailability(LocalDate date, LocalTime start, int trainerId, int roomId) {
        String query = "DELETE FROM availability WHERE date = ? AND start_time = ? AND trainer_id = ? AND room_id = ?";

        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setDate(1, Date.valueOf(date));
            stmt.setTime(2, Time.valueOf(start));
            stmt.setInt(3, trainerId);
            stmt.setInt(4, roomId);

            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Update Availability (e.g., modify end time)
    public static boolean updateAvailability(LocalDate date, LocalTime start, LocalTime newEnd, int trainerId, int roomId) {
        String query = "UPDATE availability SET end_time = ? WHERE date = ? AND start_time = ? AND trainer_id = ? AND room_id = ?";

        try (Connection conn = connect(); PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setTime(1, Time.valueOf(newEnd));
            stmt.setDate(2, Date.valueOf(date));
            stmt.setTime(3, Time.valueOf(start));
            stmt.setInt(4, trainerId);
            stmt.setInt(5, roomId);

            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
