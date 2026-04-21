package com.Scoders.BankingApp.security;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class CaptchaService {

    private static final int CAPTCHA_LENGTH = 4;
    private static final int IMAGE_WIDTH = 120;
    private static final int IMAGE_HEIGHT = 40;
    private static final String CAPTCHA_CHARACTERS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    
    private static final ConcurrentHashMap<String, String> captchaStore = new ConcurrentHashMap<>();
    private static final Random random = new Random();

    public static String generateCaptcha(String sessionId) {
        StringBuilder captcha = new StringBuilder();
        for (int i = 0; i < CAPTCHA_LENGTH; i++) {
            captcha.append(CAPTCHA_CHARACTERS.charAt(random.nextInt(CAPTCHA_CHARACTERS.length())));
        }
        captchaStore.put(sessionId, captcha.toString());
        return captcha.toString();
    }

    public static boolean validateCaptcha(String sessionId, String userInput) {
        String storedCaptcha = captchaStore.get(sessionId);
        if (storedCaptcha == null || userInput == null) {
            return false;
        }
        captchaStore.remove(sessionId);
        return storedCaptcha.equalsIgnoreCase(userInput.trim());
    }

    public static void clearCaptcha(String sessionId) {
        captchaStore.remove(sessionId);
    }

    public static BufferedImage generateCaptchaImage(String captcha) {
        BufferedImage image = new BufferedImage(IMAGE_WIDTH, IMAGE_HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT);
        
        g.setFont(new Font("Arial", Font.BOLD, 28));
        
        for (int i = 0; i < captcha.length(); i++) {
            int x = 10 + i * 28;
            int y = 30;
            
            g.setColor(new Color(random.nextInt(100), random.nextInt(100), random.nextInt(100)));
            
            AffineTransform transform = new AffineTransform();
            transform.rotate(random.nextDouble() * 0.4 - 0.2, x, y);
            g.setTransform(transform);
            
            g.drawString(String.valueOf(captcha.charAt(i)), x, y);
        }
        
        for (int i = 0; i < 8; i++) {
            int x1 = random.nextInt(IMAGE_WIDTH);
            int y1 = random.nextInt(IMAGE_HEIGHT);
            int x2 = random.nextInt(IMAGE_WIDTH);
            int y2 = random.nextInt(IMAGE_HEIGHT);
            g.setColor(new Color(random.nextInt(200), random.nextInt(200), random.nextInt(200)));
            g.drawLine(x1, y1, x2, y2);
        }
        
        g.dispose();
        return image;
    }
}
