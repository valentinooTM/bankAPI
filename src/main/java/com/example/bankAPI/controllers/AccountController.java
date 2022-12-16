package com.example.bankAPI.controllers;

import com.example.bankAPI.models.Account;
import com.example.bankAPI.repositories.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/account")
public class AccountController {

    @Autowired
    private AccountRepository accountRepository;

    @PostMapping("/login/{account_num}/{password}")
    public ResponseEntity<Account> login(@PathVariable String account_num, @PathVariable String password){
        Account account = accountRepository.getAccountByAccountNum(account_num);
        if(account == null){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
        if(!account.getPassword().equals(password)){
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }
        return new ResponseEntity<>(account, HttpStatus.OK);
    }


}
