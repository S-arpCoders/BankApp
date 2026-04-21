package com.Scoders.BankingApp.database;

import com.Scoders.BankingApp.model.User;
import com.Scoders.BankingApp.security.PasswordEncoder;

import java.sql.*;

public class UserDatabase {

    private static final String DATABASE_URL = "jdbc:sqlite:bank.db";

    public static void createUserTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS User ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "username TEXT, "
                + "surname TEXT, "
                + "password TEXT, "
                + "sessionId TEXT)";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
            System.out.println("User table created or already exists.");
            ensureSessionIdColumnExists(conn);
        } catch (SQLException e) {
            System.out.println("Error creating table: " + e.getMessage());
        }
    }

    private static void ensureSessionIdColumnExists(Connection conn) {
        try (Statement stmt = conn.createStatement()) {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet columns = metaData.getColumns(null, null, "User", "sessionId");
            
            if (!columns.next()) {
                stmt.execute("ALTER TABLE User ADD COLUMN sessionId TEXT");
                System.out.println("sessionId column added to User table.");
            } else {
                System.out.println("sessionId column already exists.");
            }
        } catch (SQLException e) {
            System.out.println("sessionId column may already exist: " + e.getMessage());
        }
    }

    public static void insertUser(String username, String surname, String password) {
        String insertSQL = "INSERT INTO User (username, surname, password) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            String encodedPassword = PasswordEncoder.encode(password);
            pstmt.setString(1, username);
            pstmt.setString(2, surname);
            pstmt.setString(3, encodedPassword);
            pstmt.executeUpdate();
            System.out.println("User inserted successfully.");
        } catch (SQLException e) {
            System.out.println("Error inserting user: " + e.getMessage());
        }
    }

    // Method to get a user by ID

    public static User getUserById(Long id) {
        String selectSQL = "SELECT * FROM User WHERE id = ?";
        User user = null;

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Long userId = rs.getLong("id");
                String username = rs.getString("username");
                String surname = rs.getString("surname");
                String password = rs.getString("password");
                String sessionId = getSafeString(rs, "sessionId");

                user = new User();
                user.setId(userId);
                user.setUsername(username);
                user.setSurname(surname);
                user.setPassword(password);
                user.setSessionId(sessionId);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving user: " + e.getMessage());
        }

        return user;
    }



    public static User getUserByUsername(String username) {
        String selectSQL = "SELECT * FROM User WHERE username = ?";
        User user = null;

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Long userId = rs.getLong("id");
                String surname = rs.getString("surname");
                String password = rs.getString("password");
                String sessionId = getSafeString(rs, "sessionId");

                user = new User();
                user.setId(userId);
                user.setUsername(username);
                user.setSurname(surname);
                user.setPassword(password);
                user.setSessionId(sessionId);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving user: " + e.getMessage());
        }

        return user;
    }


    public static void updateUser(Long id, String username, String surname, String password) {
        String updateSQL = "UPDATE User SET username = ?, surname = ?, password = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
            pstmt.setString(1, username);
            pstmt.setString(2, surname);
            pstmt.setString(3, password);
            pstmt.setLong(4, id);
            pstmt.executeUpdate();
            System.out.println("User updated successfully.");
        } catch (SQLException e) {
            System.out.println("Error updating user: " + e.getMessage());
        }
    }

    public static void deleteUser(Long id) {
        String deleteSQL = "DELETE FROM User WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement pstmt = conn.prepareStatement(deleteSQL)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
            System.out.println("User deleted successfully.");
        } catch (SQLException e) {
            System.out.println("Error deleting user: " + e.getMessage());
        }
    }

    public static void updateUserSessionId(Long id, String sessionId) {
        String updateSQL = "UPDATE User SET sessionId = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
            pstmt.setString(1, sessionId);
            pstmt.setLong(2, id);
            pstmt.executeUpdate();
            System.out.println("User sessionId updated successfully.");
        } catch (SQLException e) {
            System.out.println("Error updating user sessionId: " + e.getMessage());
        }
    }

    private static String getSafeString(ResultSet rs, String columnName) {
        try {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            
            for (int i = 1; i <= columnCount; i++) {
                if (metaData.getColumnName(i).equalsIgnoreCase(columnName)) {
                    return rs.getString(columnName);
                }
            }
            return null;
        } catch (SQLException e) {
            System.out.println("Error getting column " + columnName + ": " + e.getMessage());
            return null;
        }
    }

}
