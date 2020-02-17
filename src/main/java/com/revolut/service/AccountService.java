package com.revolut.service;

import com.revolut.exception.InSufficentFundsException;
import com.revolut.exception.NoSuchAccountException;
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

    private final static Map<Integer, Account> accounts = new ConcurrentHashMap<>();
    private final static AtomicInteger idCounter = new AtomicInteger();
    private AccountRepository repository;

    public AccountService() {
        repository = new AccountRepository();
    }

    public void createAccount(Account account) {
        account.setId(idCounter.incrementAndGet());
        accounts.put(account.getId(), account);
        repository.save(account);
    }

    public Optional<Account> findAccountById(Integer id) {
        if (accounts.containsKey(id)) {
            return Optional.of(accounts.get(id));
        }
        return Optional.empty();
    }

    public boolean makeTransfer(Integer fromAccount, Integer toAccount, BigDecimal amount) {

        if (amount.compareTo(BigDecimal.ZERO) == -1 ||
                !accounts.containsKey(fromAccount) || !accounts.containsKey(toAccount)) {
            return false;
        }

        Account from = accounts.get(fromAccount);
        Account to = accounts.get(toAccount);

        if (to.getId() < from.getId()) {
            from = accounts.get(toAccount);
            to = accounts.get(fromAccount);
        }

        synchronized (from) {
            synchronized (to) {
                BigDecimal newFromBalance = from.getBalance().subtract(amount);
                if (newFromBalance.compareTo(BigDecimal.ZERO) == -1) {
                    return false;
                }
                from.setBalance(newFromBalance);
                to.setBalance(to.getBalance().add(amount));
                repository.save(from);
                repository.save(to);
                return true;
            }
        }

    }


}
