package com.Scoders.BankingApp.database;

import com.Scoders.BankingApp.model.Account;
import com.Scoders.BankingApp.model.Trade;
import com.Scoders.BankingApp.model.User;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TradeDatabase {
    
    private static final String DATABASE_URL = "jdbc:sqlite:bank.db";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static void createTradeTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS Trade ("
                + "tradeId INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "account_id TEXT, "
                + "symbol TEXT, "
                + "assetName TEXT, "
                + "assetType TEXT, "
                + "tradeType TEXT, "
                + "quantity DOUBLE, "
                + "price DOUBLE, "
                + "totalAmount DOUBLE, "
                + "tradeTime TEXT, "
                + "status TEXT, "
                + "FOREIGN KEY(account_id) REFERENCES Account(accNo))";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
            System.out.println("Trade table created or already exists.");
        } catch (SQLException e) {
            System.out.println("Error creating Trade table: " + e.getMessage());
        }
    }

    public static void insertTrade(Trade trade) {
        String insertSQL = "INSERT INTO Trade (account_id, symbol, assetName, assetType, "
                + "tradeType, quantity, price, totalAmount, tradeTime, status) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {

            pstmt.setString(1, trade.getAccount().getAccNo().toString());
            pstmt.setString(2, trade.getSymbol());
            pstmt.setString(3, trade.getAssetName());
            pstmt.setString(4, trade.getAssetType());
            pstmt.setString(5, trade.getTradeType());
            pstmt.setDouble(6, trade.getQuantity());
            pstmt.setDouble(7, trade.getPrice());
            pstmt.setDouble(8, trade.getTotalAmount());
            pstmt.setString(9, trade.getTradeTime().format(FORMATTER));
            pstmt.setString(10, trade.getStatus());
            
            pstmt.executeUpdate();
            System.out.println("Trade inserted successfully: " + trade.getSymbol() + " " + trade.getTradeType());
        } catch (SQLException e) {
            System.out.println("Error inserting trade: " + e.getMessage());
        }
    }

    public static List<Trade> getTradesByAccount(Account account) {
        String selectSQL = "SELECT * FROM Trade WHERE account_id = ? ORDER BY tradeTime DESC";
        List<Trade> trades = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
            pstmt.setString(1, account.getAccNo().toString());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                Trade trade = new Trade();
                trade.setTradeId(rs.getLong("tradeId"));
                trade.setAccount(account);
                trade.setSymbol(rs.getString("symbol"));
                trade.setAssetName(rs.getString("assetName"));
                trade.setAssetType(rs.getString("assetType"));
                trade.setTradeType(rs.getString("tradeType"));
                trade.setQuantity(rs.getDouble("quantity"));
                trade.setPrice(rs.getDouble("price"));
                trade.setTotalAmount(rs.getDouble("totalAmount"));
                trade.setStatus(rs.getString("status"));
                
                String timeStr = rs.getString("tradeTime");
                if (timeStr != null) {
                    trade.setTradeTime(LocalDateTime.parse(timeStr, FORMATTER));
                }

                trades.add(trade);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving trades: " + e.getMessage());
        }

        return trades;
    }

    public static List<Trade> getTradesByUser(User user) {
        List<Trade> allTrades = new ArrayList<>();
        Account stockAccount = AccountDatabase.getStockAccountByUser(user);
        
        if (stockAccount != null) {
            allTrades = getTradesByAccount(stockAccount);
        }
        
        return allTrades;
    }

    public static List<Trade> getTradesByAssetType(User user, String assetType) {
        List<Trade> trades = getTradesByUser(user);
        List<Trade> filtered = new ArrayList<>();
        
        for (Trade trade : trades) {
            if (assetType.equals(trade.getAssetType())) {
                filtered.add(trade);
            }
        }
        
        return filtered;
    }
}
