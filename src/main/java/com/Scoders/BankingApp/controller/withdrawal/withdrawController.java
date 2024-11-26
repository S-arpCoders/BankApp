package com.Scoders.BankingApp.controller.withdrawal;

import com.Scoders.BankingApp.database.AccountDatabase;
import com.Scoders.BankingApp.model.Account;
import com.Scoders.BankingApp.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;



@Controller
public class withdrawController {

    // Display the withdrawal form
    @GetMapping("/withdraw")
    public String showWithdrawPage(HttpSession session, Model model) {


        // Retrieve the current user from the session
        User user = (User) session.getAttribute("currentAccount");

        // Check if user is null to avoid NullPointerException
        if (user == null) {
            model.addAttribute("error", "No user is currently logged in.");
            return "error";
        }

        // Add a message to the model
        model.addAttribute("message", "How much do you want to withdraw today?");

        // Retrieve the user's accounts (assuming a service method exists)
        List<Account> accounts = (List<Account>) AccountDatabase.getAccountByAccNo(1l);

        // Add user and account data to the model
        model.addAttribute("user", user);
        model.addAttribute("accounts", accounts);
        return "withdraw";
    }

    // Handle the withdrawal process
    @PostMapping("/withdraw")
    public String withdraw(
            @RequestParam("user") long currentUser,
            @RequestParam("fromAccount") long fromAccount,
            @RequestParam("toAccount") long toAccount, 
            @RequestParam("balance") long balance,
            Model model)
     {
        try {
           
            if (balance < 10) {
                throw new IllegalArgumentException("Minimum withdrawal amount is R10.");
            }

            // Withdraw the amount
            withdrawController account = null;
            account.withdraw(fromAccount, balance);

            model.addAttribute("message", "Successfully withdrew R" + String.format("%.2f", balance));
            return "success"; // Redirect to a success page
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Error: " + e.getMessage());
        } catch (Exception e) {
            model.addAttribute("error", "An unexpected error occurred: " + e.getMessage());
        }

        return "withdraw";
    }

    private void withdraw(long fromAccount, long balance) {
    }
}
