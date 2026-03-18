package com.fintech.finance_platform_aish.dto;



import lombok.Data;

import java.math.BigDecimal;

@Data
public class AccountResponseDTO {

    private Long id;
    private String accountNumber;
    private BigDecimal balance;
    private String accountType;


}
