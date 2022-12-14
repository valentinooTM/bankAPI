package com.example.bankAPI.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Blik {
    private String blik_num;
    private BigDecimal demanded_money;
    private String account_num;
    private String target_account;
    private BlikStatus status;
}
