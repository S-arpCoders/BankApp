package com.Scoders.BankingApp.controller.transactionhistory;

import com.Scoders.BankingApp.database.TransactionDatabase;
import com.Scoders.BankingApp.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class TransactionController {

    // Display the transaction history page
    @GetMapping("/transactions")
    public String showTransactionHistory(HttpSession session, Model model) {
        // Retrieve the current user from the session
        User user = (User) session.getAttribute("currentUser");

        // Check if user is null to avoid NullPointerException
        if (user == null) {
            return "login";
        }

        // Get all transactions for the user
        List<TransactionDatabase.TransactionDTO> transactions = TransactionDatabase.getTransactionDTOsByUser(user);

        // Add user and transaction data to the model
        model.addAttribute("user", user);
        model.addAttribute("transactions", transactions);
        
        return "transactions";
    }
}
