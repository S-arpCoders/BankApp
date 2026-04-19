package com.Scoders.BankingApp.database;

import com.Scoders.BankingApp.model.FAQ;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FAQDatabase {

    private static final String DATABASE_URL = "jdbc:sqlite:bank.db";

    public static void createFAQTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS FAQ ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "question TEXT NOT NULL, "
                + "answer TEXT NOT NULL, "
                + "keywords TEXT, "
                + "category TEXT, "
                + "priority INTEGER DEFAULT 0)";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
            System.out.println("FAQ table created or already exists.");
        } catch (SQLException e) {
            System.out.println("Error creating FAQ table: " + e.getMessage());
        }
    }

    public static void insertFAQ(FAQ faq) {
        String insertSQL = "INSERT INTO FAQ (question, answer, keywords, category, priority) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setString(1, faq.getQuestion());
            pstmt.setString(2, faq.getAnswer());
            pstmt.setString(3, faq.getKeywords());
            pstmt.setString(4, faq.getCategory());
            pstmt.setInt(5, faq.getPriority() != null ? faq.getPriority() : 0);
            pstmt.executeUpdate();
            System.out.println("FAQ inserted successfully: " + faq.getQuestion());
        } catch (SQLException e) {
            System.out.println("Error inserting FAQ: " + e.getMessage());
        }
    }

    public static List<FAQ> getAllFAQs() {
        String selectSQL = "SELECT * FROM FAQ ORDER BY priority DESC, id ASC";
        List<FAQ> faqs = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectSQL)) {

            while (rs.next()) {
                faqs.add(mapResultSetToFAQ(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving all FAQs: " + e.getMessage());
        }

        return faqs;
    }

    public static FAQ getFAQById(Long id) {
        String selectSQL = "SELECT * FROM FAQ WHERE id = ?";
        FAQ faq = null;

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                faq = mapResultSetToFAQ(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving FAQ by ID: " + e.getMessage());
        }

        return faq;
    }

    public static void updateFAQ(FAQ faq) {
        String updateSQL = "UPDATE FAQ SET question = ?, answer = ?, keywords = ?, category = ?, priority = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
            pstmt.setString(1, faq.getQuestion());
            pstmt.setString(2, faq.getAnswer());
            pstmt.setString(3, faq.getKeywords());
            pstmt.setString(4, faq.getCategory());
            pstmt.setInt(5, faq.getPriority() != null ? faq.getPriority() : 0);
            pstmt.setLong(6, faq.getId());
            pstmt.executeUpdate();
            System.out.println("FAQ updated successfully.");
        } catch (SQLException e) {
            System.out.println("Error updating FAQ: " + e.getMessage());
        }
    }

    public static void deleteFAQ(Long id) {
        String deleteSQL = "DELETE FROM FAQ WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement pstmt = conn.prepareStatement(deleteSQL)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
            System.out.println("FAQ deleted successfully.");
        } catch (SQLException e) {
            System.out.println("Error deleting FAQ: " + e.getMessage());
        }
    }

    public static long getFAQCount() {
        String countSQL = "SELECT COUNT(*) FROM FAQ";
        long count = 0;

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(countSQL)) {

            if (rs.next()) {
                count = rs.getLong(1);
            }
        } catch (SQLException e) {
            System.out.println("Error counting FAQs: " + e.getMessage());
        }

        return count;
    }

    private static FAQ mapResultSetToFAQ(ResultSet rs) throws SQLException {
        FAQ faq = new FAQ();
        faq.setId(rs.getLong("id"));
        faq.setQuestion(rs.getString("question"));
        faq.setAnswer(rs.getString("answer"));
        faq.setKeywords(rs.getString("keywords"));
        faq.setCategory(rs.getString("category"));
        faq.setPriority(rs.getInt("priority"));
        return faq;
    }
}
