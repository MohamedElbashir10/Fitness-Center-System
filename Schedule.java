import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;

public class Schedule {
    private List<WorkoutSession> scheduledSessions;

    public Schedule() {
        this.scheduledSessions = new ArrayList<>();
        loadSessionsFromDatabase();
    }

    private void loadSessionsFromDatabase() {
        try (Connection conn = new DatabaseHandler().connect()) {
            String query = "SELECT s.id, st.name AS exercise_type, s.start_time, s.end_time, s.room_id, s.trainer_id " +
                          "FROM sessions s JOIN session_types st ON s.type_id = st.id";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    Room room = getRoomById(rs.getInt("room_id"));
                    Trainer trainer = getTrainerById(rs.getInt("trainer_id"));
                    LocalDateTime startTime = rs.getTimestamp("start_time").toLocalDateTime();
                    WorkoutSession session = new WorkoutSession(
                        String.valueOf(rs.getInt("id")),
                        rs.getString("exercise_type"),
                        startTime,
                        15, // Default capacity
                        room,
                        trainer
                    );
                    scheduledSessions.add(session);
                }
            }
        } catch (SQLException e) {
            LoggerUtils.logError("Error loading sessions: " + e.getMessage());
        }
    }

    private Room getRoomById(int roomId) {
        try (Connection conn = new DatabaseHandler().connect()) {
            String query = "SELECT * FROM rooms WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, roomId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return new Room(rs.getString("name"), rs.getInt("id"), rs.getInt("capacity"), "");
                }
            }
        } catch (SQLException e) {
            LoggerUtils.logError("Error fetching room: " + e.getMessage());
        }
        return null;
    }

    private Trainer getTrainerById(int trainerId) {
        try (Connection conn = new DatabaseHandler().connect()) {
            String query = "SELECT * FROM users WHERE id = ? AND role = 'Trainer'";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, trainerId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return new Trainer(rs.getString("name"), rs.getString("username"), rs.getString("password"));
                }
            }
        } catch (SQLException e) {
            LoggerUtils.logError("Error fetching trainer: " + e.getMessage());
        }
        return null;
    }

    public boolean scheduleWorkout(Admin admin, WorkoutSession session, Trainer trainer, Room room) {
        boolean isTrainerAvailable = trainer.getAvailabilitySlots().stream()
                .anyMatch(slot -> slot.getDate().equals(session.getDateTime().toLocalDate())
                        && slot.getStartTime().isBefore(session.getDateTime().toLocalTime())
                        && slot.getEndTime().isAfter(session.getDateTime().toLocalTime()));

        if (!isTrainerAvailable) {
            LoggerUtils.logError("Trainer is not available at the selected time.");
            return false;
        }

        if (!room.isAvailable(session.getDateTime())) {
            LoggerUtils.logError("Room is already booked for another session at this time.");
            return false;
        }

        try (Connection conn = new DatabaseHandler().connect()) {
            String query = "INSERT INTO sessions (type_id, trainer_id, room_id, start_time, end_time) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                int typeId = getSessionTypeId(session.getExerciseType());
                if (typeId == -1) {
                    LoggerUtils.logError("Exercise type not found: " + session.getExerciseType());
                    return false;
                }
                stmt.setInt(1, typeId);
                stmt.setInt(2, trainer.getId());
                stmt.setInt(3, room.getId());
                stmt.setTimestamp(4, Timestamp.valueOf(session.getDateTime()));
                stmt.setTimestamp(5, Timestamp.valueOf(session.getDateTime().plusHours(1)));
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    try (ResultSet rs = stmt.getGeneratedKeys()) {
                        if (rs.next()) {
                            session.setSessionID(String.valueOf(rs.getInt(1)));
                        }
                    }
                    session.setRoom(room);
                    session.setTrainer(trainer);
                    trainer.assignSession(session);
                    room.bookRoom(session.getDateTime());
                    scheduledSessions.add(session);
                    LoggerUtils.logSuccess("Workout session scheduled by admin: " + admin.getUsername());
                    return true;
                }
            }
        } catch (SQLException e) {
            LoggerUtils.logError("Error scheduling session: " + e.getMessage());
        }
        return false;
    }

    private int getSessionTypeId(String exerciseType) {
        try (Connection conn = new DatabaseHandler().connect()) {
            String query = "SELECT id FROM session_types WHERE name = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, exerciseType);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            LoggerUtils.logError("Error fetching session type: " + e.getMessage());
        }
        return -1;
    }

    public boolean addExercise(String exerciseName, String description) {
        try (Connection conn = new DatabaseHandler().connect()) {
            String query = "INSERT INTO session_types (name, description) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, exerciseName);
                stmt.setString(2, description);
                stmt.executeUpdate();
                LoggerUtils.logSuccess("Exercise added: " + exerciseName);
                return true;
            }
        } catch (SQLException e) {
            LoggerUtils.logError("Error adding exercise: " + e.getMessage());
        }
        return false;
    }

    public boolean removeExercise(String exerciseName) {
        try (Connection conn = new DatabaseHandler().connect()) {
            String query = "DELETE FROM session_types WHERE name = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, exerciseName);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    LoggerUtils.logSuccess("Exercise removed: " + exerciseName);
                    return true;
                }
                LoggerUtils.logError("Exercise not found: " + exerciseName);
            }
        } catch (SQLException e) {
            LoggerUtils.logError("Error removing exercise: " + e.getMessage());
        }
        return false;
    }

    public String getFormattedSchedule() {
        if (scheduledSessions.isEmpty()) {
            return "No sessions scheduled.";
        }

        StringBuilder scheduleBuilder = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (WorkoutSession session : scheduledSessions) {
            scheduleBuilder.append("Session ID: ").append(session.getSessionID())
                    .append("\nExercise: ").append(session.getExerciseType())
                    .append("\nDate & Time: ").append(session.getDateTime().format(formatter))
                    .append("\nRoom: ").append(session.getRoom().getName())
                    .append("\nTrainer: ").append(session.getTrainer().getUsername())
                    .append("\n\n");
        }

        return scheduleBuilder.toString();
    }

    public void displaySchedule() {
        LoggerUtils.logSection("Fitness Center Schedule");
        LoggerUtils.logInfo(getFormattedSchedule());
    }

    public List<WorkoutSession> getScheduledSessions() {
        return scheduledSessions;
    }
}