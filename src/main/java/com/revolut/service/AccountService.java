package com.revolut.service;

import com.revolut.TransferState;
import com.revolut.model.Account;
import com.revolut.repository.AccountRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static com.revolut.TransferState.*;

/**
 * service layer used to create/find {@link Account}
 */
public class AccountService {

    private final static AtomicInteger idCounter = new AtomicInteger();
    private AccountRepository repository;

    public AccountService() {
        repository = new AccountRepository();
    }

    public void createAccount(Account account) {
        account.setId(idCounter.incrementAndGet());
        List<Account> list = new ArrayList();
        list.add(account);
        repository.save(list);
    }

    public Optional<Account> findAccountById(Integer id) {
        Account accountById = repository.findAccountById(id);
        if (accountById != null) {
            return Optional.of(accountById);
        }
        return Optional.empty();
    }

    public TransferState makeTransfer(Account fromAccount, Account toAccount, BigDecimal amount) {

        if (lessThanZero(amount)){
            return NEGATIVE_AMOUNT;
        }
        if (fromAccount == null) {
            return FAILED_ACCOUNT_FROM;
        }
        if (toAccount == null) {
            return FAILED_ACCOUNT_TO;
        }

        Account from = fromAccount;
        Account to = toAccount;

        if (to.getId() < from.getId()) {
            from = toAccount;
            to = fromAccount;
        }

        synchronized (from) {
            synchronized (to) {
                BigDecimal newFromBalance = fromAccount.getBalance().subtract(amount);
                if (lessThanZero(newFromBalance)) {
                    return FAILED_INSUFFICIENT_BALANCE;
                }
                fromAccount.setBalance(newFromBalance);
                toAccount.setBalance(toAccount.getBalance().add(amount));
                try {
                    List list = new ArrayList();
                    list.add(from);
                    list.add(to);
                    repository.save(list);
                } catch (Exception e) {
                    return DB_ERROR;
                }
                return SUCCESS;
            }
        }

    }

    private boolean lessThanZero(BigDecimal newFromBalance) {
        return newFromBalance.compareTo(BigDecimal.ZERO) == -1;
    }


}
