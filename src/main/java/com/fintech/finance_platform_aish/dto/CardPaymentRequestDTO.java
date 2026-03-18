package com.fintech.finance_platform_aish.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CardPaymentRequestDTO {
    private String cardNumber;
    private BigDecimal amount;
    private String type; // PAY or TRANSFER
    private Long receiverUserId; // only for transfer
}