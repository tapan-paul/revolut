package com.revolut.rest.controller;

import com.revolut.dto.AccountDTO;
import com.revolut.model.Account;
import com.revolut.service.AccountService;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Optional;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/account")
public class AccountResource {

    private AccountService accountService;

    public AccountResource() {
        accountService = new AccountService();
    }

    @POST
    @Path("/create")
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    public Response createAccount(AccountDTO account) {

        if (account == null) {
            throw new BadRequestException();
        }
        accountService.createAccount(account);
        return Response.created(URI.create("/account/get/" + account.getId())).entity(account.getId()).build();
    }

    @GET
    @Path("/get/{id}")
    @Produces(APPLICATION_JSON)
    public Response findAccountById(@PathParam("id") int id) {

        Optional<Account> account = accountService.findAccountById(id);
        if (account.isPresent()) {
            return Response.ok(account.get()).build();
        }
        throw new NotFoundException();
    }

    @POST
    @Consumes(APPLICATION_JSON)
    @Produces(APPLICATION_JSON)
    @Path("/{fromAccount}/transfer/{toAccount}/{amount}")
    public Response transfer(AccountDTO fromAccount, AccountDTO toAccount, BigDecimal amount) {

        if (fromAccount == null || toAccount == null) {
            throw new BadRequestException();
        }
        if (!accountService.makeTransfer(fromAccount, toAccount, amount)) {
            return Response.status(Response.Status.NOT_IMPLEMENTED).build();
        }
        return Response.ok().build();
    }

}
