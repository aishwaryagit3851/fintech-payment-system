package com.fintech.finance_platform_aish.service;

import com.fintech.finance_platform_aish.dto.CardPaymentRequestDTO;
import com.fintech.finance_platform_aish.dto.TransactionResponseDTO;
import com.fintech.finance_platform_aish.entity.Account;
import com.fintech.finance_platform_aish.entity.Card;
import com.fintech.finance_platform_aish.entity.Transaction;
import com.fintech.finance_platform_aish.entity.User;
import com.fintech.finance_platform_aish.repository.AccountRepository;
import com.fintech.finance_platform_aish.repository.CardRepository;
import com.fintech.finance_platform_aish.repository.TransactionRepository;
import com.fintech.finance_platform_aish.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class PaymentService {

    private final CardRepository cardRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public PaymentService(CardRepository cardRepository,
                          AccountRepository accountRepository,
                          TransactionRepository transactionRepository,
                          UserRepository userRepository) {
        this.cardRepository = cardRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public TransactionResponseDTO processCardPayment(CardPaymentRequestDTO request) {

        // 1. Validate card
        Card card = cardRepository.findByCardNumber(request.getCardNumber())
                .orElseThrow(() -> new RuntimeException("Card not found"));

        Account senderAccount = card.getAccount();

        // 2. Check balance
        if (senderAccount.getBalance().compareTo(request.getAmount()) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        // 3. Deduct money
        senderAccount.setBalance(senderAccount.getBalance().subtract(request.getAmount()));

        Account receiverAccount = null;



        // 5. Save transaction
        Transaction txn = new Transaction();
        txn.setAmount(request.getAmount());
        txn.setTransactionType(request.getType());
        txn.setTimestamp(LocalDateTime.now());
        txn.setFromAccount(senderAccount);

        transactionRepository.save(txn);

        // 6. Prepare response
        TransactionResponseDTO response = new TransactionResponseDTO();
        response.setAmount(txn.getAmount());
        response.setTransactionType(txn.getTransactionType());
        response.setTimestamp(txn.getTimestamp());

        return response;
    }
}