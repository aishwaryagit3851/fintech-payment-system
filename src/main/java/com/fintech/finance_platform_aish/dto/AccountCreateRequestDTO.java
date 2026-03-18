package com.fintech.finance_platform_aish.dto;



import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountCreateRequestDTO {

    private Long userId;
    private String accountType;
    private BigDecimal initialDeposit;


}
