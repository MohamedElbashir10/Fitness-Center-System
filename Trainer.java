import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Trainer extends User {
    private List<WorkoutSession> assignedSessions;
    private List<Availability> availabilitySlots;

    public Trainer(String name, String username, String password) {
        super(name, username, password, "Trainer");
        this.assignedSessions = new ArrayList<>();
        this.availabilitySlots = new ArrayList<>();
        loadAvailabilityFromDatabase();
    }

    private void loadAvailabilityFromDatabase() {
        try (Connection conn = new DatabaseHandler().connect()) {
            String query = "SELECT date, start_time, end_time FROM availability WHERE trainer_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, getId());
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    LocalDate date = rs.getDate("date").toLocalDate();
                    LocalTime startTime = rs.getTime("start_time").toLocalTime();
                    LocalTime endTime = rs.getTime("end_time").toLocalTime();
                    availabilitySlots.add(new Availability(date, startTime, endTime));
                }
            }
        } catch (SQLException e) {
            LoggerUtils.logError("Error loading availability: " + e.getMessage());
        }
    }

    public void addAvailabilitySlot(Availability slot) {
        if (slot.getStartTime().isAfter(slot.getEndTime()) || slot.getStartTime().equals(slot.getEndTime())) {
            LoggerUtils.logError("Invalid availability slot: start=" + slot.getStartTime() + ", end=" + slot.getEndTime());
            return;
        }
        try (Connection conn = new DatabaseHandler().connect()) {
            String query = "INSERT INTO availability (trainer_id, date, start_time, end_time) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, getId());
                stmt.setDate(2, java.sql.Date.valueOf(slot.getDate()));
                stmt.setTime(3, java.sql.Time.valueOf(slot.getStartTime()));
                stmt.setTime(4, java.sql.Time.valueOf(slot.getEndTime()));
                stmt.executeUpdate();
                availabilitySlots.add(slot);
                LoggerUtils.logSuccess("Availability added for trainer " + getUsername() + ": " + slot);
            }
        } catch (SQLException e) {
            LoggerUtils.logError("Error adding availability: " + e.getMessage());
        }
    }

    public String getFormattedAvailability() {
        if (availabilitySlots.isEmpty()) {
            return "No availability slots for trainer: " + getUsername();
        }
        StringBuilder sb = new StringBuilder("Availability for trainer " + getUsername() + ":\n");
        for (Availability slot : availabilitySlots) {
            sb.append(slot.getDate()).append(" from ")
              .append(slot.getStartTime()).append(" to ")
              .append(slot.getEndTime()).append("\n");
        }
        return sb.toString();
    }

    public void assignSession(WorkoutSession session) {
        assignedSessions.add(session);
    }

    public List<WorkoutSession> getAssignedSessions() {
        return assignedSessions;
    }

    public List<Availability> getAvailabilitySlots() {
        return availabilitySlots;
    }
}