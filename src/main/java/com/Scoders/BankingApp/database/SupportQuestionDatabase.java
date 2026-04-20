package com.Scoders.BankingApp.database;

import com.Scoders.BankingApp.model.SupportQuestion;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class SupportQuestionDatabase {

    private static final String DATABASE_URL = "jdbc:sqlite:bank.db";
    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    public static void createSupportQuestionTable() {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS SupportQuestion ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "userId INTEGER, "
                + "question TEXT, "
                + "answer TEXT, "
                + "createdAt TEXT, "
                + "answeredAt TEXT, "
                + "status TEXT, "
                + "isSmartReply INTEGER DEFAULT 0, "
                + "FOREIGN KEY(userId) REFERENCES User(id))";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(createTableSQL);
            try {
                stmt.execute("ALTER TABLE SupportQuestion ADD COLUMN isSmartReply INTEGER DEFAULT 0");
                System.out.println("Added isSmartReply column to SupportQuestion table.");
            } catch (SQLException e) {
                System.out.println("isSmartReply column may already exist: " + e.getMessage());
            }
            System.out.println("SupportQuestion table created or already exists.");
        } catch (SQLException e) {
            System.out.println("Error creating SupportQuestion table: " + e.getMessage());
        }
    }

    public static void insertSupportQuestion(Long userId, String question) {
        String insertSQL = "INSERT INTO SupportQuestion (userId, question, createdAt, status) VALUES (?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement pstmt = conn.prepareStatement(insertSQL)) {
            pstmt.setLong(1, userId);
            pstmt.setString(2, question);
            pstmt.setString(3, LocalDateTime.now().format(ISO_FORMATTER));
            pstmt.setString(4, "PENDING");
            pstmt.executeUpdate();
            System.out.println("Support question inserted successfully.");
        } catch (SQLException e) {
            System.out.println("Error inserting support question: " + e.getMessage());
        }
    }

    public static SupportQuestion getSupportQuestionById(Long id) {
        String selectSQL = "SELECT * FROM SupportQuestion WHERE id = ?";
        SupportQuestion question = null;

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
            pstmt.setLong(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                question = mapResultSetToSupportQuestion(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving support question: " + e.getMessage());
        }

        return question;
    }

    public static List<SupportQuestion> getSupportQuestionsByUserId(Long userId) {
        String selectSQL = "SELECT * FROM SupportQuestion WHERE userId = ? ORDER BY createdAt ASC";
        List<SupportQuestion> questions = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
            pstmt.setLong(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                questions.add(mapResultSetToSupportQuestion(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving support questions: " + e.getMessage());
        }

        return questions;
    }

    public static SupportQuestion getLatestQuestionByUserId(Long userId) {
        String selectSQL = "SELECT * FROM SupportQuestion WHERE userId = ? ORDER BY createdAt DESC LIMIT 1";
        SupportQuestion question = null;

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement pstmt = conn.prepareStatement(selectSQL)) {
            pstmt.setLong(1, userId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                question = mapResultSetToSupportQuestion(rs);
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving latest question: " + e.getMessage());
        }

        return question;
    }

    public static void updateSupportQuestionAnswer(Long id, String answer, boolean isSmartReply) {
        String updateSQL = "UPDATE SupportQuestion SET answer = ?, answeredAt = ?, status = ?, isSmartReply = ? WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             PreparedStatement pstmt = conn.prepareStatement(updateSQL)) {
            pstmt.setString(1, answer);
            pstmt.setString(2, LocalDateTime.now().format(ISO_FORMATTER));
            pstmt.setString(3, "ANSWERED");
            pstmt.setInt(4, isSmartReply ? 1 : 0);
            pstmt.setLong(5, id);
            pstmt.executeUpdate();
            System.out.println("Support question answer updated successfully.");
        } catch (SQLException e) {
            System.out.println("Error updating support question: " + e.getMessage());
        }
    }

    public static List<SupportQuestion> getAllSupportQuestions() {
        String selectSQL = "SELECT * FROM SupportQuestion ORDER BY createdAt DESC";
        List<SupportQuestion> questions = new ArrayList<>();

        try (Connection conn = DriverManager.getConnection(DATABASE_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectSQL)) {

            while (rs.next()) {
                questions.add(mapResultSetToSupportQuestion(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving all support questions: " + e.getMessage());
        }

        return questions;
    }

    private static SupportQuestion mapResultSetToSupportQuestion(ResultSet rs) throws SQLException {
        SupportQuestion question = new SupportQuestion();
        question.setId(rs.getLong("id"));
        question.setUserId(rs.getLong("userId"));
        question.setQuestion(rs.getString("question"));
        question.setAnswer(rs.getString("answer"));
        question.setStatus(rs.getString("status"));

        try {
            int isSmartReplyInt = rs.getInt("isSmartReply");
            question.setIsSmartReply(isSmartReplyInt == 1);
        } catch (SQLException e) {
            question.setIsSmartReply(false);
        }

        String createdAtStr = rs.getString("createdAt");
        if (createdAtStr != null && !createdAtStr.isEmpty()) {
            question.setCreatedAt(LocalDateTime.parse(createdAtStr, ISO_FORMATTER));
        }

        String answeredAtStr = rs.getString("answeredAt");
        if (answeredAtStr != null && !answeredAtStr.isEmpty()) {
            question.setAnsweredAt(LocalDateTime.parse(answeredAtStr, ISO_FORMATTER));
        }

        return question;
    }
}
