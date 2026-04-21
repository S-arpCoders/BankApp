package com.Scoders.BankingApp.database;

import com.Scoders.BankingApp.model.Account;
import com.Scoders.BankingApp.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.Scoders.BankingApp.database.UserDatabase.getUserById;

public class AccountDatabase {

    private static final String DATABASE_URL = "jdbc:sqlite:bank.db"; // The SQLite database file

    public static final String TYPE_SAVINGS = "Savings";
    public static final String TYPE_CURRENT = "Current";
    public static final String TYPE_STOCK = "Stock";

    // Method to create the Account table
    public static void createAccountTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS Account ("
                + "accNo TEXT PRIMARY KEY CHECK(length(accNo) = 8), "
                + "user_id INTEGER, "
                + "balance DOUBLE, "
                + "accountType TEXT DEFAULT 'Savings', "
                + "FOREIGN KEY(user_id) REFERENCES User(id))";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
            System.out.println("Account table created or already exists.");
            
            try {
                String alterTableSQL = "ALTER TABLE Account ADD COLUMN accountType TEXT DEFAULT 'Savings'";
                stmt.execute(alterTableSQL);
                System.out.println("Account table updated with accountType column.");
            } catch (SQLException e) {
                System.out.println("accountType column may already exist: " + e.getMessage());
            }
        } catch (SQLException e) {
            System.out.println("Error creating table: " + e.getMessage());
        }
    }

    // Method to insert an account into the table
    public static void insertAccount(Long userId, Double balance) {
        insertAccount(userId, balance, TYPE_SAVINGS);
    }
    
    // Method to insert an account with specific account type
    public static void insertAccount(Long userId, Double balance, String accountType) {
        String insertSQL = "INSERT INTO Account (accNo, user_id, balance, accountType) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            String accNo = generateAccountNumber();

            pstmt.setString(1, accNo);
            pstmt.setLong(2, userId);
            pstmt.setDouble(3, balance);
            pstmt.setString(4, accountType != null ? accountType : TYPE_SAVINGS);
            pstmt.executeUpdate();

            System.out.println("Account inserted successfully with accNo: " + accNo + ", type: " + accountType);
        } catch (SQLException e) {
            System.out.println("Error inserting account: " + e.getMessage());
        }
    }

    // Utility method to generate an 8-digit account number
    private static String generateAccountNumber() {
        return String.format("%08d", new Random().nextInt(100000000));
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
                String accountType = rs.getString("accountType");

                account = new Account();
                account.setAccNo(id);
                account.setBalance(balance);
                if (accountType != null) {
                    account.setAccountType(accountType);
                } else {
                    account.setAccountType(TYPE_SAVINGS);
                }

                User user = getUserById(userId);
                account.setUser(user);

            }
        } catch (SQLException e) {
            System.out.println("Error retrieving account: " + e.getMessage());
        }

        return account;
    }
    public static List<Account> getAccountByUserId(User user) {
        String selectSQL = "SELECT * FROM Account WHERE user_id = ?";
        List<Account> account = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
            pstmt.setLong(1, user.getId());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Long AccNo = rs.getLong("accNo");
                Double balance = rs.getDouble("balance");
                String accountType = rs.getString("accountType");
                if (accountType == null) {
                    accountType = TYPE_SAVINGS;
                }

                account.add(new Account(AccNo, user, balance, accountType));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving account: " + e.getMessage());
        }

        return account;
    }
    
    public static Account getStockAccountByUser(User user) {
        List<Account> accounts = getAccountByUserId(user);
        for (Account acc : accounts) {
            if (TYPE_STOCK.equals(acc.getAccountType())) {
                return acc;
            }
        }
        return null;
    }
    
    public static List<Account> getNonStockAccountsByUser(User user) {
        List<Account> accounts = getAccountByUserId(user);
        List<Account> nonStockAccounts = new ArrayList<>();
        for (Account acc : accounts) {
            if (!TYPE_STOCK.equals(acc.getAccountType())) {
                nonStockAccounts.add(acc);
            }
        }
        return nonStockAccounts;
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

    // Method to get all accounts except the current user's accounts
    public static List<Account> getAllAccountsExceptUser(User user) {
        String selectSQL = "SELECT * FROM Account WHERE user_id != ?";
        List<Account> accounts = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
            pstmt.setLong(1, user.getId());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Long AccNo = rs.getLong("accNo");
                Long userId = rs.getLong("user_id");
                Double balance = rs.getDouble("balance");
                String accountType = rs.getString("accountType");
                if (accountType == null) {
                    accountType = TYPE_SAVINGS;
                }

                User accountUser = getUserById(userId);
                accounts.add(new Account(AccNo, accountUser, balance, accountType));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving accounts: " + e.getMessage());
        }

        return accounts;
    }

}
