package com.example.bankAPI.controllers;

import com.example.bankAPI.models.*;
import com.example.bankAPI.repositories.AccountRepository;
import com.example.bankAPI.repositories.BlikRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.math.BigDecimal;
import java.util.concurrent.ForkJoinPool;

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
            if(blik.getStatus() == BlikStatus.active){
                return blik;
            }
        }
        new BlikExpireTask(blikNum, blikRepository).start();
        return blikRepository.addBlik(blikNum, accountNum);
    }

    @PostMapping("/pay/{blik_num}/{demanded_money}/{target_account_num}")
    public DeferredResult<BlikTransactionStatus> blikPayment(@PathVariable("blik_num") String blikNum,
                                                             @PathVariable("demanded_money")BigDecimal demandedMoney,
                                                             @PathVariable("target_account_num")String targetAccountNum){
        DeferredResult<BlikTransactionStatus> output = new DeferredResult<BlikTransactionStatus>(31_000L);
        Blik blik = blikRepository.getBlikByNum(blikNum);
        if (blik == null){
            output.setResult(BlikTransactionStatus.BAD_BLIK);
            return output;
        }
        if(blik.getStatus() != BlikStatus.active){
            output.setResult(BlikTransactionStatus.BAD_BLIK);
            return output;
        }
        Account account = accountRepository.getAccountByAccountNum(blik.getAccount_num());
        Account targetAccount = accountRepository.getAccountByAccountNum(targetAccountNum);
        if(targetAccount == null){
            output.setResult(BlikTransactionStatus.BAD_TARGET_ACCOUNT);
            return output;
        }
        if(demandedMoney.compareTo(account.getBalance()) >= 0){
            output.setResult(BlikTransactionStatus.NOT_ENOUGHT_MONEY);
            return output;
        }
        blik.setStatus(BlikStatus.to_confirm);
        blik.setDemanded_money(demandedMoney);
        blik.setTarget_account(targetAccountNum);
        blikRepository.saveBlik(blik);
        ForkJoinPool.commonPool().submit(()->{
            for(int i=0; i<29; i++){
                try {
                    Thread.sleep(1000);
                    Blik b = blikRepository.getBlikByNum(blikNum);
                    if(b.getStatus().equals(BlikStatus.used)){
                        output.setResult(BlikTransactionStatus.CONFIRMED);
                        break;
                    }

                }catch(InterruptedException e){}
            }
            if(!output.hasResult()){
                output.setResult(BlikTransactionStatus.NO_CONFIRMATION);
            }
        });
        return output;
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
        if(blik == null){
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
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
