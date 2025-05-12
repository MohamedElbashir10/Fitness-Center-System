import java.time.LocalDateTime;

public class Reservation {
    private String reservationID;
    private Member member;
    private WorkoutSession session;
    private LocalDateTime reservationTimestamp;

    public Reservation(String reservationID, Member member, WorkoutSession session) {
        this.reservationID = reservationID;
        this.member = member;
        this.session = session;
        this.reservationTimestamp = LocalDateTime.now();
    }

    public static Reservation createReservation(String reservationID, Member member, WorkoutSession session) {
        if (session.getParticipants().contains(member)) {
            LoggerUtils.logError("Member already reserved this session.");
            return null;
        }

        if (session.getCurrentOccupancy() >= session.getMaxCapacity()) {
            LoggerUtils.logError("Session is full.");
            return null;
        }

        boolean trainerAvailable = DatabaseHandler.checkAvailability(
            session.getTrainer().getId(), null,
            session.getDateTime().toLocalDate(),
            session.getDateTime().toLocalTime());

        boolean roomAvailable = DatabaseHandler.checkAvailability(
            null, session.getRoom().getId(),
            session.getDateTime().toLocalDate(),
            session.getDateTime().toLocalTime());

        if (!trainerAvailable) {
            LoggerUtils.logError("Trainer not available at this time.");
            return null;
        }

        if (!roomAvailable) {
            LoggerUtils.logError("Room not available at this time.");
            return null;
        }

        if (session.addParticipant(member)) {
            LoggerUtils.logSuccess("Reservation successful.");
            return new Reservation(reservationID, member, session);
        } else {
            LoggerUtils.logError("Failed to add participant.");
            return null;
        }
    }

    public boolean cancelReservation() {
        if (session.removeParticipant(member)) {
            LoggerUtils.logSuccess("Reservation cancelled.");
            return true;
        } else {
            LoggerUtils.logError("Member was not enrolled.");
            return false;
        }
    }

    public void displayReservationDetails() {
        LoggerUtils.logSection("Reservation Details");
        LoggerUtils.logInfo("Reservation ID: " + reservationID);
        LoggerUtils.logInfo("Member: " + member.getUsername());
        LoggerUtils.logInfo("Session ID: " + session.getSessionID());
        LoggerUtils.logInfo("Time: " + session.getDateTime());
    }

    public String getReservationID() {
        return reservationID;
    }

    public Member getMember() {
        return member;
    }

    public WorkoutSession getSession() {
        return session;
    }

    public LocalDateTime getReservationTimestamp() {
        return reservationTimestamp;
    }
}
