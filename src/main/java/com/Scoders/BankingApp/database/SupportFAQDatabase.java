package com.Scoders.BankingApp.database;

import com.Scoders.BankingApp.model.SupportFAQ;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SupportFAQDatabase {

    private static final String DATABASE_URL = "jdbc:sqlite:bank.db";

    public static void createSupportFAQTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS SupportFAQ ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "question TEXT, "
                + "answer TEXT, "
                + "keywords TEXT, "
                + "category TEXT)";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
            System.out.println("SupportFAQ table created or already exists.");
        } catch (SQLException e) {
            System.out.println("Error creating SupportFAQ table: " + e.getMessage());
        }
    }

    public static void insertSupportFAQ(String question, String answer, String keywords, String category) {
        String insertSQL = "INSERT INTO SupportFAQ (question, answer, keywords, category) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setString(1, question);
            pstmt.setString(2, answer);
            pstmt.setString(3, keywords);
            pstmt.setString(4, category);
            pstmt.executeUpdate();
            System.out.println("Support FAQ inserted successfully: " + question);
        } catch (SQLException e) {
            System.out.println("Error inserting support FAQ: " + e.getMessage());
        }
    }

    public static List<SupportFAQ> getAllSupportFAQs() {
        String selectSQL = "SELECT * FROM SupportFAQ";
        List<SupportFAQ> faqs = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectSQL)) {

            while (rs.next()) {
                faqs.add(mapResultSetToSupportFAQ(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving all support FAQs: " + e.getMessage());
        }

        return faqs;
    }

    public static List<SupportFAQ> getFAQsByCategory(String category) {
        String selectSQL = "SELECT * FROM SupportFAQ WHERE category = ?";
        List<SupportFAQ> faqs = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
            pstmt.setString(1, category);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                faqs.add(mapResultSetToSupportFAQ(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving FAQs by category: " + e.getMessage());
        }

        return faqs;
    }

    public static boolean isFAQTableEmpty() {
        String countSQL = "SELECT COUNT(*) as count FROM SupportFAQ";
        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(countSQL)) {
            if (rs.next()) {
                return rs.getInt("count") == 0;
            }
        } catch (SQLException e) {
            System.out.println("Error checking FAQ table: " + e.getMessage());
        }
        return true;
    }

    public static void deleteAllFAQs() {
        String deleteSQL = "DELETE FROM SupportFAQ";
        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(deleteSQL);
            System.out.println("All FAQs deleted successfully.");
        } catch (SQLException e) {
            System.out.println("Error deleting FAQs: " + e.getMessage());
        }
    }

    private static SupportFAQ mapResultSetToSupportFAQ(ResultSet rs) throws SQLException {
        SupportFAQ faq = new SupportFAQ();
        faq.setId(rs.getLong("id"));
        faq.setQuestion(rs.getString("question"));
        faq.setAnswer(rs.getString("answer"));
        faq.setKeywords(rs.getString("keywords"));
        faq.setCategory(rs.getString("category"));
        return faq;
    }
}
