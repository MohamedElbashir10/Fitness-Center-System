import java.time.LocalDateTime;

public class Reservation {

    private String reservationID;
    private Member member; // Only members can make a reservation
    private WorkoutSession session;
    private LocalDateTime reservationTimestamp;

    Reservation(String reservationID, Member member, WorkoutSession session) {
        this.reservationID = reservationID;
        this.member = member;
        this.session = session;
        this.reservationTimestamp = LocalDateTime.now();
    }

    public static Reservation createReservation(String reservationID, Member member, WorkoutSession session) {
        if (session.getParticipants().contains(member)) {
            LoggerUtils.logError("Member has already reserved this session.");
            return null;
        }

        if (session.getCurrentOccupancy() >= session.getMaxCapacity()) {
            LoggerUtils.logError("Cannot reserve: session is full.");
            return null;
        }

        boolean added = session.addParticipant(member);
        if (added) {
            LoggerUtils.logSuccess("Reservation created for session: " + session.getSessionID());
            return new Reservation(reservationID, member, session);
        } else {
            LoggerUtils.logError("Unknown error while adding participant.");
            return null;
        }
    }

    public boolean cancelReservation() {
        boolean removed = session.removeParticipant(member);

        if (removed) {
            LoggerUtils.logSuccess("Reservation cancelled for member: " + member.getUsername());
            return true;
        } else {
            LoggerUtils.logError("Member was not enrolled in this session.");
            return false;
        }
    }

    public void displayReservationDetails() {
        LoggerUtils.logSection("Reservation Details");
        LoggerUtils.logInfo("Reservation ID: " + reservationID);
        LoggerUtils.logInfo("Member: " + member.getUsername());
        LoggerUtils.logInfo("Session: " + session.getExerciseType() + " (" + session.getSessionID() + ")");
        LoggerUtils.logInfo("Scheduled Time: " + session.getDateTime());
        LoggerUtils.logInfo("Room: " + session.getRoom().getName());
        LoggerUtils.logInfo("Trainer: " + session.getTrainer().getUsername());
        LoggerUtils.logInfo("Reservation Time: " + reservationTimestamp);
    }

    // Getters
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
