package com.revolut.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbNumberFormat;
import javax.json.bind.annotation.JsonbProperty;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "Account")
@NamedQueries({
        @NamedQuery(name = "Account.findAccountById", query = "SELECT a FROM Account a WHERE a.id = :id")
})
public class Account {

    @Id
    @Column(nullable = false)
    private Integer id;

    @Column(nullable = false)
    @JsonbNumberFormat("#0.00")
    private BigDecimal balance;

    @Column(nullable = false)
    private String name;

    public Account() {
        this.balance = BigDecimal.ZERO;
    }

    @JsonbCreator
    public Account(@JsonbProperty("name") String name, @JsonbProperty("balance") BigDecimal initialBal) {
        this.name = name;
        this.balance = initialBal;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Account{" +
                "balance=" + balance +
                ", name='" + name + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return id.equals(account.id) &&
                balance.equals(account.balance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, balance);
    }
}
