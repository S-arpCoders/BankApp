package com.Scoders.BankingApp.controller.withdrawal;

import com.Scoders.BankingApp.database.AccountDatabase;
import com.Scoders.BankingApp.database.TransactionDatabase;
import com.Scoders.BankingApp.model.Account;

public class withdrawal {

    public withdrawal(Account account, Double balance){

        double newBalance = account.getBalance()-balance;

        AccountDatabase.updateBalance(account.getAccNo(),newBalance );
       // TransactionDatabase.insertTransaction(account.getAccNo(),balance,"Withdrawal");
    }
}
