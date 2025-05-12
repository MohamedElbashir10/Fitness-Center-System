// Admin.java
public class Admin extends User {

    // Constructor
    public Admin(String name, String username, String password) {
        super(name, username, password, "Admin");
    }

    public Trainer createTrainerAccount(String name, String username, String password) {
        return new Trainer(name, username, password);
    }

    public boolean scheduleWorkout(WorkoutSession session, Trainer trainer, Room room) {
        if (room.isAvailable(session.getDateTime()) && trainer != null) {
            session.setTrainer(trainer);
            session.setRoom(room);
            trainer.assignSession(session);
            room.bookRoom(session.getDateTime());
            return true;
        }
        return false;
    }
}