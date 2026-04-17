package com.Scoders.BankingApp.controller.transaction;

import com.Scoders.BankingApp.database.TransactionDatabase;
import com.Scoders.BankingApp.model.User;
import com.Scoders.BankingApp.model.transaction;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class TransactionController {

    @GetMapping("/transactions")
    public String showTransactions(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        
        if (user == null) {
            return "login";
        }
        
        List<transaction> transactions = TransactionDatabase.getTransactionsByUser(user);
        
        model.addAttribute("transactions", transactions);
        model.addAttribute("user", user);
        
        return "transactions";
    }
}
