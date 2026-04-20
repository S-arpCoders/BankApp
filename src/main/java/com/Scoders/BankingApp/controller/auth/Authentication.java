package com.Scoders.BankingApp.controller.auth;

import com.Scoders.BankingApp.database.UserDatabase;
import com.Scoders.BankingApp.model.User;
import com.Scoders.BankingApp.security.CaptchaService;
import com.Scoders.BankingApp.security.LoginAttemptService;
import com.Scoders.BankingApp.security.PasswordEncoder;
import com.Scoders.BankingApp.security.PasswordValidator;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;

public class Authentication {

    private static final String ERROR_MESSAGE = "用户名或密码错误，请重新尝试";

    public PasswordValidator.ValidationResult register(String username, String surname, String password) {
        PasswordValidator.ValidationResult validationResult = PasswordValidator.validate(password);
        if (!validationResult.isValid()) {
            return validationResult;
        }
        
        User existingUser = UserDatabase.getUserByUsername(username);
        if (existingUser != null) {
            return new PasswordValidator.ValidationResult(false, "用户名已存在");
        }
        
        UserDatabase.insertUser(username, surname, password);
        return new PasswordValidator.ValidationResult(true, "注册成功");
    }

    public String login(String username, String password, String captcha, HttpSession session, Model model){
        String sessionId = session.getId();
        
        boolean requiresCaptcha = LoginAttemptService.requiresCaptcha(username);
        
        if (requiresCaptcha) {
            if (captcha == null || captcha.trim().isEmpty()) {
                model.addAttribute("response", "请输入验证码");
                model.addAttribute("showCaptcha", true);
                return "login";
            }
            
            if (!CaptchaService.validateCaptcha(sessionId, captcha)) {
                model.addAttribute("response", "验证码错误，请重新输入");
                model.addAttribute("showCaptcha", true);
                return "login";
            }
        }
        
        User user = UserDatabase.getUserByUsername(username);
        boolean loginSuccess = false;
        
        if (user != null && user.getUsername().equals(username) && PasswordEncoder.matches(password, user.getPassword())) {
            loginSuccess = true;
        }
        
        if (loginSuccess) {
            LoginAttemptService.loginSucceeded(username);
            session.setAttribute("currentUser", user);
            model.addAttribute("user", user);
            return "dashboard";
        } else {
            LoginAttemptService.loginFailed(username);
            boolean shouldShowCaptcha = LoginAttemptService.requiresCaptcha(username);
            int remainingAttempts = LoginAttemptService.getRemainingAttempts(username);
            
            model.addAttribute("response", ERROR_MESSAGE);
            model.addAttribute("showCaptcha", shouldShowCaptcha);
            
            if (!shouldShowCaptcha && remainingAttempts > 0 && remainingAttempts <= 2) {
                model.addAttribute("remainingAttempts", remainingAttempts);
            }
            
            return "login";
        }
    }
}
