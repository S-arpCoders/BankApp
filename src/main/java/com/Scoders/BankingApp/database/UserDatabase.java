package com.Scoders.BankingApp.database;

import com.Scoders.BankingApp.model.User;

import java.sql.*;

public class UserDatabase {

    private static final String DATABASE_URL = "jdbc:sqlite:bank.db";

    public static void createUserTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS User ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "username TEXT, "
                + "surname TEXT, "
                + "password TEXT)";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
            System.out.println("User table created or already exists.");
        } catch (SQLException e) {
            System.out.println("Error creating table: " + e.getMessage());
        }
    }

    public static void insertUser(String username, String surname, String password) {
        String insertSQL = "INSERT INTO User (username, surname, password) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setString(1, username);
            pstmt.setString(2, surname);
            pstmt.setString(3, password);
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

                user = new User();
                user.setId(userId);
                user.setUsername(username);
                user.setSurname(surname);
                user.setPassword(password);
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

                user = new User();
                user.setId(userId);
                user.setUsername(username);
                user.setSurname(surname);
                user.setPassword(password);
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

}
