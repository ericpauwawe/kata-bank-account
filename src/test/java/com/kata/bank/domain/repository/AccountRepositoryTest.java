package com.kata.bank.domain.repository;

import com.kata.bank.domain.model.Account;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class AccountRepositoryTest {

    private final AccountRepository accountRepository = new AccountRepository();

    @Test
     void save_account_should_store_in_repository() {
        // Given
        Account account = new Account();

        // When
        accountRepository.save(account);

        // Then
        Account retrievedAccount = accountRepository.findById(account.getAccountId());
        assertThat(retrievedAccount).isNotNull();
        assertThat(account.getAccountId()).isEqualTo(retrievedAccount.getAccountId());
    }

    @Test
     void find_existing_account_by_id() {
        // Given
        Account account = new Account();
        accountRepository.save(account);
        int accountId = account.getAccountId();

        // When
        Account foundAccount = accountRepository.findById(accountId);

        // Then
        assertThat(foundAccount).isNotNull();
        assertThat(foundAccount.getAccountId()).isEqualTo(account.getAccountId());
    }

    @Test
     void test_retrieve_all_accounts() {
        // Given
        AccountRepository accountRepository = new AccountRepository();
        Account account1 = new Account();
        Account account2 = new Account();
        accountRepository.save(account1);
        accountRepository.save(account2);

        // When
        List<Account> accounts = accountRepository.retrieveAll();

        // Then
        assertThat(accounts)
                .as("There should be exactly 2 accounts saved")
                .hasSize(2)
                .contains(account1, account2);
    }

}