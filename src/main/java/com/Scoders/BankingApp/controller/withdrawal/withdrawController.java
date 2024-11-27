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
        User user = (User) session.getAttribute("currentUser");

       // Check if user is null to avoid NullPointerException
       if (user == null) {

          return "login ";
       }

        // Add a message to the model
        model.addAttribute("message", "How much do you want to withdraw today?");

        // Retrieve the user's accounts (assuming a service method exists)
        List<Account> accounts = AccountDatabase.getAccountByUserId(user);

        // Add user and account data to the model
        model.addAttribute("user", user);
        model.addAttribute("accounts", accounts);
        return "withdraw";
    }

    // Handle the withdrawal process
    @PostMapping("/withdraw")
    public String withdraw(
            @RequestParam("toAccount") long accountNo,
            @RequestParam("balance") double balance,
            Model model)
     {

         Account account = AccountDatabase.getAccountByAccNo(accountNo);
         try {

             if (balance < 10 && balance < account.getBalance()) {
                 throw new IllegalArgumentException("Minimum withdrawal amount is R10.");
             }

//            // Withdraw the amount
//            withdrawController account = null;
//            account.withdraw(fromAccount, balance);

             new withdrawal(account, balance);


             model.addAttribute("message", "Successfully withdrew R" + String.format("%.2f", balance));
             return "success"; // Redirect to a success page
         } finally {
             return "status";
         }


    }

    private void withdraw(long fromAccount, long balance) {
    }
}
