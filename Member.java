import java.util.ArrayList;
import java.util.List;

public class Member extends User {
    
    // List of sessions the member has reserved
    private List<WorkoutSession> reservedSessions; 

    // Construtor
    public Member(int id, String name, String username, String password) {
        super(id, name, username, password, "Member");
        this.reservedSessions = new ArrayList<>();
    }

    // Reserving a session method
    public void reserveSession(WorkoutSession session) {
        reservedSessions.add(session);
    }

    // ReservedSession by the member Getter
    public List<WorkoutSession> getReservedSessions() {
        return reservedSessions;
    }

}
