import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class SessionBookingCoordinator {
    private final DatabaseHandler dbHandler;
    private final List<WorkoutSession> sessions;
    private final List<Trainer> trainers;
    private final List<Room> rooms;

    public SessionBookingCoordinator(List<Trainer> trainers, List<Room> rooms, List<WorkoutSession> sessions) {
        this.dbHandler = new DatabaseHandler();
        this.trainers = trainers;
        this.rooms = rooms;
        this.sessions = sessions;
    }

    public boolean bookSession(String sessionId, Member member) {
        Optional<WorkoutSession> sessionOpt = sessions.stream()
                .filter(session -> session.getSessionID().equals(sessionId))
                .findFirst();

        if (!sessionOpt.isPresent()) {
            LoggerUtils.logError("Session not found: " + sessionId);
            return false;
        }

        WorkoutSession session = sessionOpt.get();

        // Check session capacity
        if (session.getCurrentOccupancy() >= session.getMaxCapacity()) {
            LoggerUtils.logError("Session is full: " + sessionId);
            return false;
        }

        // Check trainer and room availability using DatabaseHandler
        boolean trainerAvailable = DatabaseHandler.checkAvailability(
                session.getTrainer().getId(), null,
                session.getDateTime().toLocalDate(),
                session.getDateTime().toLocalTime());

        boolean roomAvailable = DatabaseHandler.checkAvailability(
                null, session.getRoom().getId(),
                session.getDateTime().toLocalDate(),
                session.getDateTime().toLocalTime());

        if (!trainerAvailable) {
            LoggerUtils.logError("Trainer not available for session: " + sessionId);
            return false;
        }

        if (!roomAvailable) {
            LoggerUtils.logError("Room not available for session: " + sessionId);
            return false;
        }

        // Create reservation
        String reservationId = "RES" + System.currentTimeMillis();
        Reservation reservation = Reservation.createReservation(reservationId, member, session);
        if (reservation == null) {
            LoggerUtils.logError("Failed to create reservation for session: " + sessionId);
            return false;
        }

        // Save reservation to database
        try (Connection conn = dbHandler.connect()) {
            String query = "INSERT INTO reservations (id, member_id, session_id, reservation_time) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, reservationId);
                stmt.setInt(2, member.getId());
                stmt.setString(3, sessionId);
                stmt.setTimestamp(4, java.sql.Timestamp.valueOf(reservation.getReservationTimestamp()));
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    member.reserveSession(session);
                    LoggerUtils.logSuccess("Session booked successfully for " + member.getName() + ", Session ID: " + sessionId);
                    NotificationService.sendBookingConfirmation(
                            member.getUsername(),
                            session.getExerciseType() + " on " + session.getDateTime());
                    return true;
                } else {
                    LoggerUtils.logError("Failed to save reservation to database for session: " + sessionId);
                    return false;
                }
            }
        } catch (SQLException e) {
            LoggerUtils.logError("Database error while booking session: " + e.getMessage());
            return false;
        }
    }

    public boolean cancelSession(String sessionId, Member member) {
        Optional<WorkoutSession> sessionOpt = sessions.stream()
                .filter(session -> session.getSessionID().equals(sessionId))
                .findFirst();

        if (!sessionOpt.isPresent()) {
            LoggerUtils.logError("Session not found: " + sessionId);
            return false;
        }

        WorkoutSession session = sessionOpt.get();
        Reservation reservation = new Reservation("RES" + System.currentTimeMillis(), member, session);

        if (!reservation.cancelReservation()) {
            LoggerUtils.logError("Failed to cancel reservation for session: " + sessionId);
            return false;
        }

        // Remove reservation from database
        try (Connection conn = dbHandler.connect()) {
            String query = "DELETE FROM reservations WHERE session_id = ? AND member_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, sessionId);
                stmt.setInt(2, member.getId());
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    member.cancelReservation(session);
                    LoggerUtils.logSuccess("Session cancelled successfully for " + member.getName() + ", Session ID: " + sessionId);
                    NotificationService.sendCancellationNotification(
                            member.getUsername(),
                            session.getExerciseType() + " on " + session.getDateTime());
                    return true;
                } else {
                    LoggerUtils.logError("Reservation not found in database for session: " + sessionId);
                    return false;
                }
            }
        } catch (SQLException e) {
            LoggerUtils.logError("Database error while cancelling session: " + e.getMessage());
            return false;
        }
    }

    public boolean isRoomAvailable(Room room, LocalDateTime dateTime) {
        return DatabaseHandler.checkAvailability(
                null, room.getId(),
                dateTime.toLocalDate(),
                dateTime.toLocalTime());
    }

    public boolean isTrainerAvailable(Trainer trainer, LocalDateTime dateTime) {
        return DatabaseHandler.checkAvailability(
                trainer.getId(), null,
                dateTime.toLocalDate(),
                dateTime.toLocalTime());
    }

    public List<WorkoutSession> getAvailableSessions() {
        return sessions.stream()
                .filter(session -> session.getCurrentOccupancy() < session.getMaxCapacity())
                .toList();
    }
}