import java.util.List;
import java.util.Scanner;

public class SessionBookingCoordinator {

    private List<Trainer> trainers;
    private List<Room> rooms;
    private List<WorkoutSession> sessions;
    private Availability availability;
    private Reservation reservationService;

    public SessionBookingCoordinator(List<Trainer> trainers, List<Room> rooms, List<WorkoutSession> sessions, Availability availability, Reservation reservationService) {
        
        this.trainers = trainers;
        this.rooms = rooms;
        this.sessions = sessions;
        this.availability = availability;
        this.reservationService = reservationService;
    }

    
}
