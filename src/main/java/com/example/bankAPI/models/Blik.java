package com.example.bankAPI.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Blik {
    private String blik_num;
    private BigDecimal demanded_money;
    private String account_num;
}
