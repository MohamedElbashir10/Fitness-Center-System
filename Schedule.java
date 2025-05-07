import java.util.ArrayList;
import java.util.List;

public class Schedule {

    private List<WorkoutSession> scheduledSessions;

    public Schedule() {
        this.scheduledSessions = new ArrayList<>();
    }


    public boolean scheduleWorkout(Admin admin, WorkoutSession session, Trainer trainer, Room room) {
        boolean isTrainerAvailable = trainer.getAvailabilitySlots().stream()
                .anyMatch(slot -> slot.getDate().equals(session.getDateTime().toLocalDate())
                        && slot.getStartTime().isBefore(session.getDateTime().toLocalTime())
                        && slot.getEndTime().isAfter(session.getDateTime().toLocalTime()));

        if (!isTrainerAvailable) {
            System.out.println("[ERROR] Trainer is not available at the selected time.");
            return false;
        }

        
        for (WorkoutSession existing : scheduledSessions) {
            if (existing.getRoom().getId() == room.getId()
                    && existing.getDateTime().equals(session.getDateTime())) {
                System.out.println("[ERROR] Room is already booked for another session at this time.");
                return false;
            }

            if (existing.getTrainer().getId() == trainer.getId()
                    && existing.getDateTime().equals(session.getDateTime())) {
                System.out.println("[ERROR] Trainer already has another session at this time.");
                return false;
            }
        }

        
        session.setRoom(room);
        session.setTrainer(trainer);

        
        trainer.assignSession(session);

        
        scheduledSessions.add(session);

        System.out.println("[SUCCESS] Workout session scheduled by admin: " + admin.getUsername());
        return true;
    }

    
    public void displaySchedule() {
        System.out.println("\n=== Scheduled Workout Sessions ===");
        for (WorkoutSession session : scheduledSessions) {
            System.out.println("Session ID: " + session.getSessionID()
                    + " | Exercise: " + session.getExerciseType()
                    + " | Date: " + session.getDateTime()
                    + " | Room: " + session.getRoom().getName()
                    + " | Trainer: " + session.getTrainer().getUsername());
        }
    }

    public List<WorkoutSession> getScheduledSessions() {
        return scheduledSessions;
    }
}
