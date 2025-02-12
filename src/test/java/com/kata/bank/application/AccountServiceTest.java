package com.kata.bank.application;

import com.kata.bank.domain.model.Account;
import com.kata.bank.domain.model.Operation;
import com.kata.bank.domain.model.OperationType;
import com.kata.bank.domain.repository.AccountRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.internal.matchers.Any;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private  AccountRepository accountRepository;
    @InjectMocks
    private  AccountService accountService;


    private Account createAndStubAccount() {
        var account = new Account();
        Mockito.when(accountRepository.findById(account.getAccountId())).thenReturn(account);
        return account;
    }


    @Test
    void should_make_initial_deposit() {
        //GIVEN
        var account = createAndStubAccount();

        //WHEN
        accountService.deposit(account.getAccountId(), BigDecimal.valueOf(100));

        //THEN
        assertThat(account.getBalance()).isEqualTo(BigDecimal.valueOf(100));
    }

    @Test
    void should_make_deposit() {
        //GIVEN
        var account = createAndStubAccount();
        var accountId = account.getAccountId();

        //WHEN
        accountService.deposit(accountId,BigDecimal.valueOf(100));
        accountService.deposit(accountId,BigDecimal.valueOf(50));

        //THEN
        assertThat(account.getBalance()).isEqualTo(BigDecimal.valueOf(150));
    }



    @Test
    void should_not_make_deposit_because_account_is_null() {
        //GIVEN
        var amount = BigDecimal.valueOf(100);

        //WHEN
        assertThatThrownBy(()->{
            accountService.deposit(5, amount);
        }).isInstanceOf(BankAccountException.class).hasMessage(AccountService.ACCOUNT_UNKNOW);
    }

    @Test
    void should_not_make_deposit_because_amount_is_null() {
        //GIVEN
        var account = createAndStubAccount();
        var accountId = account.getAccountId();

        //WHEN
        assertThatThrownBy(()->{
            accountService.deposit(accountId, null);
        }).isInstanceOf(BankAccountException.class).hasMessage(AccountService.AMOUNT_IS_NULL);
    }

    @Test
    void should_not_make_deposit_negative_amount() {
        //GIVEN
        var account = createAndStubAccount();
        var accountId = account.getAccountId();
        var amount = BigDecimal.valueOf(-50);

        //WHEN
        assertThatThrownBy(()->{
            accountService.deposit(accountId, amount);
        }).isInstanceOf(BankAccountException.class).hasMessage(AccountService.NEGATIVE_AMOUNT_NOT_ACCEPTED);
    }


    @Test
    void should_make_withdrawal() {
        //GIVEN
        var account = createAndStubAccount();
        var accountId = account.getAccountId();
        //WHEN
        accountService.deposit(accountId,BigDecimal.valueOf(100));
        accountService.withdrawal(accountId,BigDecimal.valueOf(50));

        //THEN
        assertThat(account.getBalance()).isEqualTo(BigDecimal.valueOf(50));
    }

    @Test
    void should_not_make_withdrawal() {
        //GIVEN
        var account = createAndStubAccount();
        var accountId = account.getAccountId();


        //WHEN
        var amount=BigDecimal.valueOf(100);

        //WHEN
        assertThatThrownBy(()->{
            accountService.withdrawal(accountId, amount);
        }).isInstanceOf(BankAccountException.class).hasMessage(AccountService.OVERDRAFT_UNAUTHORIZED);
    }

    @Test
    void should_get_statement(){
        //GIVEN
        var account = createAndStubAccount();
        var accountId = account.getAccountId();


        //WHEN
        accountService.deposit(accountId,BigDecimal.valueOf(100));
        accountService.withdrawal(accountId,BigDecimal.valueOf(50));
        var transactionDate = LocalDate.now().plusDays(1);
        var transactionDateTime=transactionDate.atStartOfDay();
        List<Operation> statement=accountService.getStatement(account, transactionDate);

        //THEN
        assertThat(statement).size().isEqualTo(2);

        assertThat(statement).filteredOn(
                operation -> OperationType.DEPOSIT.name().contentEquals(operation.operationType().name())
                && operation.operationAmount().intValue()==100
                && transactionDateTime.isAfter(operation.operationDate())
        ).size().isEqualTo(1);

        assertThat(statement).filteredOn(
                operation -> OperationType.WITHDRAWAL.name().contentEquals(operation.operationType().name())
                        && operation.operationAmount().intValue()==50
                        && transactionDateTime.isAfter(operation.operationDate())
        ).size().isEqualTo(1);

    }

    @Test
    void should_create_account_with_zero_balance() {
        // Given

        Account expectedAccount = new Account();
        when(accountRepository.save(any(Account.class))).thenReturn(expectedAccount);

        // When
        Account createdAccount = accountService.createAccount();

        // Then
        assertThat(createdAccount).isNotNull();
        assertThat(createdAccount.getBalance()).isZero();
        verify(accountRepository).save(any(Account.class));
    }

    @Test
     void test_get_all_accounts_returns_accounts_list() {
        // Given
        var expectedAccounts = List.of(new Account(), new Account());
        when(accountRepository.retrieveAll()).thenReturn(expectedAccounts);

        // When
        List<Account> actualAccounts = accountService.getAllAccounts();

        // Then
        assertThat(actualAccounts).containsAll(expectedAccounts);
        verify(accountRepository).retrieveAll();
    }

}