package com.Scoders.BankingApp.service;

import com.Scoders.BankingApp.database.TransactionalAccountHelper;
import com.Scoders.BankingApp.model.Account;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;

@Service
public class BankingService {

    private final TransactionalAccountHelper accountHelper;

    public BankingService(TransactionalAccountHelper accountHelper) {
        this.accountHelper = accountHelper;
    }

    @Transactional(rollbackFor = {SQLException.class, RuntimeException.class})
    public void transfer(long fromAccountNo, long toAccountNo, double amount) throws SQLException {
        Account sender = accountHelper.getAccountByAccNo(fromAccountNo);
        Account receiver = accountHelper.getAccountByAccNo(toAccountNo);

        if (sender == null || receiver == null) {
            throw new IllegalArgumentException("One or both accounts do not exist.");
        }

        if (sender.getBalance() < amount) {
            throw new IllegalArgumentException("Insufficient funds in the sender's account.");
        }

        if (amount < 1) {
            throw new IllegalArgumentException("Transfer a minimum of R1");
        }

        double newSenderBalance = sender.getBalance() - amount;
        accountHelper.updateBalance(fromAccountNo, newSenderBalance);
        accountHelper.insertTransaction(sender.getAccNo(), amount, "Transfer-send");

        double newReceiverBalance = receiver.getBalance() + amount;
        accountHelper.updateBalance(toAccountNo, newReceiverBalance);
        accountHelper.insertTransaction(receiver.getAccNo(), amount, "Transfer-receive");
    }

    @Transactional(rollbackFor = {SQLException.class, RuntimeException.class})
    public void deposit(long accountNo, double amount) throws SQLException {
        Account account = accountHelper.getAccountByAccNo(accountNo);

        if (account == null) {
            throw new IllegalArgumentException("Account not found!");
        }

        if (amount < 10) {
            throw new IllegalArgumentException("Deposit a minimum of R10");
        }

        double newBalance = account.getBalance() + amount;
        accountHelper.updateBalance(accountNo, newBalance);
        accountHelper.insertTransaction(accountNo, amount, "Deposit");
    }

    @Transactional(rollbackFor = {SQLException.class, RuntimeException.class})
    public void withdraw(long accountNo, double amount) throws SQLException {
        Account account = accountHelper.getAccountByAccNo(accountNo);

        if (account == null) {
            throw new IllegalArgumentException("Account not found.");
        }

        if (account.getBalance() == 0) {
            throw new IllegalArgumentException("Your account balance is R0.00. No withdrawal possible.");
        }

        if (account.getBalance() < amount) {
            throw new IllegalArgumentException("Insufficient funds in the account.");
        }

        if (amount < 10) {
            throw new IllegalArgumentException("Minimum withdrawal amount is R10.");
        }

        double newBalance = account.getBalance() - amount;
        accountHelper.updateBalance(accountNo, newBalance);
        accountHelper.insertTransaction(accountNo, amount, "withdrawal");
    }

    @Transactional(rollbackFor = {SQLException.class, SimulatedTransactionException.class})
    public void transferWithSimulatedFailure(long fromAccountNo, long toAccountNo, double amount) throws SQLException {
        Account sender = accountHelper.getAccountByAccNo(fromAccountNo);
        Account receiver = accountHelper.getAccountByAccNo(toAccountNo);

        if (sender == null || receiver == null) {
            throw new IllegalArgumentException("One or both accounts do not exist.");
        }

        if (amount < 1) {
            throw new IllegalArgumentException("Transfer a minimum of R1");
        }

        double newSenderBalance = sender.getBalance() - amount;
        accountHelper.updateBalance(fromAccountNo, newSenderBalance);
        accountHelper.insertTransaction(sender.getAccNo(), amount, "Transfer-send");

        throw new SimulatedTransactionException("Simulated database failure after debit");
    }

    @Transactional(rollbackFor = {SQLException.class, SimulatedTransactionException.class})
    public void depositWithSimulatedFailure(long accountNo, double amount) throws SQLException {
        Account account = accountHelper.getAccountByAccNo(accountNo);

        if (account == null) {
            throw new IllegalArgumentException("Account not found!");
        }

        if (amount < 10) {
            throw new IllegalArgumentException("Deposit a minimum of R10");
        }

        double newBalance = account.getBalance() + amount;
        accountHelper.updateBalance(accountNo, newBalance);

        throw new SimulatedTransactionException("Simulated database failure after balance update");
    }

    public TransactionalAccountHelper getAccountHelper() {
        return accountHelper;
    }

    public static class SimulatedTransactionException extends RuntimeException {
        public SimulatedTransactionException(String message) {
            super(message);
        }
    }
}
