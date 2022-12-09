package com.example.bankAPI.repositories;

import com.example.bankAPI.models.Account;
import com.example.bankAPI.models.Blik;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AccountRepository {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public Account getAccountByAccountNum(String accountNum){
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM accounts WHERE account_num=?",
                    BeanPropertyRowMapper.newInstance(Account.class),
                    accountNum);
        }catch (EmptyResultDataAccessException e){
            return null;
        }
    }

    public boolean saveAccount(Account account) {
        if (getAccountByAccountNum(account.getAccount_num()) != null) {
            jdbcTemplate.update("UPDATE accounts SET name=?, surname=?, balance=? WHERE account_num=?",
                    account.getName(),
                    account.getSurname(),
                    account.getBalance(),
                    account.getAccount_num());
            return true;
        }
        return false;
    }

}
