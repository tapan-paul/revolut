package com.revolut;

import com.revolut.model.Account;
import com.revolut.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AccountServiceTest {

    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountService = new AccountService();
    }

    @Test
    void getAccount() {
        final Account account = new Account();

//        when(cache.get(id)).thenReturn(account);
//        assertThat(accountService.findAccountById(id)).isSameAs(account);
    }

    @Test
    void createAccount() {

//        final Account account = new Account();
//        doNothing().when(cache).put(any(UUID.class), eq(account));
//
//        accountService.createAccount(account);
//
//        assertThat(account.getBalance()).isEqualTo(ZERO);
//        assertThat(account.getId()).isNotNull();
//
//        verify(cache, only()).put(account.getId(), account);

    }
}
