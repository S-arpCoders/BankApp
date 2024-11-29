package com.Scoders.BankingApp.controller.auth;

import com.Scoders.BankingApp.database.UserDatabase;
import com.Scoders.BankingApp.model.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.ui.Model;

public class Authentication {

    public boolean register( String username,String surname, String password){
        UserDatabase.insertUser(username,surname,password);

        return true;
    }

    public String login(String username, String password, HttpSession session, Model model){
        User user = UserDatabase.getUserByUsername(username);

        if (user == null) {
            model.addAttribute("response", "Account not found! or incorrect credentials, go back and try again");

            return "login";

        } else if (user.getUsername().equals(username) && user.getPassword().equals(password)){
            session.setAttribute("currentUser",user); //cookies

            model.addAttribute("user",user);
            return "dashboard";
        }else {
            model.addAttribute("response", "Account not found! or incorrect credentials, go back and try again");

            return "login";
        }



    }
}
