package com.Scoders.BankingApp.security;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 5;
    private static final long LOCK_TIME_DURATION = TimeUnit.MINUTES.toMillis(30);
    
    private static final ConcurrentHashMap<String, LoginAttempt> attemptsCache = new ConcurrentHashMap<>();

    public static class LoginAttempt {
        private int attempts;
        private long lastAttemptTime;

        public LoginAttempt(int attempts, long lastAttemptTime) {
            this.attempts = attempts;
            this.lastAttemptTime = lastAttemptTime;
        }

        public int getAttempts() {
            return attempts;
        }

        public void setAttempts(int attempts) {
            this.attempts = attempts;
        }

        public long getLastAttemptTime() {
            return lastAttemptTime;
        }

        public void setLastAttemptTime(long lastAttemptTime) {
            this.lastAttemptTime = lastAttemptTime;
        }
    }

    public static void loginFailed(String username) {
        LoginAttempt attempt = attemptsCache.get(username);
        if (attempt == null) {
            attempt = new LoginAttempt(0, System.currentTimeMillis());
        }
        attempt.setAttempts(attempt.getAttempts() + 1);
        attempt.setLastAttemptTime(System.currentTimeMillis());
        attemptsCache.put(username, attempt);
    }

    public static void loginSucceeded(String username) {
        attemptsCache.remove(username);
    }

    public static boolean isBlocked(String username) {
        LoginAttempt attempt = attemptsCache.get(username);
        if (attempt == null) {
            return false;
        }
        
        if (System.currentTimeMillis() - attempt.getLastAttemptTime() > LOCK_TIME_DURATION) {
            attemptsCache.remove(username);
            return false;
        }
        
        return attempt.getAttempts() >= MAX_ATTEMPTS;
    }

    public static boolean requiresCaptcha(String username) {
        LoginAttempt attempt = attemptsCache.get(username);
        if (attempt == null) {
            return false;
        }
        
        if (System.currentTimeMillis() - attempt.getLastAttemptTime() > LOCK_TIME_DURATION) {
            attemptsCache.remove(username);
            return false;
        }
        
        return attempt.getAttempts() >= MAX_ATTEMPTS;
    }

    public static int getRemainingAttempts(String username) {
        LoginAttempt attempt = attemptsCache.get(username);
        if (attempt == null) {
            return MAX_ATTEMPTS;
        }
        return Math.max(0, MAX_ATTEMPTS - attempt.getAttempts());
    }

    public static void resetAttempts(String username) {
        attemptsCache.remove(username);
    }
}
