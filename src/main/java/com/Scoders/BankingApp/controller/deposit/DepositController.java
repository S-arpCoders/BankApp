package com.Scoders.BankingApp.controller.deposit;

import com.Scoders.BankingApp.database.AccountDatabase;
import com.Scoders.BankingApp.database.TransactionDatabase;
import com.Scoders.BankingApp.model.Account;
import com.Scoders.BankingApp.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class DepositController {

    // Serve the deposit page (deposit.html)
    @GetMapping("/deposit")
    public String showDepositForm() {
        return "deposit";  // This will render deposit.html from resources/static
    }

    // Handle the form submission (deposit money into the account)
    @PostMapping("/deposit")
    public String handleDeposit(@RequestParam Long accNo, @RequestParam Double amount, Model model, HttpSession session) {
        // First, fetch the account using the account number
        Account account = AccountDatabase.getAccountByAccNo(accNo);
        
        if (account == null) {
            model.addAttribute("response", "Account not found!");
            return "deposit";  // Show the deposit form with an error message
        }
        if (amount<10){
            model.addAttribute("response", "Deposit a minimum of R10");
            return "deposit";
        }

        // Calculate the new balance after deposit
        double newBalance = account.getBalance() + amount;

        // Update the balance in the database
        AccountDatabase.updateBalance(accNo, newBalance);
        TransactionDatabase.insertTransaction(accNo,amount,"Deposit");

        // Add a success message to the model and display the new balance
        model.addAttribute("response", "Deposit successful! New balance: " + newBalance);

        User user = (User) session.getAttribute("currentUser");

        if (user==null){
            return "deposit";
        }else {
            model.addAttribute("user",user);
            return "dashboard";  // Render the same deposit.html page with success message
        }


    }

    // Optionally, you can add a logout or home page here
    @GetMapping("/home")
    public String homePage() {
        return "home";  // Redirects to home page (you can create a home.html page for example)
    }
}
