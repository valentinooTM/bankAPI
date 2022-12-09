package com.example.bankAPI.controllers;

import com.example.bankAPI.exceptions.NoAccountException;
import com.example.bankAPI.models.Account;
import com.example.bankAPI.models.Blik;
import com.example.bankAPI.repositories.AccountRepository;
import com.example.bankAPI.repositories.BlikRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@RestController
@RequestMapping("/blik")
public class BlikController {
    @Autowired
    BlikRepository blikRepository;
    @Autowired
    AccountRepository accountRepository;

    @PostMapping("/generate-blik/{account_num}")
    public Blik generateBlik(@PathVariable("account_num") String accountNum){
        Integer blikNum = getRandomCode();
        return blikRepository.addBlik(blikNum, accountNum);
    }

    @PostMapping("/pay/{blik_num}/{demanded_money}/{target_account_num}")
    public String blikPayment(@PathVariable("blik_num") Integer blikNum,
                              @PathVariable("demanded_money")BigDecimal demandedMoney,
                              @PathVariable("target_account_num")String targetAccountNum){
        Blik blik = blikRepository.getBlikByNum(blikNum);
        if (blik == null){
            return "Zły kod blik";
        }
        Account account = accountRepository.getAccountByAccountNum(blik.getAccount_num());
        Account targetAccount = accountRepository.getAccountByAccountNum(targetAccountNum);
        if(targetAccount == null){
            return "Konto docelowe nie istnieje";
        }
        if(demandedMoney.compareTo(account.getBalance()) >= 0){
            return "Niewystarczająca ilość środków na koncie";
        }
        account.setBalance(account.getBalance().subtract(demandedMoney));
        targetAccount.setBalance(targetAccount.getBalance().add(demandedMoney));

        accountRepository.saveAccount(account);
        accountRepository.saveAccount(targetAccount);
        return "success";
    }

    private Integer getRandomCode(){
        String code = "";
        for(int j=0; j<6; j++) {
            code += randomInt(0, 9);
        }
        return Integer.parseInt(code);
    }
    private int randomInt(int min, int max){
        return min + (int)(Math.random() * ((max - min) + 1));
    }
}
