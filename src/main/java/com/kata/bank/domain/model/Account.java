package com.kata.bank.domain.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


@Getter
public class Account {

    @Getter(AccessLevel.NONE)
    private static final AtomicInteger counter = new AtomicInteger(0);

    private final int accountId;

    @Setter
    private BigDecimal balance;

    private final List<Operation> operations;


    public Account(){
        this.balance=BigDecimal.ZERO;
        this.operations= new LinkedList<>();
        this.accountId = counter.incrementAndGet();
    }

}
