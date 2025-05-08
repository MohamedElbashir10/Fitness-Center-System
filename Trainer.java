import java.util.ArrayList;
import java.util.List;

public class Trainer extends User {
    
    // Sessions assigned to this trainer
    private List<WorkoutSession> assignedSessions; 

    // Trainer's available time slots
    private List<Availability> availabilitySlots;  

    // Constructor
    public Trainer(String id, String name, String username, String password) {
        super(id, name, username, password, "Trainer");
        this.assignedSessions = new ArrayList<>();
        this.availabilitySlots = new ArrayList<>();
    }

    // Adding an Available time slot method
    public void addAvailabilitySlot(Availability slot) {
        availabilitySlots.add(slot);
    }

    // Listing Available time slots method
    public List<Availability> getAvailabilitySlots() {
        return availabilitySlots;
    }

    // Listing Assigned Sessions method
    public List<WorkoutSession> getAssignedSessions() {
        return assignedSessions;
    }

    // Assiging a session method
    public void assignSession(WorkoutSession session) {
        assignedSessions.add(session);
    }
}
