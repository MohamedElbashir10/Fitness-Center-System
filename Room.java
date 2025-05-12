import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

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

    public boolean isAvailable(LocalDateTime startTime) {
        LocalDateTime endTime = startTime.plusHours(1); // Assume 1-hour sessions
        for (LocalDateTime booked : bookedTimes) {
            LocalDateTime bookedEnd = booked.plusHours(1);
            // Check for overlap: session starts before booked ends and ends after booked starts
            if (startTime.isBefore(bookedEnd) && endTime.isAfter(booked)) {
                LoggerUtils.logInfo("Room " + name + " is booked from " + booked + " to " + bookedEnd);
                return false;
            }
        }
        return true;
    }

    public void bookRoom(LocalDateTime dateTime) {
        bookedTimes.add(dateTime);
    }

    public void clearBookings() {
        bookedTimes.clear();
    }

    public String getBookedTimes() {
        if (bookedTimes.isEmpty()) {
            return "No bookings for room: " + name;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return "Booked times for room " + name + ":\n" +
                bookedTimes.stream()
                        .map(time -> time.format(formatter) + " to " + time.plusHours(1).format(formatter))
                        .collect(Collectors.joining("\n"));
    }

    public void addRoom() {
        LoggerUtils.logInfo("Room " + name + " has been added to the system.");
        // Additional logic for adding a room to a database or system can be implemented here
    }

    public void updateRoom(String newName, int newCapacity, String newDescription) {
        LoggerUtils.logInfo("Updating room " + name + "...");
        this.name = newName;
        this.capacity = newCapacity;
        this.description = newDescription;
        LoggerUtils.logInfo("Room updated successfully: " + getRoomDetails());
    }

    public void deleteRoom() {
        LoggerUtils.logInfo("Room " + name + " has been deleted from the system.");
        // Additional logic for removing a room from a database or system can be implemented here
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