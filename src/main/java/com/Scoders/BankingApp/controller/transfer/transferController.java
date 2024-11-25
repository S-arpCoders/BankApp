package com.Scoders.BankingApp.controller.transfer;

import com.Scoders.BankingApp.model.Account;
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
    public String transferForm(
//            @RequestParam("user") long currentUser,
            Model model) {


        List<Account> accounts = use.getUsersAccounts(13453);
        model.addAttribute("accounts", accounts);
        return "transfer";
    }

    @PostMapping("/transfer")
    public String transfer(@RequestParam("fromAccount") long fromAccount,
                           @RequestParam("toAccount") long toAccount,
                           @RequestParam("amount") double amount,
                           Model model) {


        return "status";
    }

}
