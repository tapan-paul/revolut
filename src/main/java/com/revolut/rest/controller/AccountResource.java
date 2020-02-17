package com.revolut.rest.controller;

import com.revolut.model.Account;
import com.revolut.service.AccountService;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Optional;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/account")
@Produces(APPLICATION_JSON)
public class AccountResource {

    private AccountService accountService;

    public AccountResource() {
        accountService = new AccountService();
    }

    @POST
    @Path("/create")
    @Consumes(APPLICATION_JSON)
    public Response createAccount(Account account) {

        validate(account);
        accountService.createAccount(account);
        Integer id = account.getId();
        return Response.created(URI.create("/account/get"+ id)).build();
    }

    @GET
    @Path("/get/{id}")
    public Response findAccountById(@PathParam("id") int id) {

        Optional<Account> account = accountService.findAccountById(id);
        if (account.isPresent()) {
            return Response.ok(account.get()).build();
        }
        throw new NotFoundException();
    }

    @POST
    @Consumes(APPLICATION_JSON)
    @Path("/{fromAccount}/transfer/{toAccount}/{amount}")
    public Response transfer(@PathParam("fromAccount") int fromAccount, @PathParam("toAccount") int toAccount, @PathParam("amount") BigDecimal amount) {

        if (!accountService.makeTransfer(fromAccount, toAccount, amount)) {
            return Response.status(Response.Status.NOT_IMPLEMENTED).build();
        }
        return Response.ok().build();
    }

    private void validate(Account account) {
        if (account == null) {
            throw  new BadRequestException();
        }
    }
}
