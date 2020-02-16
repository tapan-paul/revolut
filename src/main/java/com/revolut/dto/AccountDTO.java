package com.revolut.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("AccountDTO")
public class AccountDTO {

    @JsonProperty("id")
    private int id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("balance")
    private double getBalance;

    public AccountDTO(int id, String name, double getBalance) {
        this.id = id;
        this.name = name;
        this.getBalance = getBalance;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getGetBalance() {
        return getBalance;
    }

    public void setGetBalance(double getBalance) {
        this.getBalance = getBalance;
    }
}
