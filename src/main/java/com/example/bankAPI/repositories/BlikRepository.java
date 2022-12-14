package com.example.bankAPI.repositories;

import com.example.bankAPI.exceptions.NoAccountException;
import com.example.bankAPI.models.Account;
import com.example.bankAPI.models.Blik;
import com.example.bankAPI.models.BlikStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BlikRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    AccountRepository accountRepository;

    public Blik addBlik(String blikNum, String accountNum) throws NoAccountException{
        if(accountRepository.getAccountByAccountNum(accountNum) == null){
            throw new NoAccountException("Konto nie istnieje");
        }
        jdbcTemplate.update("INSERT INTO blik_nums(blik_num, account_num, status) VALUES (?,?,?)",
                blikNum,
                accountNum,
                BlikStatus.active.toString());
        Blik blik = new Blik();
        blik.setBlik_num(blikNum);
        blik.setAccount_num(accountNum);
        blik.setStatus(BlikStatus.active);
        return blik;
    }

    public Blik getBlikByNum(String blikNum){
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM blik_nums WHERE blik_num=?",
                    BeanPropertyRowMapper.newInstance(Blik.class),
                    blikNum);
        }catch (EmptyResultDataAccessException e){
            return null;
        }
    }
    public boolean saveBlik(Blik blik) {
        if (getBlikByNum(blik.getBlik_num()) != null) {
            jdbcTemplate.update("UPDATE blik_nums SET demanded_money=?, account_num=?, target_account=?, status=? WHERE blik_num=?",
                    blik.getDemanded_money(),
                    blik.getAccount_num(),
                    blik.getTarget_account(),
                    blik.getStatus().toString(),
                    blik.getBlik_num());
            return true;
        }
        return false;
    }

    public List<Blik> getAllBliksFromAccount(String accountNum){
        return jdbcTemplate.query("SELECT * FROM blik_nums WHERE account_num=?",
                BeanPropertyRowMapper.newInstance(Blik.class),
                accountNum);
    }
}
