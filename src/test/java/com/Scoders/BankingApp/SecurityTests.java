package com.Scoders.BankingApp;

import com.Scoders.BankingApp.security.PasswordEncoder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SecurityTests {

    @Test
    void testPasswordEncoding() {
        String rawPassword = "testPassword123";
        String encodedPassword = PasswordEncoder.encode(rawPassword);
        
        assertNotNull(encodedPassword);
        assertNotEquals(rawPassword, encodedPassword);
        assertTrue(PasswordEncoder.matches(rawPassword, encodedPassword));
        assertFalse(PasswordEncoder.matches("wrongPassword", encodedPassword));
    }

    @Test
    void testPasswordConsistency() {
        String password1 = "password123";
        String password2 = "password123";
        String password3 = "differentPassword";
        
        assertTrue(password1.equals(password2));
        assertFalse(password1.equals(password3));
    }

    @Test
    void testBCryptProperties() {
        String password = "securePassword";
        String encoded1 = PasswordEncoder.encode(password);
        String encoded2 = PasswordEncoder.encode(password);
        
        assertNotEquals(encoded1, encoded2);
        assertTrue(PasswordEncoder.matches(password, encoded1));
        assertTrue(PasswordEncoder.matches(password, encoded2));
    }

    @Test
    void testEmptyPassword() {
        String emptyPassword = "";
        String encoded = PasswordEncoder.encode(emptyPassword);
        
        assertNotNull(encoded);
        assertTrue(PasswordEncoder.matches(emptyPassword, encoded));
    }

    @Test
    void testNullPassword() {
        assertThrows(IllegalArgumentException.class, () -> {
            PasswordEncoder.encode(null);
        });
    }
}
