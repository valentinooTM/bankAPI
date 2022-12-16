package com.example.bankAPI.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Account {
    private String account_num;
    private String name;
    private String surname;
    private BigDecimal balance;
    private String password;
}
