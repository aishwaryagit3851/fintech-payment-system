package com.fintech.finance_platform_aish.dto;

import com.fintech.finance_platform_aish.entity.TransactionType;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CardPaymentRequestDTO {
    private String cardNumber;
    private BigDecimal amount;
    private TransactionType type; // PAY or TRANSFER
    private Long receiverUserId; // only for transfer
}