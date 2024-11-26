package com.Scoders.BankingApp.controller.auth;

import com.Scoders.BankingApp.database.AccountDatabase;
import com.Scoders.BankingApp.database.TransactionDatabase;
import com.Scoders.BankingApp.database.UserDatabase;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.ui.Model;


@Controller
public class AuthenticateController {

    Authentication auth = new Authentication();
@GetMapping ("/")
    public String index()
{
    UserDatabase.createUserTable();
    TransactionDatabase.createTransactionTable();
    AccountDatabase.createAccountTable();
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
        Model model
)
{

boolean response = auth.register(username,surname,password);

if (response){
    return "login";
}else {
    model.addAttribute("response", "You Are Registered Successfully!");
    return "status";
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
        Model model
){
    boolean response = auth.login(username,password);
    if (response){
        model.addAttribute("response", "You logged in Successfully!");

    }else {
        model.addAttribute("response", "Account not found! or incorrect credentials, go back and try again");

    }
    return "status";
}
@GetMapping("/surname")
    public String surname(){
        return "surname";
    }

}
