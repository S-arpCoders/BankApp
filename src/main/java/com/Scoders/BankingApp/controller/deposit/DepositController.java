package com.Scoders.BankingApp.controller.deposit;

import com.Scoders.BankingApp.database.AccountDatabase;
import com.Scoders.BankingApp.model.Account;
import com.Scoders.BankingApp.model.User;
import com.Scoders.BankingApp.service.BankingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.sql.SQLException;
import java.util.List;

@Controller
public class DepositController {

    @Autowired
    private BankingService bankingService;

    @GetMapping("/deposit")
    public String showDepositForm(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        
        if (user == null) {
            model.addAttribute("errorMessage", "User not found!");
            return "login";
        }
        
        List<Account> accounts = AccountDatabase.getAccountByUserId(user);
        model.addAttribute("accounts", accounts);
        model.addAttribute("user", user);
        
        if (accounts.isEmpty()) {
            model.addAttribute("noAccounts", true);
        }
        
        return "deposit";
    }

    @PostMapping("/deposit")
    public String handleDeposit(@RequestParam Long accNo, @RequestParam Double amount, Model model, HttpSession session) {
        Account account = AccountDatabase.getAccountByAccNo(accNo);
        
        if (account == null) {
            model.addAttribute("response", "Account not found!");
            return "deposit";
        }
        if (amount < 10) {
            model.addAttribute("response", "Deposit a minimum of R10");
            return "deposit";
        }

        try {
            bankingService.deposit(accNo, amount);
            Account updatedAccount = AccountDatabase.getAccountByAccNo(accNo);
            model.addAttribute("response", "Deposit successful! New balance: " + updatedAccount.getBalance());
        } catch (SQLException e) {
            model.addAttribute("response", "Database error occurred: " + e.getMessage());
            return "deposit";
        }

        User user = (User) session.getAttribute("currentUser");

        if (user == null) {
            return "deposit";
        } else {
            model.addAttribute("user", user);
            return "dashboard";
        }
    }

    @GetMapping("/home")
    public String homePage() {
        return "home";
    }
}
