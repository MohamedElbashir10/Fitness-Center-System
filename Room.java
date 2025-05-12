import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.sql.*;

public class Room {
    private String name;
    private int id;
    private int capacity;
    private int roomId;
    private String description;
    private Set<LocalDateTime> bookedTimes = new HashSet<>();

    public Room(String name, int id, int capacity, String description) {
        this.name = name;
        this.id = id;
        this.capacity = capacity;
        this.roomId = id;
        this.description = description;
    }

    public boolean isAvailable(LocalDateTime startTime) {
        LocalDateTime endTime = startTime.plusHours(1); // Assume 1-hour sessions
        for (LocalDateTime booked : bookedTimes) {
            LocalDateTime bookedEnd = booked.plusHours(1);
            if (startTime.isBefore(bookedEnd) && endTime.isAfter(booked)) {
                LoggerUtils.logInfo("Room " + name + " is booked from " + booked + " to " + bookedEnd);
                return false;
            }
        }
        return true;
    }

    public void bookRoom(LocalDateTime dateTime) {
        bookedTimes.add(dateTime);
    }

    public void clearBookings() {
        bookedTimes.clear();
    }

    public String getBookedTimes() {
        if (bookedTimes.isEmpty()) {
            return "No bookings for room: " + name;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return "Booked times for room " + name + ":\n" +
                bookedTimes.stream()
                        .map(time -> time.format(formatter) + " to " + time.plusHours(1).format(formatter))
                        .collect(Collectors.joining("\n"));
    }

    public boolean addRoom() {
        // Validate inputs
        if (name == null || name.trim().isEmpty()) {
            LoggerUtils.logError("Room name is empty or null");
            throw new IllegalArgumentException("Room name cannot be empty");
        }
        if (capacity <= 0) {
            LoggerUtils.logError("Invalid capacity: " + capacity);
            throw new IllegalArgumentException("Capacity must be positive");
        }

        try (Connection conn = new DatabaseHandler().connect()) {
            // Check for duplicate name
            String checkQuery = "SELECT COUNT(*) FROM rooms WHERE name = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setString(1, name);
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    LoggerUtils.logError("Room name already exists: " + name);
                    throw new IllegalArgumentException("Room name '" + name + "' already exists");
                }
            }

            // Insert new room
            String insertQuery = "INSERT INTO rooms (name, capacity, description) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, name);
                stmt.setInt(2, capacity);
                stmt.setString(3, description != null ? description : "");
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            this.id = rs.getInt(1);
                            this.roomId = this.id;
                        }
                    }
                    LoggerUtils.logSuccess("Room added: " + name + " (ID: " + id + ")");
                    return true;
                } else {
                    LoggerUtils.logError("No rows affected when adding room: " + name);
                    return false;
                }
            }
        } catch (SQLException e) {
            LoggerUtils.logError("SQL error adding room '" + name + "': " + e.getMessage());
            throw new RuntimeException("Database error: " + e.getMessage(), e);
        }
    }

    public void updateRoom() {
        // Placeholder for future implementation
    }

    public void deleteRoom() {
        // Placeholder for future implementation
    }

    public String getRoomDetails() {
        return "ID: " + roomId + " | Name: " + name + " | Capacity: " + capacity + " | Description: " + description;
    }

    // Getters
    public int getId() {
        return roomId;
    }

    public String getName() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getDescription() {
        return description;
    }
}