package com.Scoders.BankingApp.controller.auth;

import com.Scoders.BankingApp.database.AccountDatabase;
import com.Scoders.BankingApp.database.TransactionDatabase;
import com.Scoders.BankingApp.database.UserDatabase;
import com.Scoders.BankingApp.model.User;
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
        Model model
)
{

boolean response = auth.register(username,surname,password);

    return "login";

}

@GetMapping("/login")
    public String login(){
    return "login";
}

@PostMapping("/login")
public String login(
        @RequestParam("username") String username,
        @RequestParam("password") String password,
        Model model,
        HttpSession session

){

    return auth.login(username,password,session,model);
}
@GetMapping("/surname")
    public String surname(){
        return "surname";
    }

}

