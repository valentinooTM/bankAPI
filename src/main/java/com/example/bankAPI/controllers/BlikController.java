package com.example.bankAPI.controllers;

import com.example.bankAPI.exceptions.NoAccountException;
import com.example.bankAPI.models.Account;
import com.example.bankAPI.models.Blik;
import com.example.bankAPI.models.BlikStatus;
import com.example.bankAPI.models.Confirmation;
import com.example.bankAPI.repositories.AccountRepository;
import com.example.bankAPI.repositories.BlikRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
        String blikNum = getRandomCode();
        for (Blik blik: blikRepository.getAllBliksFromAccount(accountNum)) {
            if(blik.getStatus().equals(BlikStatus.active)){
                return blik;
            }
        }
        new BlikExpireTask(blikNum, blikRepository).start();
        return blikRepository.addBlik(blikNum, accountNum);
    }

    @PostMapping("/pay/{blik_num}/{demanded_money}/{target_account_num}")
    public String blikPayment(@PathVariable("blik_num") String blikNum,
                              @PathVariable("demanded_money")BigDecimal demandedMoney,
                              @PathVariable("target_account_num")String targetAccountNum){
        Blik blik = blikRepository.getBlikByNum(blikNum);
        if (blik == null){
            return "Zły kod blik";
        }
        if(blik.getStatus() != BlikStatus.active){
            return "Nieaktywny kod blik";
        }
        Account account = accountRepository.getAccountByAccountNum(blik.getAccount_num());
        Account targetAccount = accountRepository.getAccountByAccountNum(targetAccountNum);
        if(targetAccount == null){
            return "Konto docelowe nie istnieje";
        }
        if(demandedMoney.compareTo(account.getBalance()) >= 0){
            return "Niewystarczająca ilość środków na koncie";
        }
        blik.setStatus(BlikStatus.to_confirm);
        blik.setDemanded_money(demandedMoney);
        blik.setTarget_account(targetAccountNum);
        System.out.println(blik.getStatus());
        blikRepository.saveBlik(blik);
        return "success";
    }

    @GetMapping("/check-status/{blik_num}")
    public String checkStatus(@PathVariable String blik_num){
        Blik blik = blikRepository.getBlikByNum(blik_num);
        return blik.getStatus().toString();
    }

    @GetMapping("/confirm/{blik_num}/{password}")
    public ResponseEntity<Confirmation> sendConfirmData(@PathVariable String blik_num, @PathVariable String password){
        Confirmation conf = new Confirmation();
        Blik blik = blikRepository.getBlikByNum(blik_num);
        Account targetAccount = accountRepository.getAccountByAccountNum(blik.getTarget_account());
        Account account = accountRepository.getAccountByAccountNum(blik.getAccount_num());
        if(targetAccount == null){
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
        if(!account.getPassword().equals(password)){
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }
        conf.setDemandedMoney(blik.getDemanded_money());
        conf.setTargetAccount(blik.getTarget_account());
        conf.setTargetAccountOwnerName(targetAccount.getName());
        conf.setTargetAccountOwnerSurname(targetAccount.getSurname());
        return new ResponseEntity<>(conf, HttpStatus.OK);
    }

    @PostMapping("/confirm/{blik_num}/{password}")
    public void getConfirmation(@PathVariable String blik_num, @PathVariable String password){
        Blik blik = blikRepository.getBlikByNum(blik_num);
        Account account = accountRepository.getAccountByAccountNum(blik.getAccount_num());
        Account targetAccount = accountRepository.getAccountByAccountNum(blik.getTarget_account());
        if(targetAccount == null){
            return;
        }
        if(blik.getStatus() != BlikStatus.to_confirm){
            return;
        }
        if(!account.getPassword().equals(password)) {
            return;
        }

        account.setBalance(account.getBalance().subtract(blik.getDemanded_money()));
        targetAccount.setBalance(targetAccount.getBalance().add(blik.getDemanded_money()));

        accountRepository.saveAccount(account);
        accountRepository.saveAccount(targetAccount);

        blik.setStatus(BlikStatus.used);
        blikRepository.saveBlik(blik);
    }

    private class BlikExpireTask extends Thread{
        private String blikNum;
        private BlikRepository blikRepository;
        public BlikExpireTask(String blikNum, BlikRepository blikRepository){
            this.blikNum = blikNum;
            this.blikRepository = blikRepository;
        }
        public void run(){
            try{
                Thread.sleep(120_000);
                Blik blik = blikRepository.getBlikByNum(blikNum);
                if(blik.getStatus() != BlikStatus.used){
                    blik.setStatus(BlikStatus.expired);
                    blikRepository.saveBlik(blik);
                }
            } catch(InterruptedException e){}
        }
    }

    private String getRandomCode(){
        String code = "";
        for(int j=0; j<6; j++) {
            code += randomInt(0, 9);
        }
        return code;
    }
    private int randomInt(int min, int max){
        return min + (int)(Math.random() * ((max - min) + 1));
    }

}
