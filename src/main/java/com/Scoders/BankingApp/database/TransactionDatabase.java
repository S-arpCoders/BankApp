package com.Scoders.BankingApp.database;

import com.Scoders.BankingApp.model.Account;
import com.Scoders.BankingApp.model.User;
import com.Scoders.BankingApp.model.transaction;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.time.format.DateTimeParseException;

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
                
                try {
                    // Use the same formatter as when inserting
                    transact.setDateTime(LocalDateTime.parse(dateTime, DateTimeFormatter.ISO_DATE_TIME));
                } catch (DateTimeParseException e) {
                    System.out.println("Error parsing dateTime for transaction " + id + ": " + e.getMessage());
                    // Try alternative format
                    try {
                        transact.setDateTime(LocalDateTime.parse(dateTime));
                    } catch (DateTimeParseException e2) {
                        System.out.println("Failed to parse dateTime with alternative format: " + e2.getMessage());
                        transact.setDateTime(LocalDateTime.now()); // Use current time as fallback
                    }
                }
                
                transactions.add(transact);
                System.out.println("Loaded transaction: id=" + id + ", type=" + transactionType + ", amount=" + amount);
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

    // Method to get all transactions for a specific user
    public static List<transaction> getTransactionsByUser(User user) {
        List<transaction> allTransactions = new ArrayList<>();
        List<Account> userAccounts = AccountDatabase.getAccountByUserId(user);

        for (Account account : userAccounts) {
            List<transaction> accountTransactions = getTransactionsByAccount(account);
            allTransactions.addAll(accountTransactions);
        }

        // Sort transactions by dateTime in descending order (newest first)
        Collections.sort(allTransactions, new Comparator<transaction>() {
            @Override
            public int compare(transaction t1, transaction t2) {
                LocalDateTime dt1 = t1.getDateTime();
                LocalDateTime dt2 = t2.getDateTime();
                
                if (dt1 == null && dt2 == null) {
                    return 0;
                }
                if (dt1 == null) {
                    return 1; // Null values go to the end
                }
                if (dt2 == null) {
                    return -1;
                }
                return dt2.compareTo(dt1);
            }
        });
        
        System.out.println("Total transactions loaded for user: " + allTransactions.size());

        return allTransactions;
    }

    // Method to get all transactions for a specific user with user details
    public static List<TransactionDTO> getTransactionDTOsByUser(User user) {
        List<transaction> transactions = getTransactionsByUser(user);
        List<TransactionDTO> transactionDTOs = new ArrayList<>();

        for (transaction trans : transactions) {
            TransactionDTO dto = new TransactionDTO();
            dto.setTransId(trans.getTransId());
            dto.setAmount(trans.getAmount());
            dto.setDateTime(trans.getDateTime());
            dto.setTransactionType(trans.getTransactionType());
            
            if (trans.getAccount() != null) {
                dto.setAccNo(trans.getAccount().getAccNo());
                if (trans.getAccount().getUser() != null) {
                    dto.setUsername(trans.getAccount().getUser().getUsername());
                    dto.setSurname(trans.getAccount().getUser().getSurname());
                }
            }
            
            transactionDTOs.add(dto);
        }

        return transactionDTOs;
    }

    // DTO class for transaction details
    public static class TransactionDTO {
        private Long transId;
        private Double amount;
        private LocalDateTime dateTime;
        private Long accNo;
        private String username;
        private String surname;
        private String transactionType;
        
        private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Getters and Setters
        public Long getTransId() {
            return transId;
        }

        public void setTransId(Long transId) {
            this.transId = transId;
        }

        public Double getAmount() {
            return amount;
        }

        public void setAmount(Double amount) {
            this.amount = amount;
        }

        public LocalDateTime getDateTime() {
            return dateTime;
        }

        public void setDateTime(LocalDateTime dateTime) {
            this.dateTime = dateTime;
        }
        
        public String getFormattedDateTime() {
            if (dateTime == null) {
                return "";
            }
            return dateTime.format(DISPLAY_FORMATTER);
        }

        public Long getAccNo() {
            return accNo;
        }

        public void setAccNo(Long accNo) {
            this.accNo = accNo;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getSurname() {
            return surname;
        }

        public void setSurname(String surname) {
            this.surname = surname;
        }
        
        public String getFullName() {
            StringBuilder fullName = new StringBuilder();
            if (username != null && !username.isEmpty()) {
                fullName.append(username);
            }
            if (surname != null && !surname.isEmpty()) {
                if (fullName.length() > 0) {
                    fullName.append(" ");
                }
                fullName.append(surname);
            }
            return fullName.toString();
        }

        public String getTransactionType() {
            return transactionType;
        }

        public void setTransactionType(String transactionType) {
            this.transactionType = transactionType;
        }
        
        public String getFormattedAmount() {
            if (amount == null) {
                return "R 0.00";
            }
            return String.format("R %.2f", amount);
        }
        
        public boolean isPositiveAmount() {
            if (transactionType == null) {
                return false;
            }
            return "Deposit".equals(transactionType) || "Transfer-receive".equals(transactionType);
        }
    }

}
