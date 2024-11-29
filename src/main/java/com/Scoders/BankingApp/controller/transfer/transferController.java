package com.Scoders.BankingApp.controller.transfer;

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
public class transferController {
    transferUseCases use = new transferUseCases();
    @GetMapping("/transfer")
    public String transferForm(HttpSession session,
                               Model model) {

        User user = (User) session.getAttribute("currentUser");

        if (user==null){
            return "login";
        }
        List<Account> accounts = AccountDatabase.getAccountByUserId(user);
        model.addAttribute("accounts", accounts);
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
        model.addAttribute("accounts", accounts);

        return use.transferFunds(user,fromAccount, toAccount, amount,model);
    }




}
