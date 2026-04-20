package com.Scoders.BankingApp.service;

import com.Scoders.BankingApp.database.SupportFAQDatabase;
import com.Scoders.BankingApp.model.SupportFAQ;

import java.util.List;

public class SupportService {

    private static final String DEFAULT_REPLY = "该问题已提交，请等待回复";
    private static final int EXACT_MATCH_BONUS = 100;
    private static final int KEYWORD_COUNT_BONUS = 50;

    public static String getAutoReply(String userQuestion) {
        List<SupportFAQ> faqs = SupportFAQDatabase.getAllSupportFAQs();
        
        if (faqs.isEmpty()) {
            return DEFAULT_REPLY;
        }

        String lowerCaseQuestion = userQuestion.toLowerCase();

        int maxMatchScore = 0;
        String bestMatchAnswer = null;
        int bestMatchKeywordCount = 0;

        for (SupportFAQ faq : faqs) {
            MatchResult result = calculateMatchScore(lowerCaseQuestion, faq.getKeywords(), faq.getQuestion());
            
            if (result.score > maxMatchScore) {
                maxMatchScore = result.score;
                bestMatchAnswer = faq.getAnswer();
                bestMatchKeywordCount = result.matchedKeywords;
            } else if (result.score == maxMatchScore && result.score > 0) {
                if (result.matchedKeywords > bestMatchKeywordCount) {
                    bestMatchAnswer = faq.getAnswer();
                    bestMatchKeywordCount = result.matchedKeywords;
                }
            }
        }

        if (maxMatchScore > 0) {
            return bestMatchAnswer;
        }

        return DEFAULT_REPLY;
    }

    private static MatchResult calculateMatchScore(String question, String keywords, String faqQuestion) {
        MatchResult result = new MatchResult(0, 0);
        
        if (keywords == null || keywords.isEmpty()) {
            return result;
        }

        String[] keywordArray = keywords.split(",");
        int matchedCount = 0;
        int score = 0;

        for (String keyword : keywordArray) {
            String trimmedKeyword = keyword.trim().toLowerCase();
            if (!trimmedKeyword.isEmpty() && question.contains(trimmedKeyword)) {
                score += trimmedKeyword.length() * 10;
                matchedCount++;
                
                if (isWordBoundaryMatch(question, trimmedKeyword)) {
                    score += EXACT_MATCH_BONUS;
                }
            }
        }

        if (faqQuestion != null && !faqQuestion.isEmpty()) {
            String lowerFaqQuestion = faqQuestion.toLowerCase();
            if (question.contains(lowerFaqQuestion) || lowerFaqQuestion.contains(question)) {
                score += EXACT_MATCH_BONUS * 2;
            }
        }

        if (matchedCount > 0) {
            score += matchedCount * KEYWORD_COUNT_BONUS;
        }

        return new MatchResult(score, matchedCount);
    }

    private static boolean isWordBoundaryMatch(String question, String keyword) {
        int index = question.indexOf(keyword);
        if (index == -1) {
            return false;
        }

        boolean beforeIsBoundary = (index == 0) || !Character.isLetterOrDigit(question.charAt(index - 1));
        boolean afterIsBoundary = (index + keyword.length() == question.length()) || 
                                  !Character.isLetterOrDigit(question.charAt(index + keyword.length()));

        return beforeIsBoundary && afterIsBoundary;
    }

    public static boolean isAutoReply(String reply) {
        return !DEFAULT_REPLY.equals(reply);
    }

    public static String getDefaultReply() {
        return DEFAULT_REPLY;
    }

    private static class MatchResult {
        int score;
        int matchedKeywords;

        MatchResult(int score, int matchedKeywords) {
            this.score = score;
            this.matchedKeywords = matchedKeywords;
        }
    }
}
