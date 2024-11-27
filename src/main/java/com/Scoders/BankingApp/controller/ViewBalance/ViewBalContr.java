package com.Scoders.BankingApp.controller.ViewBalance;

import com.Scoders.BankingApp.database.AccountDatabase;
import com.Scoders.BankingApp.model.Account;
import com.Scoders.BankingApp.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ViewBalContr {

    AccountDatabase database = new AccountDatabase();

    @GetMapping("/ViewBal")
    public String index(Model model, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");

        if (user == null) {
            return "login";
        }

        List<Account> accounts = AccountDatabase.getAccountByUserId(user);
        model.addAttribute("Ngwane", "View Money Site");
        model.addAttribute("accounts", accounts); // Pass accounts to the view
        model.addAttribute("user", user);
        return "ViewBal"; // Return the Thymeleaf template name
    }

    @PostMapping("/ViewBal")
    public String viewBalance(Model model, @RequestParam("test") String data) {
        // Handle POST request (e.g., filter accounts or update view)
        model.addAttribute("message", "You submitted: " + data);
        return "ViewBal"; // Redirect or render the same page with changes
    }


}
