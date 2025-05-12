import java.time.format.DateTimeFormatter;
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

    public String getFormattedSchedule() {
        if (scheduledSessions.isEmpty()) {
            return "No sessions scheduled.";
        }

        StringBuilder scheduleBuilder = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        for (WorkoutSession session : scheduledSessions) {
            scheduleBuilder.append("Session ID: ").append(session.getSessionID())
                    .append("\nExercise: ").append(session.getExerciseType())
                    .append("\nDate & Time: ").append(session.getDateTime().format(formatter))
                    .append("\nRoom: ").append(session.getRoom().getName())
                    .append("\nTrainer: ").append(session.getTrainer().getUsername())
                    .append("\n\n");
        }

        return scheduleBuilder.toString();
    }

    public List<WorkoutSession> getScheduledSessions() {
        return scheduledSessions;
    }
}