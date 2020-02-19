package com.revolut;

import com.revolut.dto.Transfer;
import com.revolut.model.Account;
import com.revolut.util.TransferState;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.Optional;

import static javax.ws.rs.client.Entity.json;
import static org.assertj.core.api.Assertions.assertThat;

public class ApplicationIntegrationTest {

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
    void createAccountAndFindById() {

        final Account account = new Account("test", BigDecimal.valueOf(2000.20));

        final Account savedAccount = target.path("/account/create").request().post(json(account), Account.class);
        assertThat(savedAccount).isNotNull();
        assertThat(((Account) savedAccount).getBalance()).isEqualTo(BigDecimal.valueOf(2000.20).setScale(2));

        final Account fetchedAccount = target.path("/account/get/{id}").resolveTemplate("id", savedAccount.getId()).request().get(Account.class);
        assertThat(fetchedAccount).isEqualTo(savedAccount);
    }

    @Test
    public void accountNotFound() {
        final Response response = target.path("/account/get/{id}").resolveTemplate("id", 100).request().get();
        assertThat(response.getStatus()).isEqualTo(204);
    }

    @Test
    public void transferFailedNoToAccount() {

        final Account account = new Account("test123", BigDecimal.TEN);
        final Account savedAccount = target.path("/account/create").request().post(json(account), Account.class);

        Transfer transfer = new Transfer();
        transfer.setFromAccount(1);
        transfer.setToAccount(2);
        transfer.setAmount(BigDecimal.valueOf(200.00));
        final Transfer response = target.path("/account/transfer").request().post(json(transfer), Transfer.class);
        assertThat(response.getReason()).isEqualTo(TransferState.FAILED_ACCOUNT_TO);
    }

    @Test
    void transferFailedAmountIsNegative() {

        Account from = new Account("test1", BigDecimal.ONE);
        Account to = new Account("test2", BigDecimal.ONE);
        from = target.path("/account/create").request().post(json(from), Account.class);
        to = target.path("/account/create").request().post(json(to), Account.class);

        final Transfer transfer = new Transfer();
        transfer.setFromAccount(from.getId());
        transfer.setToAccount(to.getId());
        transfer.setAmount(new BigDecimal("-100.50"));
        Transfer response = target.path("/account/transfer").request().post(json(transfer), Transfer.class);
        assertThat(response.getReason()).isEqualTo(TransferState.NEGATIVE_AMOUNT);
    }

    @Test
    void failInsufficientFunds() throws InterruptedException {

        Account from = new Account("test1", BigDecimal.ONE);
        Account to = new Account("test2", BigDecimal.ONE);
        from = target.path("/account/create").request().post(json(from), Account.class);
        to = target.path("/account/create").request().post(json(to), Account.class);

        final Transfer transfer = new Transfer();
        transfer.setFromAccount(from.getId());
        transfer.setToAccount(to.getId());
        transfer.setAmount(new BigDecimal("100.50"));
        Transfer response = target.path("/account/transfer").request().post(json(transfer), Transfer.class);

        assertThat(response.getReason()).isEqualTo(TransferState.FAILED_INSUFFICIENT_BALANCE);
    }

    @Test
    void successfulFundsTransfer() throws InterruptedException {

        Account from = new Account("test1", BigDecimal.valueOf(100.50));
        Account to = new Account("test2", BigDecimal.valueOf(200.75));
        from = target.path("/account/create").request().post(json(from), Account.class);
        to = target.path("/account/create").request().post(json(to), Account.class);

        final Transfer transfer = new Transfer();
        transfer.setFromAccount(from.getId());
        transfer.setToAccount(to.getId());
        transfer.setAmount(new BigDecimal("10.00"));
        Transfer response = target.path("/account/transfer").request().post(json(transfer), Transfer.class);

        assertThat(response.getReason()).isEqualTo(TransferState.SUCCESS);

        final Account fetchedFromAccount = target.path("/account/get/{id}").resolveTemplate("id", transfer.getFromAccount()).request().get(Account.class);
        assertThat(fetchedFromAccount.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(90.50));

        final Account fetchedToAccount = target.path("/account/get/{id}").resolveTemplate("id", transfer.getToAccount()).request().get(Account.class);
        assertThat(fetchedToAccount.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(210.75));
    }
}
