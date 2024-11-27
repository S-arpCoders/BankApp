package com.Scoders.BankingApp.controller.auth;

import com.Scoders.BankingApp.database.UserDatabase;
import com.Scoders.BankingApp.model.User;
import jakarta.servlet.http.HttpSession;

public class Authentication {

    public boolean register( String username,String surname, String password){
        UserDatabase.insertUser(username,surname,password);

        return true;
    }

    public boolean login(String username, String password, HttpSession session){
        User user = UserDatabase.getUserByUsername(username);

        if (user.getUsername().equals(username) && user.getPassword().equals(password)){
            session.setAttribute("currentUser",user); //cookies

            return true;
        }else {
            return false;
        }
    }
}
