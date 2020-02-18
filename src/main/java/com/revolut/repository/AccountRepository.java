package com.revolut.repository;


import com.revolut.model.Account;
import com.revolut.model.DatabaseOperation;

import java.util.List;
import java.util.Optional;

public class AccountRepository {

    private DatabaseOperation<Account> manager = new DatabaseOperation<>();

    public Optional<List<Account>> save(List<Account> account) {
        List<Account> acct = manager.insert(account);
        return Optional.of(acct);
    }

    public Account findAccountById(Integer id) {
        Account account = manager.read(Account.class, id);
        return account;
    }

}
