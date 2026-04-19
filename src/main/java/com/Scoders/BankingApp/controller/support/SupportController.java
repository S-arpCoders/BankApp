package com.Scoders.BankingApp.controller.support;

import com.Scoders.BankingApp.database.SupportQuestionDatabase;
import com.Scoders.BankingApp.model.SupportQuestion;
import com.Scoders.BankingApp.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class SupportController {

    @GetMapping("/support")
    public String support(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        
        if (currentUser == null) {
            model.addAttribute("message", "请先登录后再联系客服");
            return "support-guest";
        }
        
        List<SupportQuestion> questions = SupportQuestionDatabase.getSupportQuestionsByUserId(currentUser.getId());
        model.addAttribute("questions", questions);
        model.addAttribute("user", currentUser);
        
        return "support";
    }

    @PostMapping("/support")
    public String submitQuestion(
            @RequestParam("question") String question,
            HttpSession session,
            Model model) {
        
        User currentUser = (User) session.getAttribute("currentUser");
        
        if (currentUser == null) {
            model.addAttribute("message", "请先登录后再联系客服");
            return "support-guest";
        }
        
        if (question != null && !question.trim().isEmpty()) {
            SupportQuestionDatabase.insertSupportQuestion(currentUser.getId(), question.trim());
            model.addAttribute("successMessage", "该问题已提交，请等待回复");
        }
        
        List<SupportQuestion> questions = SupportQuestionDatabase.getSupportQuestionsByUserId(currentUser.getId());
        model.addAttribute("questions", questions);
        model.addAttribute("user", currentUser);
        
        return "support";
    }
}
