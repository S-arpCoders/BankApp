package com.Scoders.BankingApp.model;

public class SupportFAQ {
    private Long id;
    private String question;
    private String answer;
    private String keywords;
    private String category;

    public SupportFAQ() {
    }

    public SupportFAQ(Long id, String question, String answer, String keywords, String category) {
        this.id = id;
        this.question = question;
        this.answer = answer;
        this.keywords = keywords;
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
