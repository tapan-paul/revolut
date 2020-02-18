package com.revolut;

public enum TransferState {

    SUCCESS,
    FAILED_INSUFFICIENT_BALANCE,
    FAILED_ACCOUNT_FROM,
    FAILED_ACCOUNT_TO,
    NEGATIVE_AMOUNT,
    DB_ERROR
}
