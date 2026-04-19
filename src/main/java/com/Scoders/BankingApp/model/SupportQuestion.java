package com.Scoders.BankingApp.model;

import java.time.LocalDateTime;

public class SupportQuestion {
    private Long id;
    private Long userId;
    private String question;
    private String answer;
    private LocalDateTime createdAt;
    private LocalDateTime answeredAt;
    private String status;
    private Boolean isSmartReply;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getAnsweredAt() {
        return answeredAt;
    }

    public void setAnsweredAt(LocalDateTime answeredAt) {
        this.answeredAt = answeredAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Boolean getIsSmartReply() {
        return isSmartReply;
    }

    public void setIsSmartReply(Boolean isSmartReply) {
        this.isSmartReply = isSmartReply;
    }
}
