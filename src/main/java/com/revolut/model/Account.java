package com.revolut.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table (name = "Account")
@NamedQueries({
        @NamedQuery(name = "Account.findAccountById", query = "SELECT a FROM Account a WHERE a.id = :id")
})
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(nullable = false)
    private Integer id;

    @Column(nullable = false)
    private BigDecimal balance;

    @Column(nullable = false)
    private String name;

    public Account(BigDecimal balance, String name) {
        this.balance = balance;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

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
