import java.util.ArrayList;
import java.util.List;

public class Member extends User {
    private List<WorkoutSession> reservedSessions;

    public Member(String name, String username, String password) {
        super(name, username, password, "Member");
        this.reservedSessions = new ArrayList<>();
    }

    public void reserveSession(WorkoutSession session) {
        reservedSessions.add(session);
    }

    public boolean cancelReservation(WorkoutSession session) {
        return reservedSessions.remove(session);
    }

    public List<WorkoutSession> getReservedSessions() {
        return reservedSessions;
    }

    public String viewSessions(Schedule schedule) {
        return schedule.getFormattedSchedule();
    }

    public String checkSchedule(Schedule schedule) {
        return schedule.getFormattedSchedule();
    }
}