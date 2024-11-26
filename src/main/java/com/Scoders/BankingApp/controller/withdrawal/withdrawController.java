package com.Scoders.BankingApp.controller.withdrawal;

import com.Scoders.BankingApp.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class withdrawController {

    // Display the withdrawal form
    @GetMapping("/withdraw")
    public String showWithdrawPage(Model model) {
        model.addAttribute("message", "How much do you want to withdraw today?");
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
