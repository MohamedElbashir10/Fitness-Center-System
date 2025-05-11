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
            LoggerUtils.logError("Trainer is not available at the selected time.");
            return false;
        }

        for (WorkoutSession existing : scheduledSessions) {
            if (existing.getRoom().getId() == room.getId()
                    && existing.getDateTime().equals(session.getDateTime())) {
                LoggerUtils.logError("Room is already booked for another session at this time.");
                return false;
            }

            if (existing.getTrainer().getId() == trainer.getId()
                    && existing.getDateTime().equals(session.getDateTime())) {
                LoggerUtils.logError("Trainer already has another session at this time.");
                return false;
            }
        }

        session.setRoom(room);
        session.setTrainer(trainer);
        trainer.assignSession(session);
        scheduledSessions.add(session);

        LoggerUtils.logSuccess("Workout session scheduled by admin: " + admin.getUsername());
        return true;
    }

    public void displaySchedule() {
        LoggerUtils.logSection("Scheduled Workout Sessions");
        for (WorkoutSession session : scheduledSessions) {
            LoggerUtils.logInfo("Session ID: " + session.getSessionID()
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
