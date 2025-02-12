package com.kata.bank.application;

import com.kata.bank.domain.model.Account;
import com.kata.bank.domain.model.Operation;
import com.kata.bank.domain.model.OperationType;
import com.kata.bank.domain.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class AccountService {


    private final AccountRepository accountRepository;

    public static final String ACCOUNT_UNKNOW = "unknow account";
    public static final String AMOUNT_IS_NULL = "amount is null";
    public static final String NEGATIVE_AMOUNT_NOT_ACCEPTED = "negative amount not accepted";
    public static final String OVERDRAFT_UNAUTHORIZED = "overdraft unauthorized";

   @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account createAccount(){
        return accountRepository.save(new Account());
    }

    public List<Account> getAllAccounts(){
       return accountRepository.retrieveAll();
    }

    public Account getAccount(int id){
       return accountRepository.findById(id);
    }

    public void deposit(int accountId, BigDecimal amount){
       var account= getAccount(accountId);
       checkParams(account, amount);
       log.info(String.format("deposit of %s requested for account n° %s",amount.intValue(),accountId));
       account.setBalance(account.getBalance().add(amount));
       createOperation(account, amount,OperationType.DEPOSIT);
       log.info(String.format("deposit of %s  for account n° %s sucessful",amount.intValue(),accountId));
    }

    private static void createOperation(Account account, BigDecimal amount,OperationType operationType) {
        account.getOperations().add(Operation.builder()
                        .operationType(operationType)
                        .operationAmount(amount)
                        .operationDate(LocalDateTime.now())
                .build());
    }

    public void withdrawal(int accountId, BigDecimal amount) {

        var account= getAccount(accountId);
        checkParams(account,amount);
        var balance = account.getBalance();
        checkBalance(balance,amount);
        log.info(String.format("withdrawal of %s requested for account n° %s",amount.intValue(),accountId));
        account.setBalance(balance.subtract(amount));
        createOperation(account, amount,OperationType.WITHDRAWAL);
        log.info(String.format("withdrawal of %s  for account n° %s successful",amount.intValue(),accountId));

    }

    private static void checkParams(Account account, BigDecimal amount) {
        Objects.requireNonNull(account,()->{throw new BankAccountException(ACCOUNT_UNKNOW);});
        Objects.requireNonNull(amount,()->{throw new BankAccountException(AMOUNT_IS_NULL);});
        if(amount.signum() == -1)
            throw new BankAccountException(NEGATIVE_AMOUNT_NOT_ACCEPTED);
    }

    private static void checkBalance(BigDecimal balance, BigDecimal amount) {

        if (amount.compareTo(balance) > 0)
            throw new BankAccountException(OVERDRAFT_UNAUTHORIZED);
    }

    public List<Operation> getStatement(Account account, LocalDate date){

        Objects.requireNonNull(account,()->{throw new BankAccountException(ACCOUNT_UNKNOW);});
        return account.getOperations().stream().filter( operation -> operation.operationDate().isBefore(date.atStartOfDay().plusDays(1))).toList();
    }


}
