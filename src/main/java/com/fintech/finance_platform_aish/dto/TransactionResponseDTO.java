package com.fintech.finance_platform_aish.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fintech.finance_platform_aish.entity.TransactionType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionResponseDTO {
    private Long id;
    private String fromAccountNumber;
    private String toAccountNumber;
    private BigDecimal amount;
    private LocalDateTime timestamp;
    private TransactionType transactionType;

}
