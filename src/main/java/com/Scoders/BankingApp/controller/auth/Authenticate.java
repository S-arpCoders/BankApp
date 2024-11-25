package com.Scoders.BankingApp.controller.auth;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Authenticate {

@GetMapping ("/")
    public String index(){
    return "index"; // Render index.html
}
@GetMapping("/register")
    public String register(){
    return "register";
}
@GetMapping("/login")
    public String login(){
    return "login";
}
@GetMapping("/surname")
    public String surname(){
        return "surname";
    }

}
