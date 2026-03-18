
package com.fintech.finance_platform_aish.service;

import com.fintech.finance_platform_aish.dto.AccountResponseDTO;
import com.fintech.finance_platform_aish.dto.TransactionResponseDTO;
import com.fintech.finance_platform_aish.entity.Account;
import com.fintech.finance_platform_aish.entity.Card;
import com.fintech.finance_platform_aish.entity.Transaction;
import com.fintech.finance_platform_aish.repository.AccountRepository;
import com.fintech.finance_platform_aish.repository.CardRepository;
import com.fintech.finance_platform_aish.repository.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CardRepository cardRepository;

    public TransactionService(TransactionRepository transactionRepository,
                              AccountRepository accountRepository, CardRepository cardRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.cardRepository = cardRepository;
    }

    @Transactional
    public TransactionResponseDTO transferMoney(Long fromAccountId,
                                                Long toAccountId,
                                                BigDecimal amount) {



        Account fromAccount = accountRepository.findAccountForUpdate(fromAccountId)
                .orElseThrow(() -> new RuntimeException("Sender account not found"));

        Account toAccount = accountRepository.findAccountForUpdate(toAccountId)
                .orElseThrow(() -> new RuntimeException("Receiver account not found"));

        validateTransaction(fromAccount, toAccount, amount);

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        // debit sender
        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));

        // credit receiver
        toAccount.setBalance(toAccount.getBalance().add(amount));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);


        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setTransactionType("TRANSFER");
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setFromAccount(fromAccount);
        transaction.setToAccount(toAccount);

        Transaction saved = transactionRepository.save(transaction);

        TransactionResponseDTO response = new TransactionResponseDTO();
        response.setId(saved.getId());
        response.setAmount(saved.getAmount());
        response.setTransactionType(saved.getTransactionType());
        response.setTimestamp(saved.getTimestamp());
        response.setFromAccountNumber(saved.getFromAccount().getAccountNumber());
        response.setToAccountNumber(saved.getToAccount().getAccountNumber());

        return response;
    }


    public Page<TransactionResponseDTO> getAccountTransactions(
            Long accountId, int page, int size) {

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("timestamp").descending()
        );

        Page<Transaction> transactions =
                transactionRepository.findTransactionsForAccount(accountId, pageable);

        return transactions.map(this::mapToDTO);
    }


    @Transactional
    public TransactionResponseDTO deposit(Long accountId, BigDecimal amount) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        account.setBalance(account.getBalance().add(amount));

        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setTransactionType("DEPOSIT");
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setToAccount(account);

        Transaction savedTx =  transactionRepository.save(transaction);
        return mapToDTO(savedTx);
    }

    @Transactional
    public TransactionResponseDTO withdraw(Long accountId, BigDecimal amount) {

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        account.setBalance(account.getBalance().subtract(amount));

        accountRepository.save(account);

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setTransactionType("WITHDRAW");
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setFromAccount(account);

        Transaction savedTx = transactionRepository.save(transaction);

        return mapToDTO(savedTx);
    }


    private TransactionResponseDTO mapToDTO(Transaction tx) {

        TransactionResponseDTO dto = new TransactionResponseDTO();

        dto.setId(tx.getId());

        if (tx.getFromAccount() != null) {
            dto.setFromAccountNumber(tx.getFromAccount().getAccountNumber());
        }

        if (tx.getToAccount() != null) {
            dto.setToAccountNumber(tx.getToAccount().getAccountNumber());
        }

        dto.setAmount(tx.getAmount());
        dto.setTransactionType(tx.getTransactionType());
        dto.setTimestamp(tx.getTimestamp());

        return dto;
    }

    private void validateTransaction(Account from, Account to, BigDecimal amount) {

        if (amount.compareTo(BigDecimal.valueOf(10000))> 0) {
            throw new RuntimeException("Limit exceeded");
        }

        if (amount.compareTo(from.getBalance())>0) {
            throw new RuntimeException("Insufficient balance");
        }

        if (from.getId().equals(to.getId())) {
            throw new RuntimeException("Invalid transfer");
        }

        LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);

        long txnCount = transactionRepository
                .countRecentTransactions(from.getId(), oneMinuteAgo);

        if (txnCount >= 5) {
            throw new RuntimeException("Too many requests");
        }
    }

    @jakarta.transaction.Transactional
    public TransactionResponseDTO payWithCard(String cardNumber, BigDecimal amount) {

        Card card = cardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new RuntimeException("Card not found"));

        Account account = accountRepository
                .findAccountForUpdate(card.getAccount().getId())
                .orElseThrow();

        if (account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Insufficient balance");
        }

        account.setBalance(account.getBalance().subtract(amount));
        accountRepository.save(account);

        Transaction tx = new Transaction();
        tx.setAmount(amount);
        tx.setTransactionType("CARD_PAYMENT");
        tx.setTimestamp(LocalDateTime.now());
        tx.setFromAccount(account);

        Transaction saved = transactionRepository.save(tx);

        return mapToDTO(saved);
    }

}
