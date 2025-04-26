import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class WorkoutSession {
    private String sessionID;
    private String exerciseType;
    private LocalDateTime dateTime;
    private int maxCapacity;
    private Room room;
    private Trainer trainer;
    private List<Member> participants;

    public WorkoutSession(String sessionID, String exerciseType, LocalDateTime dateTime, int maxCapacity, Room room, Trainer trainer) {
        this.sessionID = sessionID;
        this.exerciseType = exerciseType;
        this.dateTime = dateTime;
        this.maxCapacity = maxCapacity;
        this.room = room;
        this.trainer = trainer;
        this.participants = new ArrayList<>();
    }

   
    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionID) {
        this.sessionID = sessionID;
    }

    public String getExerciseType() {
        return exerciseType;
    }

    public void setExerciseType(String exerciseType) {
        this.exerciseType = exerciseType;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public int getMaxCapacity() {
        return maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public Trainer getTrainer() {
        return trainer;
    }

    public void setTrainer(Trainer trainer) {
        this.trainer = trainer;
    }

    public List<Member> getParticipants() {
        return participants;
    }

    public boolean addParticipant(Member member) {
        if (participants.size() < maxCapacity) {
            participants.add(member);
            return true;
        }
        return false;
    }

    public boolean removeParticipant(Member member) {
        return participants.remove(member);
    }

    public int getCurrentOccupancy() {
        return participants.size();
    }
}
