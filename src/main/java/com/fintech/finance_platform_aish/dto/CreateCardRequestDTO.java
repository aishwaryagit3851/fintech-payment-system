package com.fintech.finance_platform_aish.dto;


import lombok.Data;

@Data
public class CreateCardRequestDTO {

    private Long accountId;
    private String cardType;
    private String bankName;
}
