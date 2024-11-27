package com.Scoders.BankingApp.database;

import com.Scoders.BankingApp.model.Account;
import com.Scoders.BankingApp.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.Scoders.BankingApp.database.UserDatabase.getUserById;

public class AccountDatabase {

    private static final String DATABASE_URL = "jdbc:sqlite:bank.db"; // The SQLite database file

    // Method to create the Account table
    public static void createAccountTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS Account ("
                + "accNo INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "user_id INTEGER, "
                + "balance DOUBLE, "
                + "FOREIGN KEY(user_id) REFERENCES User(id))";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
            System.out.println("Account table created or already exists.");
        } catch (SQLException e) {
            System.out.println("Error creating table: " + e.getMessage());
        }
    }

    // Method to insert an account into the table
    public static void insertAccount(Long userId, Double balance) {
        String insertSQL = "INSERT INTO Account (user_id, balance) VALUES (?, ?)";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setLong(1, userId);
            pstmt.setDouble(2, balance);
            pstmt.executeUpdate();
            System.out.println("Account inserted successfully.");
        } catch (SQLException e) {
            System.out.println("Error inserting account: " + e.getMessage());
        }
    }

    // Method to get an account by account number
    public static Account getAccountByAccNo(Long accNo) {
        String selectSQL = "SELECT * FROM Account WHERE accNo = ?";
        Account account = null;

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
            pstmt.setLong(1, accNo);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Long id = rs.getLong("accNo");
                Long userId = rs.getLong("user_id");
                Double balance = rs.getDouble("balance");

                account = new Account();
                account.setAccNo(id);
                account.setBalance(balance);

                // Now fetch the user from the database based on userId
                User user = getUserById(userId); // Call the method to get User by ID
                account.setUser(user); // Set the full User object

            }
        } catch (SQLException e) {
            System.out.println("Error retrieving account: " + e.getMessage());
        }

        return account;
    }


    // Method to update the balance of an account
    public static void updateBalance(Long accNo, Double newBalance) {
        String updateSQL = "UPDATE Account SET balance = ? WHERE accNo = ?";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
            pstmt.setDouble(1, newBalance);
            pstmt.setLong(2, accNo);
            pstmt.executeUpdate();
            System.out.println("Account balance updated successfully.");
        } catch (SQLException e) {
            System.out.println("Error updating balance: " + e.getMessage());
        }
    }

    // Method to delete an account
    public static void deleteAccount(Long accNo) {
        String deleteSQL = "DELETE FROM Account WHERE accNo = ?";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement pstmt = conn.prepareStatement(deleteSQL)) {
            pstmt.setLong(1, accNo);
            pstmt.executeUpdate();
            System.out.println("Account deleted successfully.");
        } catch (SQLException e) {
            System.out.println("Error deleting account: " + e.getMessage());
        }
    }

    public static List<Account> getAllAccounts() {
        List<Account> accounts = new ArrayList<>();
        String selectSQL = "SELECT accNo, user_id, balance FROM Account";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             Statement pstmt = conn.createStatement();
             ResultSet rs = pstmt.executeQuery(selectSQL)) {

            while (rs.next()) {
                Long accNo = rs.getLong("accNo");
                Long userId = rs.getLong("user_id");
                Double balance = rs.getDouble("balance");

                Account account = new Account();
                account.setAccNo(accNo);
                account.setBalance(balance);

                // Fetch user details and set it in the account
                User user = getUserById(userId);
                account.setUser(user);

                accounts.add(account);
            }
        } catch (SQLException e) {
            System.out.println("Error fetching accounts: " + e.getMessage());
        }

        return accounts;
    }
}
