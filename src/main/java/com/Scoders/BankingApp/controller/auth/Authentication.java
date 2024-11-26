package com.Scoders.BankingApp.controller.auth;

import com.Scoders.BankingApp.database.UserDatabase;
import com.Scoders.BankingApp.model.User;

public class Authentication {

    public boolean register( String username,String surname, String password){
        UserDatabase.insertUser(username,surname,password);

        return true;
    }

    public boolean login( String username,String password){
        User user = UserDatabase.getUserByUsername(username);

        if (user.getUsername().equals(username) && user.getPassword().equals(password)){
            return true;
        }else {
            return false;
        }
    }
}
