package com.Scoders.BankingApp.model;

public class FAQ {
    private Long id;
    private String question;
    private String answer;
    private String keywords;
    private String category;
    private Integer priority;

    public FAQ() {
    }

    public FAQ(String question, String answer, String keywords, String category, Integer priority) {
        this.question = question;
        this.answer = answer;
        this.keywords = keywords;
        this.category = category;
        this.priority = priority;
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

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }
}
