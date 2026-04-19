package com.Scoders.BankingApp.service;

import com.Scoders.BankingApp.database.FAQDatabase;
import com.Scoders.BankingApp.model.FAQ;

import java.util.List;

public class FAQService {

    private static final String DEFAULT_REPLY = "该问题已提交，请等待回复";

    public static String getSmartReply(String userQuestion) {
        if (userQuestion == null || userQuestion.trim().isEmpty()) {
            return DEFAULT_REPLY;
        }

        String lowerQuestion = userQuestion.trim().toLowerCase();
        List<FAQ> allFAQs = FAQDatabase.getAllFAQs();

        FAQ bestMatch = null;
        int maxMatchScore = 0;

        for (FAQ faq : allFAQs) {
            int score = calculateMatchScore(lowerQuestion, faq);
            if (score > maxMatchScore) {
                maxMatchScore = score;
                bestMatch = faq;
            }
        }

        if (bestMatch != null && maxMatchScore > 0) {
            return bestMatch.getAnswer();
        }

        return DEFAULT_REPLY;
    }

    private static int calculateMatchScore(String lowerQuestion, FAQ faq) {
        int score = 0;

        if (faq.getKeywords() == null || faq.getKeywords().isEmpty()) {
            return score;
        }

        String[] keywords = faq.getKeywords().split(",");
        int matchedKeywords = 0;

        for (String keyword : keywords) {
            String trimmedKeyword = keyword.trim().toLowerCase();
            if (!trimmedKeyword.isEmpty() && lowerQuestion.contains(trimmedKeyword)) {
                matchedKeywords++;
                score += 10;
            }
        }

        if (matchedKeywords > 0) {
            Integer priority = faq.getPriority();
            if (priority != null && priority > 0) {
                score += priority * 5;
            }
        }

        return score;
    }

    public static boolean isSmartReplyAvailable(String userQuestion) {
        if (userQuestion == null || userQuestion.trim().isEmpty()) {
            return false;
        }

        String lowerQuestion = userQuestion.trim().toLowerCase();
        List<FAQ> allFAQs = FAQDatabase.getAllFAQs();

        for (FAQ faq : allFAQs) {
            if (faq.getKeywords() == null || faq.getKeywords().isEmpty()) {
                continue;
            }

            String[] keywords = faq.getKeywords().split(",");
            for (String keyword : keywords) {
                String trimmedKeyword = keyword.trim().toLowerCase();
                if (!trimmedKeyword.isEmpty() && lowerQuestion.contains(trimmedKeyword)) {
                    return true;
                }
            }
        }

        return false;
    }
}
