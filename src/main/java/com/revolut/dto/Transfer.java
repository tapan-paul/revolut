package com.revolut.dto;

import com.revolut.util.TransferState;

import javax.json.bind.annotation.JsonbProperty;
import java.math.BigDecimal;

public class Transfer {
    @JsonbProperty
    private Integer fromAccount;
    @JsonbProperty
    private Integer toAccount;
    @JsonbProperty
    private BigDecimal amount;

    private TransferState transferState;

    public Integer getFromAccount() {
        return fromAccount;
    }

    public void setFromAccount(Integer fromAccount) {
        this.fromAccount = fromAccount;
    }

    public Integer getToAccount() {
        return toAccount;
    }

    public void setToAccount(Integer toAccount) {
        this.toAccount = toAccount;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setReason(TransferState transferState) {
        this.transferState = transferState;
    }

    public TransferState getReason() {
        return transferState;
    }

    @Override
    public String toString() {
        return "Transfer{" +
                "fromAccount=" + fromAccount +
                ", toAccount=" + toAccount +
                ", amount=" + amount +
                '}';
    }
}
