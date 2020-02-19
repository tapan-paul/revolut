package com.revolut;

import com.revolut.dto.Transfer;
import com.revolut.model.Account;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import static javax.ws.rs.client.Entity.json;
import static org.assertj.core.api.Assertions.assertThat;

public class MoneyTransferLoadTest {

    private static final String LOCAL_HOST = "http://localhost:8050";
    private static ApplicationServer server;
    private static Client client;
    private static WebTarget target;

    @BeforeAll
    static void initApp() throws Exception {

        server = ApplicationServer.getInstance(Optional.of("8050"));
        server.start();

        client = ClientBuilder.newClient();
        target = client.target(LOCAL_HOST);
    }

    @AfterAll
    static void destroyApp() {
        client.close();
        server.shutDown();
    }

    @Test
    public void performLoadTestingFundTransfers() throws InterruptedException {

        final Account account1 = target.path("/account/create").request().post(json(new Account("account1", BigDecimal.valueOf(1.11))), Account.class);
        final Account account2 = target.path("/account/create").request().post(json(new Account("account2", BigDecimal.valueOf(22.22))), Account.class);
        final Account account3 = target.path("/account/create").request().post(json(new Account("account3", BigDecimal.valueOf(333.33))), Account.class);
        final Account account4 = target.path("/account/create").request().post(json(new Account("account4", BigDecimal.valueOf(4444.44))), Account.class);
        final Account account5 = target.path("/account/create").request().post(json(new Account("account5", BigDecimal.valueOf(55555.55))), Account.class);

        CyclicBarrier barrier = new CyclicBarrier(500);
        for (int i = 0; i < 10; i++) {
            performTransfer(barrier, account1, account2);
            performTransfer(barrier, account1, account3);
            performTransfer(barrier, account1, account4);
            performTransfer(barrier, account1, account5);
            performTransfer(barrier, account2, account1);
            performTransfer(barrier, account2, account3);
            performTransfer(barrier, account2, account4);
            performTransfer(barrier, account2, account5);
            performTransfer(barrier, account3, account1);
            performTransfer(barrier, account3, account2);
            performTransfer(barrier, account3, account4);
            performTransfer(barrier, account3, account5);
            performTransfer(barrier, account4, account1);
            performTransfer(barrier, account4, account2);
            performTransfer(barrier, account4, account3);
            performTransfer(barrier, account4, account5);
            performTransfer(barrier, account5, account1);
            performTransfer(barrier, account5, account2);
            performTransfer(barrier, account5, account3);
            performTransfer(barrier, account5, account4);

        }

        Thread.sleep(5000);

        final Account fetchedAccount1 = target.path("/account/get/{id}").resolveTemplate("id", account1.getId()).request().get(Account.class);
        final Account fetchedAccount2 = target.path("/account/get/{id}").resolveTemplate("id", account2.getId()).request().get(Account.class);
        final Account fetchedAccount3 = target.path("/account/get/{id}").resolveTemplate("id", account3.getId()).request().get(Account.class);
        final Account fetchedAccount4 = target.path("/account/get/{id}").resolveTemplate("id", account4.getId()).request().get(Account.class);
        final Account fetchedAccount5 = target.path("/account/get/{id}").resolveTemplate("id", account5.getId()).request().get(Account.class);

        assertThat(fetchedAccount1.getBalance()).isEqualTo(BigDecimal.valueOf(1.11));
        assertThat(fetchedAccount2.getBalance()).isEqualTo(BigDecimal.valueOf(22.22));
        assertThat(fetchedAccount3.getBalance()).isEqualTo(BigDecimal.valueOf(333.33));
        assertThat(fetchedAccount4.getBalance()).isEqualTo(BigDecimal.valueOf(4444.44));
        assertThat(fetchedAccount5.getBalance()).isEqualTo(BigDecimal.valueOf(55555.55));

    }

    private void performTransfer(CyclicBarrier barrier, Account from, Account to) {
        final Thread thread = new Thread(() -> {
            try {

                barrier.await();

                while (true) {
                    try {
                        final Transfer transfer = new Transfer();
                        transfer.setFromAccount(from.getId());
                        transfer.setToAccount(to.getId());
                        transfer.setAmount(new BigDecimal("10.50"));
                        target.path("/account/transfer").request().post(json(transfer), Transfer.class);

                        return;
                    } catch (Exception e) {

                    }
                }

            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        });
        thread.setDaemon(true);
        thread.start();
    }
}
