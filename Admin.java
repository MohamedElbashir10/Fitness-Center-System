public class Admin extends User {
    public Admin(String name, String username, String password) {
        super(name, username, password, "Admin");
    }

    public Trainer createTrainerAccount(String name, String username, String password) {
        Trainer trainer = new Trainer(name, username, password);
        AuthService authService = new AuthService(new DatabaseHandler());
        if (authService.registerUser(trainer)) {
            return trainer;
        }
        return null;
    }

    public boolean removeTrainerAccount(String username) {
        try (java.sql.Connection conn = new DatabaseHandler().connect()) {
            String query = "DELETE FROM users WHERE username = ? AND role = 'Trainer'";
            try (java.sql.PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (java.sql.SQLException e) {
            LoggerUtils.logError("Error removing trainer: " + e.getMessage());
        }
        return false;
    }
}