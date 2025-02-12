package com.kata.bank.domain.repository;

import com.kata.bank.domain.model.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;


import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Repository
@Slf4j
public class AccountRepository {

    private final Map<Integer, Account> accounts= new ConcurrentHashMap<>();

   public Account save(Account account){
       var  accountId = account.getAccountId();
       accounts.put(accountId, account);
       log.info( String.format("account with id %s is created",account.getAccountId()));
       return account;
   }

   public Account findById(int id){
       return accounts.get(id);
   }

   public List<Account> retrieveAll(){
       return accounts.values().stream().toList();
   }





}
