package com.Scoders.BankingApp.repository;

import com.Scoders.BankingApp.model.Account;


public interface AccountRepository extends com.Scoders.BankingApp.repository.JpaRepository<Account, Long> {
    Account findByAccNo(Long accNo);
}
