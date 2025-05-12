public class User {
    private int id;
    private String name;
    private String username;
    private String password;
    private String role;

    // Constructor for registration (no ID, as it's auto-incremented)
    public User(String name, String username, String password, String role) {
        this.id = 0; // ID will be set after database insert
        this.name = name;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Constructor for authentication (includes ID from database)
    public User(int id, String name, String username, String password, String role) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    // Setter for ID (used after database insert)
    public void setId(int id) {
        this.id = id;
    }
}