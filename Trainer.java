import java.util.ArrayList;
import java.util.List;

public class Trainer extends User {

    // Sessions assigned to this trainer
    private List<WorkoutSession> assignedSessions;
    private List<Availability> availabilities;
    private List<Availability> availabilitySlots; // Declare availabilitySlots

    // Constructor
    public Trainer( String name, String username, String password) {
        super(name, username, password, "Trainer");
        this.assignedSessions = new ArrayList<>();
        this.availabilities = new ArrayList<>();
        this.availabilitySlots = new ArrayList<>(); // Initialize availabilitySlots
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

    // Assigning a session method
    public void assignSession(WorkoutSession session) {
        assignedSessions.add(session);
    }

    // Adding availability method
    public void addAvailability(Availability availability) {
        availabilities.add(availability);
    }
}