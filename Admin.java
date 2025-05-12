// Admin.java
public class Admin extends User {

    // Constructor
    public Admin(int id, String name, String username, String password) {
        super(id, name, username, password, "Admin");
    }

    public Trainer createTrainerAccount(int id, String name, String username, String password) {
        return new Trainer(id, name, username, password);
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