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
            if (existing.getRoom() != null && room != null
                    && existing.getRoom().getId() == room.getId()
                    && existing.getDateTime().equals(session.getDateTime())) {
                LoggerUtils.logError("Room is already booked for another session at this time.");
                return false;
            }

            if (existing.getTrainer() != null && trainer != null
                    && existing.getTrainer().getId() == (trainer.getId())
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
                    + " | Room: " + (session.getRoom() != null ? session.getRoom().getName() : "N/A")
                    + " | Trainer: " + (session.getTrainer() != null ? session.getTrainer().getUsername() : "N/A"));
        }
    }

    public List<WorkoutSession> getScheduledSessions() {
        return scheduledSessions;
    }

    public void removeWorkoutSession(WorkoutSession session) {
        scheduledSessions.remove(session);
    }

    public List<WorkoutSession> getSessionsForTrainer(Trainer trainer) {
        List<WorkoutSession> result = new ArrayList<>();
        for (WorkoutSession session : scheduledSessions) {
            if (session.getTrainer() != null && session.getTrainer().getId() == (trainer.getId())) {
                result.add(session);
            }
        }
        return result;
    }
}

