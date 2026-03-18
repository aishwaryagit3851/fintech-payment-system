package com.fintech.finance_platform_aish.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CardResponseDTO {

    private String cardNumber;
    private String cardType;
    private String bankName;
    private LocalDate expiryDate;
}
