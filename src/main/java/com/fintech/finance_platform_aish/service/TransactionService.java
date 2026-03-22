
package com.fintech.finance_platform_aish.service;

import com.fintech.finance_platform_aish.dto.AccountResponseDTO;
import com.fintech.finance_platform_aish.dto.TransactionResponseDTO;
import com.fintech.finance_platform_aish.entity.*;
import com.fintech.finance_platform_aish.repository.AccountRepository;
import com.fintech.finance_platform_aish.repository.CardRepository;
import com.fintech.finance_platform_aish.repository.LedgerRepository;
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
import java.util.Optional;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final CardRepository cardRepository;
    private final LedgerRepository ledgerRepository;

    public TransactionService(TransactionRepository transactionRepository,
                              AccountRepository accountRepository, CardRepository cardRepository, LedgerRepository ledgerRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.cardRepository = cardRepository;
        this.ledgerRepository = ledgerRepository;
    }

    @Transactional
    public TransactionResponseDTO transferMoney(Long fromAccountId,
                                                Long toAccountId,
                                                BigDecimal amount,
                                                String idempotencyKey) {



        // 1. Check if already exists
        Optional<Transaction> existingTxn =
                transactionRepository.findByIdempotencyKey(idempotencyKey);

        if (existingTxn.isPresent()) {
            // 👉 Return old response (NO duplicate processing)
            System.out.println("idempotency key exists");
            return mapToDTO(existingTxn.get());
        }

        System.out.println("idempotency key not exists");

        Account fromAccount = accountRepository.findAccountForUpdate(fromAccountId)
                .orElseThrow(() -> new RuntimeException("Sender account not found"));

        Account toAccount = accountRepository.findAccountForUpdate(toAccountId)
                .orElseThrow(() -> new RuntimeException("Receiver account not found"));

        // 2. Create transaction with PENDING
        Transaction txn = new Transaction();
        txn.setAmount(amount);
        txn.setTransactionType(TransactionType.TRANSFER);
        txn.setTransactionStatus(TransactionStatus.PENDING);
        txn.setTimestamp(LocalDateTime.now());
        txn.setReferenceId(java.util.UUID.randomUUID().toString());
        txn.setFromAccount(fromAccount);
        txn.setToAccount(toAccount);
        txn.setIdempotencyKey(idempotencyKey);

        transactionRepository.save(txn);

        try {

            validateTransaction(fromAccount, toAccount, amount);

            if (fromAccount.getBalance().compareTo(amount) < 0) {
                throw new RuntimeException("Insufficient balance");
            }

            // 1. DEBIT entry
            LedgerEntry debit = new LedgerEntry();
            debit.setAccount(fromAccount);
            debit.setAmount(amount);
            debit.setEntryType(EntryType.DEBIT);
            debit.setTimestamp(LocalDateTime.now());
            debit.setTransaction(txn);

            // 2. CREDIT entry
            LedgerEntry credit = new LedgerEntry();
            credit.setAccount(toAccount);
            credit.setAmount(amount);
            credit.setEntryType(EntryType.CREDIT);
            credit.setTimestamp(LocalDateTime.now());
            credit.setTransaction(txn);

            ledgerRepository.save(debit);
            ledgerRepository.save(credit);

            // debit sender
            fromAccount.setBalance(fromAccount.getBalance().subtract(amount));

            // credit receiver
            toAccount.setBalance(toAccount.getBalance().add(amount));


            accountRepository.save(fromAccount);
            accountRepository.save(toAccount);

            txn.setTransactionStatus(TransactionStatus.SUCCESS);

        }
        catch (Exception e){
            txn.setTransactionStatus(TransactionStatus.FAILED);
        }
        Transaction saved = transactionRepository.save(txn);

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
    public TransactionResponseDTO deposit(Long accountId, BigDecimal amount,String idempotencyKey) {


        // 1. Check if already exists
        Optional<Transaction> existingTxn =
                transactionRepository.findByIdempotencyKey(idempotencyKey);

        if (existingTxn.isPresent()) {
            // 👉 Return old response (NO duplicate processing)
            System.out.println("idempotency key exists");
            return mapToDTO(existingTxn.get());
        }

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionType.ADD_MONEY);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setToAccount(account);
        transaction.setTransactionStatus(TransactionStatus.PENDING);
        Transaction savedTx =  transactionRepository.save(transaction);

        try {
            account.setBalance(account.getBalance().add(amount));

            accountRepository.save(account);
            transaction.setTransactionStatus(TransactionStatus.SUCCESS);
        }
        catch (Exception e){
            transaction.setTransactionStatus(TransactionStatus.FAILED);
        }

        savedTx = transactionRepository.save(transaction);
        return mapToDTO(savedTx);
    }

    @Transactional
    public TransactionResponseDTO withdraw(Long accountId, BigDecimal amount,String idempotencyKey) {


        // 1. Check if already exists
        Optional<Transaction> existingTxn =
                transactionRepository.findByIdempotencyKey(idempotencyKey);

        if (existingTxn.isPresent()) {
            // 👉 Return old response (NO duplicate processing)
            System.out.println("idempotency key exists");
            return mapToDTO(existingTxn.get());
        }

        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionType.WITHDRAW);
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setFromAccount(account);
        transaction.setTransactionStatus(TransactionStatus.PENDING);

        Transaction savedTx = transactionRepository.save(transaction);

        try {

            if (account.getBalance().compareTo(amount) < 0) {
                throw new RuntimeException("Insufficient balance");
            }

            account.setBalance(account.getBalance().subtract(amount));

            accountRepository.save(account);

            transaction.setTransactionStatus(TransactionStatus.SUCCESS);

        } catch (Exception e) {
            transaction.setTransactionStatus(TransactionStatus.FAILED);
        }


        savedTx = transactionRepository.save(transaction);

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
        tx.setTransactionType(TransactionType.CARD);
        tx.setTimestamp(LocalDateTime.now());
        tx.setFromAccount(account);

        Transaction saved = transactionRepository.save(tx);

        return mapToDTO(saved);
    }

}
