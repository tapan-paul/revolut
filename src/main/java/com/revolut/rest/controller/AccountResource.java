package com.revolut.rest.controller;

import com.revolut.TransferState;
import com.revolut.model.Account;
import com.revolut.service.AccountService;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Optional;
import java.util.logging.Logger;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/account")
@Produces(APPLICATION_JSON)
public class AccountResource {

    private AccountService accountService;
    private Logger logger = Logger.getLogger(String.valueOf(AccountResource.class));

    public AccountResource() {
        accountService = new AccountService();
    }

    @POST
    @Path("/create")
    @Consumes(APPLICATION_JSON)
    public Response createAccount(Account account) {

        try {
            validate(account);
            accountService.createAccount(account);
            Integer id = account.getId();
            return Response.created(URI.create("/account/get" + id)).entity(account).build();
        } catch (Exception e) {
            logger.warning(e.toString());
            return Response.noContent().entity("Exception in adding account: " + e.getMessage()).build();
        }
    }

    @GET
    @Path("/get/{id}")
    public Response findAccountById(@PathParam("id") int id) {

        try {
            Optional<Account> account = accountService.findAccountById(id);
            if (account.isPresent()) {
                logger.info("Account Found with id: " + id);
                return Response.ok().entity(account.get()).build();
            } else {
                logger.warning("Could not find account with id: " + id);
                return Response.noContent().entity("Account with given id is not found").build();
            }
        } catch (Exception e) {
                logger.warning(e.toString());
                return Response.noContent()
                        .entity("Exception in fetching the account with given id: " + e.getMessage()).build();
        }
    }

    @POST
    @Consumes(APPLICATION_JSON)
    @Path("/{fromAccount}/transfer/{toAccount}/{amount}")
    public Response transfer(@PathParam("fromAccount") int fromAccount, @PathParam("toAccount") int toAccount, @PathParam("amount") BigDecimal amount) {

        Response from = findAccountById(fromAccount);
        Response to = findAccountById(toAccount);

        TransferState transferState = accountService.makeTransfer((Account) from.getEntity(), (Account) to.getEntity(), amount);
        if (transferState != TransferState.SUCCESS) {
            final String message = "Transfer failed" + fromAccount + " to " + toAccount + " for amount" + amount + " reason " + transferState;
            logger.warning(message);
            return Response.noContent().entity(message).build();
        }
        return Response.ok().entity(transferState).build();
    }

    private void validate(Account account) {
        if (account == null) {
            throw new BadRequestException();
        }
    }
}
