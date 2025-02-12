package com.kata.bank.api;

import com.kata.bank.application.AccountService;
import com.kata.bank.application.BankAccountException;
import com.kata.bank.domain.model.Account;
import com.kata.bank.domain.model.Operation;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Account API", description = "API for bank account operations")
@RestController
@RequestMapping("/api/accounts")
@Slf4j
public class AccountController {

    private final AccountService accountService;

    @Autowired
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @io.swagger.v3.oas.annotations.Operation(
            summary = "Create a new bank account",
            description = "Creates a new bank account with zero balance."
    )
    @ApiResponse(responseCode = "201", description = "Account successfully created", content = @Content(schema = @Schema(implementation = Account.class)))
    @PostMapping
    public ResponseEntity<Account> createAccount() {
        Account account = accountService.createAccount();
        return ResponseEntity.status(HttpStatus.CREATED).body(account);
    }

    @io.swagger.v3.oas.annotations.Operation(
            summary = "Get account details",
            description = "Retrieves account details by account ID"
    )
    @ApiResponse(responseCode = "200", description = "Account found", content = @Content(schema = @Schema(implementation = Account.class)))
    @ApiResponse(responseCode = "404", description = "Account not found", content = @Content)
    @GetMapping("/{accountId}")
    public ResponseEntity<Account> getAccount(
            @Parameter(description = "Account identifier", required = true)
            @PathVariable int accountId) {
        Account account = accountService.getAccount(accountId);
        if (account == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(account);
    }

    @io.swagger.v3.oas.annotations.Operation(
            summary = "Deposit funds",
            description = "Deposits a specified amount into the account."
    )
    @ApiResponse(responseCode = "200", description = "Deposit successful", content = @Content(schema = @Schema(implementation = Account.class)))
    @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content)
    @PostMapping("/{accountId}/deposit")
    public ResponseEntity<Account> deposit(
            @Parameter(description = "Account identifier", required = true)
            @PathVariable int accountId,
            @Parameter(description = "Amount to deposit", required = true)
            @RequestParam BigDecimal amount) {
        try {
            accountService.deposit(accountId, amount);
            Account updatedAccount = accountService.getAccount(accountId);
            return ResponseEntity.ok(updatedAccount);
        } catch (BankAccountException | NullPointerException | IllegalArgumentException e) {
            log.error(e.getMessage(),e);
            return ResponseEntity.badRequest().build();
        }
    }

    @io.swagger.v3.oas.annotations.Operation(
            summary = "Withdraw funds",
            description = "Withdraws a specified amount from the account."
    )
    @ApiResponse(responseCode = "200", description = "Withdrawal successful", content = @Content(schema = @Schema(implementation = Account.class)))
    @ApiResponse(responseCode = "400", description = "Invalid input or overdraft unauthorized", content = @Content)
    @PostMapping("/{accountId}/withdrawal")
    public ResponseEntity<Account> withdrawal(
            @Parameter(description = "Account identifier", required = true)
            @PathVariable int accountId,
            @Parameter(description = "Amount to withdraw", required = true)
            @RequestParam BigDecimal amount) {
        try {
            accountService.withdrawal(accountId, amount);
            Account updatedAccount = accountService.getAccount(accountId);
            return ResponseEntity.ok(updatedAccount);
        } catch (BankAccountException | NullPointerException | IllegalArgumentException e) {
            log.error(e.getMessage(),e);
            return ResponseEntity.badRequest().build();
        }
    }

    @io.swagger.v3.oas.annotations.Operation(
            summary = "Get account statement",
            description = "Retrieves a statement of account operations up to a specific date-time."
    )
    @ApiResponse(responseCode = "200", description = "Statement retrieved", content = @Content)
    @GetMapping("/{accountId}/statement")
    public ResponseEntity<List<Operation>> getStatement(
            @Parameter(description = "Account identifier", required = true)
            @PathVariable int accountId,
            @Parameter(description = "Transaction date up to which operations are included, in ISO format AAAA-MM-dd",
                    required = true)
            @RequestParam String date) {
        try {
            Account account = accountService.getAccount(accountId);
            if (account == null) {
                return ResponseEntity.notFound().build();
            }
            var statement = accountService.getStatement(account, LocalDate.parse(date));
            return ResponseEntity.ok(statement);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return ResponseEntity.badRequest().build();
        }
    }
}
