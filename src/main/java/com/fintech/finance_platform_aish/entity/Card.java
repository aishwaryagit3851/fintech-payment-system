package com.fintech.finance_platform_aish.entity;


import jakarta.persistence.*;
import lombok.Data;


import java.time.LocalDate;

@Entity
@Data
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cardNumber;

    private String cardType; // DEBIT

    private String bankName;

    private LocalDate expiryDate;

    private String cvv;

    @ManyToOne
    private User user;

    @ManyToOne
    private Account account;
}
