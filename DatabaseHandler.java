import java.sql.*;

public class DatabaseHandler {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3307/fcs_db";
        String user = "root";
        String password = "rootpassword";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("Connected to the database!");

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM users");

            while (rs.next()) {
                System.out.println(rs.getString("name") + " (" + rs.getString("role") + ")");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
