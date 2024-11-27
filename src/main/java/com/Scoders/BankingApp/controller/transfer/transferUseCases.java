package com.Scoders.BankingApp.controller.transfer;

import com.Scoders.BankingApp.database.AccountDatabase;
import com.Scoders.BankingApp.database.TransactionDatabase;
import com.Scoders.BankingApp.model.Account;
import com.Scoders.BankingApp.model.User;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class transferUseCases {


    public String transferFunds(User user, long fromAccount, long toAccount, double amount, Model model) {
        Account sender = AccountDatabase.getAccountByAccNo(fromAccount);
        Account receiver = AccountDatabase.getAccountByAccNo(toAccount);

        if (sender == null || receiver == null) {
            model.addAttribute("response","One or both accounts do not exist.");
            return "status";
        }

        if (sender.getBalance() < amount) {
            model.addAttribute("response","Insufficient funds in the sender's account.");
            return"status";
        }

        // Perform transfer
        //From
        AccountDatabase.updateBalance(fromAccount, sender.getBalance() - amount);
        //TransactionDatabase.insertTransaction(sender.getAccNo(),amount,"Transfer-send");

        //To
        AccountDatabase.updateBalance(toAccount, receiver.getBalance() + amount);
        //TransactionDatabase.insertTransaction(receiver.getAccNo(),amount,"Transfer-receive");

        model.addAttribute("response","Transfer successful.");
        return "status";
    }

}
