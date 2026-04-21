package com.Scoders.BankingApp.database;

import com.Scoders.BankingApp.model.Account;
import com.Scoders.BankingApp.model.User;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class TransactionalAccountHelper {

    private final DataSource dataSource;

    public TransactionalAccountHelper(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Account getAccountByAccNo(Long accNo) throws SQLException {
        String selectSQL = "SELECT * FROM Account WHERE accNo = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
            pstmt.setLong(1, accNo);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Long id = rs.getLong("accNo");
                Long userId = rs.getLong("user_id");
                Double balance = rs.getDouble("balance");
                String accountType = rs.getString("accountType");

                Account account = new Account();
                account.setAccNo(id);
                account.setBalance(balance);
                account.setAccountType(accountType != null ? accountType : "Savings");

                User user = UserDatabase.getUserById(userId);
                account.setUser(user);
                return account;
            }
        }
        return null;
    }

    public void updateBalance(Long accNo, Double newBalance) throws SQLException {
        String updateSQL = "UPDATE Account SET balance = ? WHERE accNo = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
            pstmt.setDouble(1, newBalance);
            pstmt.setLong(2, accNo);
            pstmt.executeUpdate();
        }
    }

    public void insertTransaction(Long accNo, Double amount, String transactionType) throws SQLException {
        String insertSQL = "INSERT INTO Transactions (amount, dateTime, accNo, transactionType) VALUES (?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setDouble(1, amount);
            pstmt.setString(2, LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
            pstmt.setLong(3, accNo);
            pstmt.setString(4, transactionType);
            pstmt.executeUpdate();
        }
    }

    public long insertAccount(Long userId, Double balance) throws SQLException {
        return insertAccount(userId, balance, "Savings");
    }

    public long insertAccount(Long userId, Double balance, String accountType) throws SQLException {
        String insertSQL = "INSERT INTO Account (accNo, user_id, balance, accountType) VALUES (?, ?, ?, ?)";
        String accNo = generateAccountNumber();
        
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setString(1, accNo);
            pstmt.setLong(2, userId);
            pstmt.setDouble(3, balance);
            pstmt.setString(4, accountType != null ? accountType : "Savings");
            pstmt.executeUpdate();
        }
        return Long.parseLong(accNo);
    }

    public void deleteAccount(Long accNo) throws SQLException {
        String deleteSQL = "DELETE FROM Account WHERE accNo = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(deleteSQL)) {
            pstmt.setLong(1, accNo);
            pstmt.executeUpdate();
        }
    }

    public void deleteTransactionsByAccount(Long accNo) throws SQLException {
        String deleteSQL = "DELETE FROM Transactions WHERE accNo = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(deleteSQL)) {
            pstmt.setLong(1, accNo);
            pstmt.executeUpdate();
        }
    }

    private String generateAccountNumber() {
        return String.format("%08d", (int) (Math.random() * 100000000));
    }
}
