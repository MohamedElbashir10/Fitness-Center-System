public class Room {

    private String name;
    private int roomId;
    private int capacity;
    private String description;

    public Room(String name,int roomId, int capacity, String description){
        this.name = name;
        this.roomId = roomId;
        this.capacity = capacity;
        this.description = description;
    }

    public void addRoom(){

    }

    public void updateRoom(){

    }

    public void deleteRoom(){

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
