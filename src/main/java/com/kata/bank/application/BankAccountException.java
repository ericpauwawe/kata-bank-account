package com.kata.bank.application;

public class BankAccountException extends RuntimeException{

    public BankAccountException(String errorMessage) {
        super(errorMessage);
    }

}
