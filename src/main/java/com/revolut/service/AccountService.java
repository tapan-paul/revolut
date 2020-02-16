package com.revolut.service;

import com.revolut.dto.AccountDTO;
import com.revolut.model.Account;
import com.revolut.repository.AccountRepository;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * service layer used to create/find {@link Account}
 */
public class AccountService {

    private final Map<Integer, Account> accounts = new ConcurrentHashMap<>();
    private AtomicInteger idCounter = new AtomicInteger();
    private @Inject AccountRepository repository;

    public void createAccount(AccountDTO accountDTO) {

        if (accountDTO == null) {
            throw new IllegalArgumentException();
        }
        accountDTO.setId(idCounter.incrementAndGet());
        Account account = new Account(BigDecimal.valueOf(accountDTO.getGetBalance()),accountDTO.getName());
        accounts.put(account.getId(), account);
        repository.save(account);
    }

    public Optional<Account> findAccountById(Integer id) {

        if (accounts.containsKey(id)) {
            return Optional.of(accounts.get(id));
        }
        return Optional.empty();
    }

}
