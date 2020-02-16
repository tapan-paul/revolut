package com.revolut.repository;


import com.revolut.model.Account;
import com.revolut.model.DatabaseOperation;

import java.util.Optional;


public class AccountRepository {

    private DatabaseOperation<Account> manager = new DatabaseOperation<>();

    public Optional<Account> save(Account account) {
        Account acct = manager.insert(account);
        return Optional.of(acct);
    }

}
