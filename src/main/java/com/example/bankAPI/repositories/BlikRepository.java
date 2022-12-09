package com.example.bankAPI.repositories;

import com.example.bankAPI.exceptions.NoAccountException;
import com.example.bankAPI.models.Blik;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class BlikRepository {
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    AccountRepository accountRepository;

    public Blik addBlik(int blikNum, String accountNum) throws NoAccountException{
        if(accountRepository.getAccountByAccountNum(accountNum) == null){
            throw new NoAccountException("Konto nie istnieje");
        }
        jdbcTemplate.update("INSERT INTO blik_nums(blik_num, account_num) VALUES (?,?)",
                blikNum,
                accountNum);
        Blik blik = new Blik();
        blik.setBlik_num(blikNum);
        blik.setAccount_num(accountNum);
        return blik;
    }

    public Blik getBlikByNum(int blikNum){
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM blik_nums WHERE blik_num=?",
                    BeanPropertyRowMapper.newInstance(Blik.class),
                    blikNum);
        }catch (EmptyResultDataAccessException e){
            return null;
        }
    }
}
