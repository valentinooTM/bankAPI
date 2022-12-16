package com.example.bankAPI.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class Confirmation {
    private String targetAccount;
    private BigDecimal demandedMoney;
    private String targetAccountOwnerName;
    private String targetAccountOwnerSurname;
}
