package Accounts;

import com.Scoders.BankingApp.database.AccountDatabase;
import com.Scoders.BankingApp.database.UserDatabase;
import com.Scoders.BankingApp.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/dashboard")
public class AccountController {

    @GetMapping
    public String viewAccounts(@RequestParam Long userId, Model model) {
        User user = UserDatabase.getUserById(userId);
        if (user == null) {
            return "error"; // Redirect to an error page if the user is not found
        }


        var accounts = AccountDatabase.getAccountByAccNo(userId);
        model.addAttribute("user", user);
        model.addAttribute("accounts", accounts);

        return "dashboard"; // Thymeleaf or JSP template for the accounts dashboard
    }

    @GetMapping("/create")
    public String createAccForm(Model model) {
        model.addAttribute("accountTypes", List.of("Savings", "Current")); // Example account types
        return "createAccount"; // Thymeleaf or JSP template for the account creation form
    }

    @PostMapping("/create")
    public String createAccount(
            @RequestParam Long userId,
            @RequestParam String accountType,
            Model model
    ) {
        User user = UserDatabase.getUserById(userId);
        if (user == null) {
            model.addAttribute("errorMessage", "User not found!");
            return "error"; // Redirect to an error page with an appropriate message
        }

        // Create a new account and insert into the database
        AccountDatabase.insertAccount(userId,0.0); // userId, accountType, 0.0 Assuming accountType is saved in the database

        return "redirect:/dashboard?userId=" + userId; // Redirect back to the accounts dashboard
    }
}
