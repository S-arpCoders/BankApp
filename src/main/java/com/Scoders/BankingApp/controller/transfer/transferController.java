package com.Scoders.BankingApp.controller.transfer;

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
public class transferController {

    @Autowired
    private BankingService bankingService;

    @GetMapping("/transfer")
    public String transferForm(HttpSession session,
                               Model model) {

        User user = (User) session.getAttribute("currentUser");

        if (user==null){
            return "login";
        }
        List<Account> accounts = AccountDatabase.getAccountByUserId(user);
        List<Account> allOtherAccounts = AccountDatabase.getAllAccountsExceptUser(user);
        model.addAttribute("accounts", accounts);
        model.addAttribute("allOtherAccounts", allOtherAccounts);
        return "transfer";
    }

    @PostMapping("/transfer")
    public String transfer(HttpSession session,
            @RequestParam("fromAccount") long fromAccount,
                           @RequestParam("toAccount") long toAccount,
                           @RequestParam("amount") double amount,
                           Model model) {
        User user = (User) session.getAttribute("currentUser");

        List<Account> accounts = AccountDatabase.getAccountByUserId(user);
        List<Account> allOtherAccounts = AccountDatabase.getAllAccountsExceptUser(user);
        model.addAttribute("accounts", accounts);
        model.addAttribute("allOtherAccounts", allOtherAccounts);

        Account sender = AccountDatabase.getAccountByAccNo(fromAccount);
        Account receiver = AccountDatabase.getAccountByAccNo(toAccount);

        if (sender == null || receiver == null) {
            model.addAttribute("response","One or both accounts do not exist.");
            return "transfer";
        }

        if (sender.getBalance() < amount) {
            model.addAttribute("response","Insufficient funds in the sender's account.");
            return "transfer";
        }
        if (amount < 1) {
            model.addAttribute("response","Transfer a minimum of R1");
            return "transfer";
        }

        try {
            bankingService.transfer(fromAccount, toAccount, amount);
            model.addAttribute("response","Transfer successful.");
            model.addAttribute("user",user);
            return "dashboard";
        } catch (SQLException e) {
            model.addAttribute("response","Database error occurred: " + e.getMessage());
            return "transfer";
        }
    }
}
