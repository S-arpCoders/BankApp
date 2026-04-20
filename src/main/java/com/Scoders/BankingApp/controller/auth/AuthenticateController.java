package com.Scoders.BankingApp.controller.auth;

import com.Scoders.BankingApp.database.AccountDatabase;
import com.Scoders.BankingApp.database.SupportQuestionDatabase;
import com.Scoders.BankingApp.database.TransactionDatabase;
import com.Scoders.BankingApp.database.UserDatabase;
import com.Scoders.BankingApp.model.User;
import com.Scoders.BankingApp.security.PasswordValidator;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;


@Controller
public class AuthenticateController {

    Authentication auth = new Authentication();
@GetMapping ("/")
    public String index(HttpSession session)
{
    UserDatabase.createUserTable();
    AccountDatabase.createAccountTable();
    TransactionDatabase.createTransactionTable();
    SupportQuestionDatabase.createSupportQuestionTable();
    User user = (User) session.getAttribute("currentUser");

    if (user!= null){
        session.removeAttribute("currentUser");
    }
    return "index"; // Render index.html
}

@GetMapping("/register")
    public String register(){

    return "register";
}
@PostMapping("/register")
public String register(
        @RequestParam("username") String username,
        @RequestParam("surname") String surname,
        @RequestParam("password") String password,
        @RequestParam("confirmPassword") String confirmPassword,
        Model model
)
{
    if (!password.equals(confirmPassword)) {
        model.addAttribute("response", "Passwords do not match!");
        return "register";
    }

    PasswordValidator.ValidationResult response = auth.register(username,surname,password);
    if (response.isValid()) {
        return "login";
    } else {
        model.addAttribute("response", response.getMessage());
        return "register";
    }
}

@GetMapping("/login")
    public String login(){
    return "login";
}

@PostMapping("/login")
public String login(
        @RequestParam("username") String username,
        @RequestParam("password") String password,
        @RequestParam(value = "captcha", required = false) String captcha,
        Model model,
        HttpSession session

){

    return auth.login(username,password,captcha,session,model);
}
@GetMapping("/surname")
    public String surname(){
        return "surname";
    }

}

