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
            System.out.println("[ERROR] Member has already reserved this session.");
            return null;
        }

        if (session.getCurrentOccupancy() >= session.getMaxCapacity()) {
            System.out.println("[ERROR] Cannot reserve: session is full.");
            return null;
        }

        boolean added = session.addParticipant(member);
        if (added) {
            System.out.println("[SUCCESS] Reservation created for session: " + session.getSessionID());
            return new Reservation(reservationID, member, session);
        } else {
            System.out.println("[ERROR] Unknown error while adding participant.");
            return null;
        }
    }


    public boolean cancelReservation() {
        boolean removed = session.removeParticipant(member);

        if (removed) {
            System.out.println("[SUCCESS] Reservation cancelled for member: " + member.getUsername()); // fix the getters
            return true;
        } else {
            System.out.println("[ERROR] Member was not enrolled in this session.");
            return false;
        }

    }

    public void displayReservationDetails() {
        System.out.println("=== Reservation Details ===");
        System.out.println("Reservation ID: " + reservationID);
        System.out.println("Member: " + member.getUsername()); // fix the getters
        System.out.println("Session: " + session.getExerciseType() + " (" + session.getSessionID() + ")");
        System.out.println("Scheduled Time: " + session.getDateTime());
        System.out.println("Room: " + session.getRoom().getName());
        System.out.println("Trainer: " + session.getTrainer().getUsername()); // fix the getters
        System.out.println("Reservation Time: " + reservationTimestamp);
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