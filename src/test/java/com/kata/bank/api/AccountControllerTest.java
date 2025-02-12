package com.kata.bank.api;

import static org.junit.jupiter.api.Assertions.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kata.bank.application.AccountService;
import com.kata.bank.domain.model.Account;
import com.kata.bank.domain.model.Operation;
import com.kata.bank.domain.model.OperationType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(AccountController.class)
class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Mock the dependent AccountService bean
    @MockitoBean
    private AccountService accountService;

    @Test
    @DisplayName("GET /api/accounts/{accountId} - Found")
     void testGetAccountFound() throws Exception {
        // Arrange: prepare a dummy account with id 1 and balance 100.00
        Account dummyAccount = new Account();
        dummyAccount.setBalance(BigDecimal.valueOf(100));

        when(accountService.getAccount(1)).thenReturn(dummyAccount);

        // Act & Assert: perform GET and verify the response
        mockMvc.perform(get("/api/accounts/{accountId}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accountId", is(1)))
                .andExpect(jsonPath("$.balance", is(100)));
    }

    @Test
    @DisplayName("GET /api/accounts/{accountId} - Not Found")
     void testGetAccountNotFound() throws Exception {
        // Arrange: when no account is found, return null
        when(accountService.getAccount(1)).thenReturn(null);

        // Act & Assert: verify 404 response when account is not found
        mockMvc.perform(get("/api/accounts/{accountId}", 1))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/accounts/{accountId}/deposit - Successful Deposit")
     void testDepositSuccessful() throws Exception {
        // Arrange: prepare the dummy account before deposit
        Account accountBefore = new Account();
        accountBefore.setBalance(BigDecimal.valueOf(100.00));


        // Mock the accountService.deposit behavior
        // For deposit, we simulate that deposit is executed and then getAccount returns updated account.
        int accountId = accountBefore.getAccountId();
        doNothing().when(accountService).deposit(accountId, BigDecimal.valueOf(50.00));
        accountBefore.setBalance(BigDecimal.valueOf(150.00));;
        when(accountService.getAccount(accountId)).thenReturn(accountBefore);

        // Act & Assert: perform deposit POST and expect updated account details in response
        mockMvc.perform(post("/api/accounts/{accountId}/deposit", accountId)
                        .param("amount", "50.00"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accountId", is(accountId)))
                .andExpect(jsonPath("$.balance", is(150.00)));
    }

    @Test
    @DisplayName("GET /api/accounts/{accountId}/statement - Successful Statement Retrieval")
     void testGetStatement() throws Exception {
        // Arrange: simulate an account having one operation before current date-time
        Account account = new Account();
        account.setBalance(BigDecimal.valueOf(100.00));

        Operation op = Operation.builder()
                .operationType(OperationType.DEPOSIT)
                .operationAmount(BigDecimal.valueOf(100.00))
                .operationDate(LocalDateTime.now())
                .build();
        List<Operation> operations = Collections.singletonList(op);

        var accountId = account.getAccountId();
        Mockito.when(accountService.getAccount(accountId)).thenReturn(account);
        Mockito.when(accountService.getStatement(eq(account), any(LocalDate.class))).thenReturn(operations);

        // Act & Assert: perform GET with a valid dateTime query parameter
        String date = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        mockMvc.perform(get("/api/accounts/{accountId}/statement", accountId)
                        .param("date", date))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                // Assuming the response is a JSON array containing one operation
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].operationType", is("DEPOSIT")))
                .andExpect(jsonPath("$[0].operationAmount", is(100.00)));
    }


}
