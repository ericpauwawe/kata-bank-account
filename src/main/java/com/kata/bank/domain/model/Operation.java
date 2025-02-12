package com.kata.bank.domain.model;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record Operation(LocalDateTime operationDate, OperationType operationType, BigDecimal operationAmount) {
}
