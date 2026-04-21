package com.Scoders.BankingApp.controller.withdrawal;

import com.Scoders.BankingApp.database.AccountDatabase;
import com.Scoders.BankingApp.model.Account;
import com.Scoders.BankingApp.model.User;
import com.Scoders.BankingApp.service.BankingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.SQLException;
import java.util.List;

@Controller
public class withdrawController {

    @Autowired
    private BankingService bankingService;

    @GetMapping("/withdraw")
    public String showWithdrawPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");

        if (user == null) {
            return "login ";
        }

        model.addAttribute("message", "How much do you want to withdraw today?");

        List<Account> accounts = AccountDatabase.getAccountByUserId(user);

        model.addAttribute("user", user);
        model.addAttribute("accounts", accounts);
        return "withdraw";
    }

    @PostMapping("/withdraw")
    public String withdraw(HttpSession session,
                           @RequestParam(value = "toAccount", required = false) Long accountNo,
                           @RequestParam("balance") double balance,
                           Model model) {

        User user = (User) session.getAttribute("currentUser");
        List<Account> accounts = AccountDatabase.getAccountByUserId(user);

        model.addAttribute("user", user);
        model.addAttribute("accounts", accounts);

        if (accountNo == null) {
            model.addAttribute("response", "Please select an account first.");
            model.addAttribute("isError", true);
            return "withdraw";
        }

        Account account = AccountDatabase.getAccountByAccNo(accountNo);

        if (account == null) {
            model.addAttribute("response", "Account not found.");
            model.addAttribute("isError", true);
            return "withdraw";
        }

        if (account.getBalance() == 0) {
            model.addAttribute("response", "Your account balance is R0.00. No withdrawal possible.");
            model.addAttribute("isError", true);
            return "withdraw";
        }

        if (account.getBalance() < balance) {
            model.addAttribute("response", "Insufficient funds in the account.");
            model.addAttribute("isError", true);
            return "withdraw";
        }
        if (balance < 10) {
            model.addAttribute("response", "Minimum withdrawal amount is R10.");
            model.addAttribute("isError", true);
            return "withdraw";
        }

        try {
            bankingService.withdraw(accountNo, balance);
            model.addAttribute("response", "Successfully withdrew R" + String.format("%.2f", balance));
        } catch (SQLException e) {
            model.addAttribute("response", "Database error occurred: " + e.getMessage());
            model.addAttribute("isError", true);
            return "withdraw";
        }

        model.addAttribute("user", user);
        return "dashboard";
    }
}
