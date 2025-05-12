// Room.java
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class Room {
    private String name;
    private int id;
    private int capacity;
    private int roomId;
    private String description;
    private Set<LocalDateTime> bookedTimes = new HashSet<>();

    public Room(String name, int id, int capacity, String description) {
        this.name = name;
        this.id = id;
        this.capacity = capacity;
        this.roomId = id;
        this.description = description;
    }

    public boolean isAvailable(LocalDateTime dateTime) {
        return !bookedTimes.contains(dateTime);
    }

    public void bookRoom(LocalDateTime dateTime) {
        bookedTimes.add(dateTime);
    }

    public void addRoom() {
        // Implementation for adding a room
    }

    public void updateRoom() {
        // Implementation for updating a room
    }

    public void deleteRoom() {
        // Implementation for deleting a room
    }

    public String getRoomDetails() {
        return "ID: " + roomId + " | Name: " + name + " | Capacity: " + capacity + " | Description: " + description;
    }

    // Getters
    public int getId() {
        return roomId;
    }

    public String getName() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getDescription() {
        return description;
    }
}