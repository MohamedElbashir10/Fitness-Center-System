import java.util.ArrayList;
import java.util.List;

public class Trainer extends User {
    
    // Sessions assigned to this trainer
    private List<WorkoutSession> assignedSessions; 

    // Trainer's available time slots
    private List<Availability> availabilitySlots;  

    // Constructor
    public Trainer(int id, String name, String username, String password) {
        super(id, name, username, password, "Trainer");
        this.assignedSessions = new ArrayList<>();
        this.availabilitySlots = new ArrayList<>();
    }


}
