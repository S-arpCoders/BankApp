package com.Scoders.BankingApp.database;

import com.Scoders.BankingApp.model.Account;
import com.Scoders.BankingApp.model.transaction;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TransactionDatabase {

    private static final String DATABASE_URL = "jdbc:sqlite:bank.db"; // The SQLite database file

    // Method to create the Transaction table
    public static void createTransactionTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS Transactions ("
                + "transId INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "amount DOUBLE, "
                + "dateTime TEXT, "
                + "accNo INTEGER, "
                + "transactionType TEXT, "
                + "FOREIGN KEY(accNo) REFERENCES Account(accNo))";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
            System.out.println("Transaction table created or already exists.");
        } catch (SQLException e) {
            System.out.println("Error creating table: " + e.getMessage());
        }
    }

    // Method to insert a transaction into the table
    public static void insertTransaction(Long accNo, Double amount, String transactionType) {
        String insertSQL = "INSERT INTO Transactions (amount, dateTime, accNo, transactionType) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setDouble(1, amount);
            pstmt.setString(2, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)); // current timestamp
            pstmt.setLong(3, accNo);
            pstmt.setString(4, transactionType);
            pstmt.executeUpdate();
            System.out.println("Transaction inserted successfully.");
        } catch (SQLException e) {
            System.out.println("Error inserting transaction: " + e.getMessage());
        }
    }

    // Method to get a transaction by transaction ID
    public static transaction getTransactionByTransId(Long transId) {
        String selectSQL = "SELECT * FROM Transactions WHERE transId = ?";
        transaction transaction = null;

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
            pstmt.setLong(1, transId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Long id = rs.getLong("transId");
                Double amount = rs.getDouble("amount");
                String dateTime = rs.getString("dateTime");
                Long accNo = rs.getLong("accNo");
                String transactionType = rs.getString("transactionType");

                transaction = new transaction();
                transaction.setTransId(id);
                transaction.setAmount(amount);
                transaction.setDateTime(LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_DATE_TIME));
                transaction.setAccount(new Account()); // Assuming Account constructor takes accNo
                transaction.setTransactionType(transactionType);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving transaction: " + e.getMessage());
        }

        return transaction;
    }

    // Method to get all transactions for a specific account
    public static List<transaction> getTransactionsByAccount(Account account) {
        String selectSQL = "SELECT * FROM Transactions WHERE accNo = ?";
        List<transaction> transactions = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
            pstmt.setLong(1, account.getAccNo());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Long id = rs.getLong("transId");
                Double amount = rs.getDouble("amount");
                String dateTime = rs.getString("dateTime");
                String transactionType = rs.getString("transactionType");

                transaction transact = new transaction();
                transact.setTransId(id);
                transact.setAccount(account);
                transact.setTransactionType(transactionType);
                transact.setAmount(amount);
                transact.setDateTime(LocalDateTime.parse(dateTime));
                transactions.add(transact);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving transactions: " + e.getMessage());
        }

        return transactions;
    }

    // Method to update a transaction (just an example, may not be used often)
    public static void updateTransaction(Long transId, Double newAmount, String newTransactionType) {
        String updateSQL = "UPDATE Transactions SET amount = ?, transactionType = ? WHERE transId = ?";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
            pstmt.setDouble(1, newAmount);
            pstmt.setString(2, newTransactionType);
            pstmt.setLong(3, transId);
            pstmt.executeUpdate();
            System.out.println("Transaction updated successfully.");
        } catch (SQLException e) {
            System.out.println("Error updating transaction: " + e.getMessage());
        }
    }

    // Method to delete a transaction
    public static void deleteTransaction(Long transId) {
        String deleteSQL = "DELETE FROM Transactions WHERE transId = ?";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement pstmt = conn.prepareStatement(deleteSQL)) {
            pstmt.setLong(1, transId);
            pstmt.executeUpdate();
            System.out.println("Transaction deleted successfully.");
        } catch (SQLException e) {
            System.out.println("Error deleting transaction: " + e.getMessage());
        }
    }

}
