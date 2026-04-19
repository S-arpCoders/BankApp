package com.Scoders.BankingApp.service;

import com.Scoders.BankingApp.database.SupportFAQDatabase;
import com.Scoders.BankingApp.model.SupportFAQ;

import java.util.List;

public class SupportService {

    private static final String DEFAULT_REPLY = "该问题已提交，请等待回复";

    public static String getAutoReply(String userQuestion) {
        List<SupportFAQ> faqs = SupportFAQDatabase.getAllSupportFAQs();
        
        if (faqs.isEmpty()) {
            return DEFAULT_REPLY;
        }

        String lowerCaseQuestion = userQuestion.toLowerCase();

        int maxMatchScore = 0;
        String bestMatchAnswer = null;

        for (SupportFAQ faq : faqs) {
            int score = calculateMatchScore(lowerCaseQuestion, faq.getKeywords());
            if (score > maxMatchScore) {
                maxMatchScore = score;
                bestMatchAnswer = faq.getAnswer();
            }
        }

        if (maxMatchScore > 0) {
            return bestMatchAnswer;
        }

        return DEFAULT_REPLY;
    }

    private static int calculateMatchScore(String question, String keywords) {
        if (keywords == null || keywords.isEmpty()) {
            return 0;
        }

        int score = 0;
        String[] keywordArray = keywords.split(",");

        for (String keyword : keywordArray) {
            String trimmedKeyword = keyword.trim().toLowerCase();
            if (!trimmedKeyword.isEmpty() && question.contains(trimmedKeyword)) {
                score += trimmedKeyword.length();
            }
        }

        return score;
    }

    public static boolean isAutoReply(String reply) {
        return !DEFAULT_REPLY.equals(reply);
    }

    public static String getDefaultReply() {
        return DEFAULT_REPLY;
    }
}
