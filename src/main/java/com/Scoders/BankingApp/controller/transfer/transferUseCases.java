package com.Scoders.BankingApp.controller.transfer;

import com.Scoders.BankingApp.database.AccountDatabase;
import com.Scoders.BankingApp.database.TransactionDatabase;
import com.Scoders.BankingApp.model.Account;

import java.util.ArrayList;
import java.util.List;

public class transferUseCases {

    public List<Account> getUsersAccounts(long userId) {
        List<Account> userAccounts = new ArrayList<>();

        List<Long> knownAccountIds = getAllKnownAccountIds();

        for (Long accNo : knownAccountIds) {
            Account account = AccountDatabase.getAccountByAccNo(accNo);
            if (account != null && account.getUser() != null && account.getUser().getId() == userId) {
                userAccounts.add(account);
            }
        }

        return userAccounts;
    }

    private List<Long> getAllKnownAccountIds() {

        return List.of(1L, 2L, 3L, 4L, 5L);
    }


    public String transferFunds(Long currentUserId,long fromAccount, long toAccount, double amount) {
        Account sender = AccountDatabase.getAccountByAccNo(fromAccount);
        Account receiver = AccountDatabase.getAccountByAccNo(toAccount);

        if (sender == null || receiver == null) {
            return "One or both accounts do not exist.";
        }

        if (sender.getUser() == null || sender.getUser().getId() != currentUserId) {
            return "You can only transfer from your own accounts.";
        }

        if (sender.getBalance() < amount) {
            return "Insufficient funds in the sender's account.";
        }

        // Perform transfer
        //From
        AccountDatabase.updateBalance(fromAccount, sender.getBalance() - amount);
        TransactionDatabase.insertTransaction(sender.getAccNo(),amount,"Transfer-send");

        //To
        AccountDatabase.updateBalance(toAccount, receiver.getBalance() + amount);
        TransactionDatabase.insertTransaction(receiver.getAccNo(),amount,"Transfer-receive");

        return "Transfer successful.";
    }

}
