package com.fintech.finance_platform_aish.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Data
public class LedgerEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Account account;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private EntryType entryType; // DEBIT / CREDIT

    private LocalDateTime timestamp;

    @ManyToOne
    private Transaction transaction;
}