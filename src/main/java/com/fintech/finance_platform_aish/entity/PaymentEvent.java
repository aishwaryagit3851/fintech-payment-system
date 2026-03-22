package com.fintech.finance_platform_aish.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentEvent {
    private String referenceId;
    private BigDecimal amount;
    private String status;
    private String type;
}
