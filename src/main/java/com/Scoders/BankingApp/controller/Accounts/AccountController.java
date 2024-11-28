package com.Scoders.BankingApp.controller.Accounts;

import com.Scoders.BankingApp.database.AccountDatabase;
import com.Scoders.BankingApp.database.UserDatabase;
import com.Scoders.BankingApp.model.Account;
import com.Scoders.BankingApp.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class AccountController {

    @GetMapping("/dashboard")
    public String viewAccounts(HttpSession session, Model model) {

        User user = (User) session.getAttribute("currentUser");

        if (user == null) {
            model.addAttribute("errorMessage", "User not found!");
            return "login";
        }else {
            model.addAttribute("user", user);
            System.out.println(user.getUsername());
            return "dashboard";
        }


    }

    @GetMapping("/status")
    public String status(){
        return "status";
    }
    @GetMapping("/create")
    public String createAccForm(Model model,HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        model.addAttribute("user", user);
        model.addAttribute("accountTypes", List.of("Savings", "Current"));
        if (user == null) {
            model.addAttribute("errorMessage", "User not found!");
            return "login";
        }
        return "createAcc";

    }

    @PostMapping("/create")
    public String createAccount(
            @RequestParam String accountType,
            HttpSession session,
            Model model
    ) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            model.addAttribute("errorMessage", "User not found!");
            return "login";
        }


        AccountDatabase.insertAccount(user.getId(),0.00);

        model.addAttribute("message","account created successful");
        model.addAttribute("user",user);
        return "dashboard";
    }
}
