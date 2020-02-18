package com.revolut;

import com.revolut.model.Account;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.math.BigDecimal;
import java.net.InetSocketAddress;
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

        Optional<InetSocketAddress> address = Optional.of(new InetSocketAddress(8050));
        server = ApplicationServer.getInstance(address);
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

        final Account account = new Account("test", BigDecimal.valueOf(2000.00));
        account.setBalance(BigDecimal.valueOf(2000.00));

        final Account savedAccount = target.path("/account/create").request().post(json(account), Account.class);
        assertThat(savedAccount).isNotNull();
        assertThat(((Account)savedAccount).getBalance()).isEqualTo(BigDecimal.valueOf(2000));

        final Account fetchedAccount = target.path("/account/get/{id}").resolveTemplate("id", savedAccount.getId()).request().get(Account.class);
        assertThat(fetchedAccount).isEqualTo(savedAccount);
    }




}
