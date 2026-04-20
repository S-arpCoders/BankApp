package com.Scoders.BankingApp;

import com.Scoders.BankingApp.security.CaptchaService;
import com.Scoders.BankingApp.security.LoginAttemptService;
import com.Scoders.BankingApp.security.PasswordValidator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SecurityPolicyTests {

    @Test
    void testPasswordValidator_ValidPassword() {
        String validPassword = "MyP@ssw0rd7x9";
        PasswordValidator.ValidationResult result = PasswordValidator.validate(validPassword);
        assertTrue(result.isValid(), "Valid password should pass validation: " + result.getMessage());
    }

    @Test
    void testPasswordValidator_TooShort() {
        String shortPassword = "Ab1!";
        PasswordValidator.ValidationResult result = PasswordValidator.validate(shortPassword);
        assertFalse(result.isValid(), "Short password should fail validation");
        assertTrue(result.getMessage().contains("至少") || result.getMessage().contains("8"), 
                "Error message should mention minimum length: " + result.getMessage());
    }

    @Test
    void testPasswordValidator_NoUppercase() {
        String password = "myp@ssw0rd123";
        PasswordValidator.ValidationResult result = PasswordValidator.validate(password);
        assertFalse(result.isValid(), "Password without uppercase should fail");
        assertTrue(result.getMessage().contains("大写字母"), 
                "Error message should mention uppercase: " + result.getMessage());
    }

    @Test
    void testPasswordValidator_NoLowercase() {
        String password = "MYP@SSW0RD123";
        PasswordValidator.ValidationResult result = PasswordValidator.validate(password);
        assertFalse(result.isValid(), "Password without lowercase should fail");
        assertTrue(result.getMessage().contains("小写字母"), 
                "Error message should mention lowercase: " + result.getMessage());
    }

    @Test
    void testPasswordValidator_NoDigit() {
        String password = "MyP@sswordABC";
        PasswordValidator.ValidationResult result = PasswordValidator.validate(password);
        assertFalse(result.isValid(), "Password without digit should fail");
        assertTrue(result.getMessage().contains("数字"), 
                "Error message should mention digit: " + result.getMessage());
    }

    @Test
    void testPasswordValidator_NoSpecialChar() {
        String password = "MyPassword123";
        PasswordValidator.ValidationResult result = PasswordValidator.validate(password);
        assertFalse(result.isValid(), "Password without special char should fail");
        assertTrue(result.getMessage().contains("特殊字符"), 
                "Error message should mention special char: " + result.getMessage());
    }

    @Test
    void testPasswordValidator_SequentialChars() {
        String password = "Abc123!@#";
        PasswordValidator.ValidationResult result = PasswordValidator.validate(password);
        assertFalse(result.isValid(), "Password with sequential chars should fail");
        assertTrue(result.getMessage().contains("连续"), 
                "Error message should mention sequential: " + result.getMessage());
    }

    @Test
    void testPasswordValidator_RepeatedChars() {
        String password = "Aaa111!!!@";
        PasswordValidator.ValidationResult result = PasswordValidator.validate(password);
        assertFalse(result.isValid(), "Password with repeated chars should fail");
        assertTrue(result.getMessage().contains("重复"), 
                "Error message should mention repeated: " + result.getMessage());
    }

    @Test
    void testPasswordValidator_NullPassword() {
        PasswordValidator.ValidationResult result = PasswordValidator.validate(null);
        assertFalse(result.isValid(), "Null password should fail");
    }

    @Test
    void testPasswordValidator_EmptyPassword() {
        PasswordValidator.ValidationResult result = PasswordValidator.validate("");
        assertFalse(result.isValid(), "Empty password should fail");
    }

    @Test
    void testLoginAttemptService_InitialState() {
        String username = "testUser_" + System.currentTimeMillis();
        assertFalse(LoginAttemptService.isBlocked(username), "New user should not be blocked");
        assertFalse(LoginAttemptService.requiresCaptcha(username), "New user should not require captcha");
        assertEquals(5, LoginAttemptService.getRemainingAttempts(username), 
                "New user should have 5 attempts");
    }

    @Test
    void testLoginAttemptService_FailedAttempts() {
        String username = "testUser_fail_" + System.currentTimeMillis();
        
        LoginAttemptService.loginFailed(username);
        assertEquals(4, LoginAttemptService.getRemainingAttempts(username));
        assertFalse(LoginAttemptService.requiresCaptcha(username));
        
        LoginAttemptService.loginFailed(username);
        assertEquals(3, LoginAttemptService.getRemainingAttempts(username));
        assertFalse(LoginAttemptService.requiresCaptcha(username));
        
        LoginAttemptService.loginFailed(username);
        assertEquals(2, LoginAttemptService.getRemainingAttempts(username));
        assertFalse(LoginAttemptService.requiresCaptcha(username));
        
        LoginAttemptService.loginFailed(username);
        assertEquals(1, LoginAttemptService.getRemainingAttempts(username));
        assertFalse(LoginAttemptService.requiresCaptcha(username));
        
        LoginAttemptService.loginFailed(username);
        assertEquals(0, LoginAttemptService.getRemainingAttempts(username));
        assertTrue(LoginAttemptService.requiresCaptcha(username), 
                "After 5 failed attempts, captcha should be required");
        assertTrue(LoginAttemptService.isBlocked(username));
    }

    @Test
    void testLoginAttemptService_SuccessfulLoginResets() {
        String username = "testUser_reset_" + System.currentTimeMillis();
        
        LoginAttemptService.loginFailed(username);
        LoginAttemptService.loginFailed(username);
        LoginAttemptService.loginFailed(username);
        assertEquals(2, LoginAttemptService.getRemainingAttempts(username));
        
        LoginAttemptService.loginSucceeded(username);
        assertEquals(5, LoginAttemptService.getRemainingAttempts(username));
        assertFalse(LoginAttemptService.requiresCaptcha(username));
    }

    @Test
    void testLoginAttemptService_ResetAttempts() {
        String username = "testUser_manualReset_" + System.currentTimeMillis();
        
        LoginAttemptService.loginFailed(username);
        LoginAttemptService.loginFailed(username);
        assertEquals(3, LoginAttemptService.getRemainingAttempts(username));
        
        LoginAttemptService.resetAttempts(username);
        assertEquals(5, LoginAttemptService.getRemainingAttempts(username));
    }

    @Test
    void testCaptchaService_GenerateAndValidate() {
        String sessionId = "testSession_" + System.currentTimeMillis();
        
        String captcha = CaptchaService.generateCaptcha(sessionId);
        assertNotNull(captcha);
        assertEquals(4, captcha.length());
        
        assertTrue(CaptchaService.validateCaptcha(sessionId, captcha), 
                "Correct captcha should be valid");
        
        assertFalse(CaptchaService.validateCaptcha(sessionId, captcha), 
                "Captcha should be invalidated after first validation");
    }

    @Test
    void testCaptchaService_WrongCaptcha() {
        String sessionId = "testSession_wrong_" + System.currentTimeMillis();
        
        CaptchaService.generateCaptcha(sessionId);
        
        assertFalse(CaptchaService.validateCaptcha(sessionId, "WRONG"));
    }

    @Test
    void testCaptchaService_NullInput() {
        String sessionId = "testSession_null_" + System.currentTimeMillis();
        
        CaptchaService.generateCaptcha(sessionId);
        
        assertFalse(CaptchaService.validateCaptcha(sessionId, null));
    }

    @Test
    void testCaptchaService_ClearCaptcha() {
        String sessionId = "testSession_clear_" + System.currentTimeMillis();
        
        String captcha = CaptchaService.generateCaptcha(sessionId);
        CaptchaService.clearCaptcha(sessionId);
        
        assertFalse(CaptchaService.validateCaptcha(sessionId, captcha));
    }

    @Test
    void testCaptchaService_NonexistentSession() {
        String sessionId = "testSession_nonexistent_" + System.currentTimeMillis();
        
        assertFalse(CaptchaService.validateCaptcha(sessionId, "ABCD"));
    }
}
